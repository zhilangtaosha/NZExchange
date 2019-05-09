package com.nze.nzexchange.controller.bibi


import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.*
import com.jakewharton.rxbinding2.widget.RxTextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.tool.NLog
import com.nze.nzeframework.widget.basepopup.BasePopupWindow
import com.nze.nzexchange.NzeApp
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.*
import com.nze.nzexchange.bean.OrderPendBean.Companion.orderPending
import com.nze.nzexchange.bean.RestOrderBean.Companion.getPendingOrderInfo
import com.nze.nzexchange.config.EventCode
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.config.KLineParam
import com.nze.nzexchange.controller.base.NBaseFragment
import com.nze.nzexchange.controller.common.CommonListPopup
import com.nze.nzexchange.controller.login.LoginActivity
import com.nze.nzexchange.controller.market.KLineActivity
import com.nze.nzexchange.extend.*
import com.nze.nzexchange.tools.editjudge.EditTextEmptyWatcher
import com.nze.nzexchange.tools.editjudge.EditTextJudgeNumberWatcher
import com.nze.nzexchange.widget.LinearLayoutAsListView
import com.nze.nzexchange.widget.indicatorseekbar.IndicatorSeekBar
import com.nze.nzexchange.widget.indicatorseekbar.OnSeekChangeListener
import com.nze.nzexchange.widget.indicatorseekbar.SeekParams
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_bibi.view.*

class BibiFragment : NBaseFragment(), View.OnClickListener, CommonListPopup.OnListPopupItemClick, OnSeekChangeListener {


    lateinit var rootView: View
    val svRoot: LinearLayout by lazy { rootView.root_sv_fb }
    val moreTv: TextView by lazy { rootView.more_bibi }
    val klineIv: ImageView by lazy { rootView.iv_kline_bibi }
    val buyTv: TextView by lazy { rootView.tv_buy_bibi }
    val saleTv: TextView by lazy { rootView.tv_sale_bibi }
    val limitTv: TextView by lazy { rootView.tv_limit_bibi }
    val giveEt: EditText by lazy {
        rootView.et_give_bibi.apply {
            setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    val content = this.getContent()
                    if (content.isEmpty())
                        this.setText("0.01")
                }
            }
        }
    }
    //    val giveUnitTv: TextView by lazy { rootView.tv_give_unit_bibi }
    val priceTv: TextView by lazy { rootView.tv_price_bibi }
    val getEt: EditText by lazy {
        rootView.et_get_bibi.apply {
            addTextChangedListener(EditTextJudgeNumberWatcher(this))
        }
    }
    //    val getUnitTv: TextView by lazy { rootView.tv_get_unit_bibi }
    val availableTv: TextView by lazy { rootView.tv_available_bibi }
    val seekbarValueTv: TextView by lazy { rootView.tv_seekbar_value_bibi }
    val buyIsb: IndicatorSeekBar by lazy {
        rootView.isb_buy_bibi
    }
    val saleIsb: IndicatorSeekBar by lazy {
        rootView.isb_sale_bibi
    }
    val totalTransactionTv: TextView by lazy { rootView.tv_total_transaction_bibi }
    val transactionBtn: Button by lazy { rootView.btn_transaction_bibi }
    val handicapSaleLv: LinearLayoutAsListView by lazy { rootView.lv_handicap_sale_bibi }
    val lastCostTv: TextView by lazy { rootView.tv_last_cost_bibi }
    val lastPriceTv: TextView by lazy { rootView.tv_last_price_bibi }
    val handicapBuyLv: LinearLayoutAsListView by lazy { rootView.lv_handicap_buy_bibi }
    val depthTv: TextView by lazy { rootView.tv_depth_bibi }
    val allOrderTv: TextView by lazy { rootView.tv_all_order_bibi }
    val currentOrderLv: LinearLayoutAsListView by lazy { rootView.lv_current_order_bibi }
    val handicapSaleAdapter by lazy {
        HandicapAdapter(activity!!, HandicapAdapter.SALE).apply {
            onHandicapItemClick = {
                giveEt.setText(it.cost.toString())
            }
        }
    }
    val handicapBuyAdapter by lazy {
        HandicapAdapter(activity!!, HandicapAdapter.BUY).apply {
            onHandicapItemClick = {
                giveEt.setText(it.cost.toString())
            }
        }
    }
    val currentOrderAdapter by lazy {
        BibiCurentOrderAdapter(activity!!).apply {
            cancelClick = { position, item ->
                OrderPendBean.cancelOrder(item.id, item.userId, currentTransactionPair?.id!!)
                        .compose(netTfWithDialog())
                        .subscribe({
                            if (it.success) {
                                orderPending(currentTransactionPair?.id!!, userBean?.userId!!)
                            }
                        }, onError)
            }
        }
    }

    val TYPE_BUY = 1
    val TYPE_SALE = 0
    var currentType = TYPE_BUY

    private val sidePopup: BibiSidePopup by lazy { BibiSidePopup(mBaseActivity, childFragmentManager!!) }
    private var currentTransactionPair: TransactionPairsBean? = null
    private var preCurrentTransactionPair: TransactionPairsBean? = null
    private var restOrderBean: RestOrderBean? = null

    val POPUP_LIMIT = 0
    val POPUP_DEPTH = 1
    var currentPopupType = POPUP_LIMIT
    private val itemLimit: MutableList<String> by lazy {
        mutableListOf<String>().apply {
            add("限价")
            add("市价")
        }
    }
    private val limitPopup: CommonListPopup by lazy {
        CommonListPopup(activity, POPUP_LIMIT).apply {
            onItemClick = this@BibiFragment
            setOnBeforeShowCallback { popupRootView, anchorView, hasShowAnima ->
                limitTv.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context!!, R.mipmap.close_icon2), null)
                true
            }
            onDismissListener = object : BasePopupWindow.OnDismissListener() {
                override fun onDismiss() {
                    limitTv.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context!!, R.mipmap.open_icon2), null)
                }
            }
            addAllItem(itemLimit)
        }
    }
    private final val TRANSACTIONTYPE_LIMIT = 0
    private final val TRANSACTIONTYPE_MARKET = 1
    private var transactionType = TRANSACTIONTYPE_LIMIT

    private var userBean: UserBean? = NzeApp.instance.userBean


    private val itemDepth: MutableList<String> = mutableListOf<String>("1", "2", "3", "4", "5", "6")
    private val depthPopup: CommonListPopup by lazy {
        CommonListPopup(activity, POPUP_DEPTH).apply {
            onItemClick = this@BibiFragment
            setOnBeforeShowCallback { popupRootView, anchorView, hasShowAnima ->
                depthTv.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context!!, R.mipmap.close_icon2), null)
                true
            }
            onDismissListener = object : BasePopupWindow.OnDismissListener() {
                override fun onDismiss() {
                    depthTv.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context!!, R.mipmap.open_icon2), null)
                }
            }
            addAllItem(itemDepth)
        }
    }


    var binder: KLineService.KBinder? = null
    var isBinder = false


    companion object {
        @JvmStatic
        fun newInstance() = BibiFragment().apply {
            onFirstRequest()
        }
    }

    override fun getRootView(): Int = R.layout.fragment_bibi

    override fun initView(rootView: View) {
        this.rootView = rootView
        moreTv.setOnClickListener(this)
        buyTv.setOnClickListener(this)
        saleTv.setOnClickListener(this)
        allOrderTv.setOnClickListener(this)
        limitTv.setOnClickListener(this)
        depthTv.setOnClickListener(this)
        transactionBtn.setShakeClickListener(this)
        klineIv.setOnClickListener(this)

        buyIsb.onSeekChangeListener = this
        saleIsb.onSeekChangeListener = this

//        handicapSaleAdapter.group = HandicapBean.getList()
//        handicapBuyAdapter.group = HandicapBean.getList()
//        handicapSaleLv.adapter = handicapSaleAdapter
//        handicapBuyLv.adapter = handicapBuyAdapter

        currentOrderLv.adapter = currentOrderAdapter


        RxTextView.textChanges(getEt)
                .subscribe {
                    if (it.isNotEmpty() && transactionType == TRANSACTIONTYPE_LIMIT) {
                        val input = it.toString().toDouble()
                        val price = giveEt.getContent().toDouble()
                        val total = input * price
                        if (restOrderBean != null && restOrderBean!!.mainCurrency != null && input != 0.0) {
                            if (currentType == TYPE_BUY) {
//                                var rate = (total / restOrderBean!!.mainCurrency!!.available) * 100
//                                seekbarValueTv.text = "${rate.retain2()}%"
//                                buyIsb.setProgress(rate.toFloat())

                            } else {
//                                var rate = (input / restOrderBean!!.currency!!.available) * 100
//                                seekbarValueTv.text = "${rate.retain2()}%"
//                                saleIsb.setProgress(rate.toFloat())
                            }
                            totalTransactionTv.text = "交易额 $total ${currentTransactionPair?.mainCurrency}"

                        }
                    }

                }

        getTransactionPair()
    }

    /**
     * 切换卖出和买入界面
     */
    private fun switchType(type: Int) {
        currentType = type
        if (type == TYPE_BUY) {
            buyTv.isSelected = true
            saleTv.isSelected = false
            availableTv.setTextFromHtml("可用<font color=\"#0DA287\">${restOrderBean?.mainCurrency?.available
                    ?: "--"}${currentTransactionPair?.mainCurrency ?: "--"}</font>")
            buyIsb.visibility = View.VISIBLE
            saleIsb.visibility = View.GONE
            transactionBtn.setBgByDrawable(ContextCompat.getDrawable(activity!!, R.drawable.selector_btn_9d81_bg)!!)
            transactionBtn.text = "买入${currentTransactionPair!!.currency}"
            seekbarValueTv.text = "${buyIsb.progress}%"
            saleIsb.setProgress(0F)
        } else {
            buyTv.isSelected = false
            saleTv.isSelected = true
            availableTv.setTextFromHtml("可用<font color=\"#FF4A5F\">${restOrderBean?.currency?.available
                    ?: "--"}${currentTransactionPair?.currency ?: "--"}</font>")
            buyIsb.visibility = View.GONE
            saleIsb.visibility = View.VISIBLE
            transactionBtn.setBgByDrawable(ContextCompat.getDrawable(activity!!, R.drawable.selector_btn_4a5f_bg)!!)
            transactionBtn.text = "卖出${currentTransactionPair!!.currency}"
            seekbarValueTv.text = "${saleIsb.progress}%"
            buyIsb.setProgress(0F)
        }
        getEt.setText("")
        if (userBean == null)
            transactionBtn.text = "登录"
    }

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {
        if (eventCenter.eventCode == EventCode.CODE_SELECT_TRANSACTIONPAIR) {
            NLog.i("bibifragment get select event....")
            preCurrentTransactionPair = currentTransactionPair
            currentTransactionPair = eventCenter.data as TransactionPairsBean
            refreshLayout()
            getPendingOrderInfo(currentTransactionPair?.id!!)
            orderPending(currentTransactionPair?.id!!, userBean?.userId)
            //切换交易对，切换盘口
            if (preCurrentTransactionPair == null || preCurrentTransactionPair?.id != currentTransactionPair?.id)
                changePair()
        }
        if (eventCenter.eventCode == EventCode.CODE_LOGIN_SUCCUSS) {
            userBean = UserBean.loadFromApp()
            getPendingOrderInfo(currentTransactionPair?.id!!)
            orderPending(currentTransactionPair?.id!!, userBean?.userId)
        }
        if (eventCenter.eventCode == EventCode.CODE_TRADE_BIBI) {
            val type: Int = eventCenter.data as Int
            switchType(type)
        }
        if (eventCenter.eventCode == EventCode.CODE_LOGOUT_SUCCESS) {
            userBean = UserBean.loadFromApp()
            restOrderBean = null
            switchType(currentType)
            currentOrderAdapter.clearGroup(false)
            currentOrderLv.adapter = currentOrderAdapter
        }
    }


    override fun isBindEventBusHere(): Boolean = true

    override fun isBindNetworkListener(): Boolean = false

    override fun onNetworkConnected(type: NetUtils.NetType) {
    }

    override fun onNetworkDisConnected() {
    }

    override fun getContainerTargetView(): View? = null

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.more_bibi -> {
//                if (mainCurrencyList.size > 0)
//                sidePopup.showPopupWindow()
                skipActivity(BibiSideActivity::class.java)
            }
            R.id.tv_buy_bibi -> {
                currentType = TYPE_BUY
                switchType(currentType)
            }
            R.id.tv_sale_bibi -> {
                currentType = TYPE_SALE
                switchType(currentType)
            }
            R.id.tv_all_order_bibi -> {
                if (userBean == null) {
                    skipActivity(LoginActivity::class.java)
                    return
                }
                BibiAllOrderActivity.toAllOrderActivity(mBaseActivity!!, BibiAllOrderActivity.FROM_BIBI)
            }
            R.id.tv_limit_bibi -> {
                currentPopupType = POPUP_LIMIT
                limitPopup.showPopupWindow()
            }
            R.id.tv_depth_bibi -> {
                currentPopupType = POPUP_DEPTH
                depthPopup.showPopupWindow()
            }
            R.id.btn_transaction_bibi -> {//买入卖出
                if (userBean == null) {
                    skipActivity(LoginActivity::class.java)
                    return
                }
                var price = 0.0
                if (transactionType == TRANSACTIONTYPE_LIMIT) {
                    if (!checkPrice()) return
                    price = giveEt.getContent().toDouble()
                }
                if (!checkNum()) {
                    showToast("请填写交易数量")
                    return
                }
                btnHandler(userBean!!, getEt.getContent().toDouble(), price)
            }
            R.id.iv_kline_bibi -> {
                startActivity(Intent(activity, KLineActivity::class.java).putExtra(IntentConstant.PARAM_TRANSACTION_PAIR, currentTransactionPair))
            }
        }
    }

    fun btnHandler(user: UserBean, number: Double, price: Double) {
        if (transactionType == TRANSACTIONTYPE_LIMIT) {//限价交易
            LimitTransactionBean.limitTransaction(currentType, user.userId, currentTransactionPair?.id!!, number, giveEt.getContent().toDouble())
                    .compose(netTfWithDialog())
                    .subscribe({
                        if (it.success) {
                            showToast("下单成功")
                            getPendingOrderInfo(currentTransactionPair?.id!!)
                            orderPending(currentTransactionPair?.id!!, userBean?.userId!!)
                        } else {
                            showToast(it.message)
                        }
                    }, onError)
        } else {//市价交易
            LimitTransactionBean.marketTransaction(currentType, user.userId, currentTransactionPair?.id!!, number)
                    .compose(netTfWithDialog())
                    .subscribe({
                        if (it.success) {
                            showToast("下单成功")
                            getPendingOrderInfo(currentTransactionPair?.id!!)
                            orderPending(currentTransactionPair?.id!!, userBean?.userId!!)
                        } else {
                            showToast(it.message)
                        }
                    }, onError)
        }
    }


    fun checkNum(): Boolean {
        val s = getEt.getContent()
        if (s.isNullOrEmpty()) {
            getEt.requestFocus()
            return false
        }
        return true
    }

    fun checkPrice(): Boolean {
        var s = giveEt.getContent()
        if (s.isNullOrEmpty()) {
            giveEt.requestFocus()
            return false
        }

        return true
    }

    /**
     * 市价和限价转换
     */
    override fun clickItem(position: Int, item: String) {
        if (currentPopupType == POPUP_LIMIT) {
            limitTv.text = item
            transactionType = if (position == 0) {
                giveEt.isFocusable = true
                giveEt.isFocusableInTouchMode = true
                giveEt.hint = "价格(${currentTransactionPair?.mainCurrency})"
                TRANSACTIONTYPE_LIMIT
            } else {
                giveEt.isFocusable = false
                giveEt.setText("")
                giveEt.hint = "以当前最优惠价格交易"
                TRANSACTIONTYPE_MARKET
            }
        } else {
            depthTv.text = "深度$item"
        }
    }

    //----------------------seekbar监听-------------------------
    override fun onSeeking(seekParams: SeekParams?) {
        if (transactionType == TRANSACTIONTYPE_LIMIT) {
            if (!getEt.hasFocus())
                getEt.requestFocus()
            val progress = seekParams?.progress
            seekbarValueTv.text = "${progress}%"
            val price = giveEt.getContent().toDouble()
            if (currentType == TYPE_BUY) {
                if (restOrderBean != null && restOrderBean!!.mainCurrency != null) {
                    val total = restOrderBean!!.mainCurrency!!.available * progress!! / 100
                    val input = total / price
                    val s = input.retain4()
                    getEt.setText(s)
                    getEt.setSelection(s.length)
                }
            } else {
                if (restOrderBean != null && restOrderBean!!.currency != null) {
                    val total = restOrderBean!!.currency!!.available * progress!! / 100
                    val s = total.retain4()
                    getEt.setText(s)
                    getEt.setSelection(s.length)
                }
            }
        }
    }

    override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {
    }

    override fun onFirstRequest() {


    }

    private fun refreshLayout() {
        currentTransactionPair?.let {
            //            switchType(currentType)
            moreTv.text = it.transactionPair
            giveEt.hint = "价格(${it.mainCurrency})"
            giveEt.setText(it.exchangeRate.formatForCurrency())
            getEt.hint = "数量(${it.currency})"

            currentOrderAdapter.mainCurrency = it.mainCurrency
            currentOrderAdapter.currency = it.currency

        }

    }

    //获取挂单信息
    private fun getPendingOrderInfo(currencyId: String) {
        RestOrderBean.getPendingOrderInfo(currencyId, userBean?.userId)
                .compose(netTfWithDialog())
                .subscribe({
                    if (it.success) {
                        restOrderBean = it.result
                        switchType(currentType)
                    }
                }, onError)
    }

    /**
     * 获取所有计价货币，取第一个计价货币的第一个交易对作为默认交易对
     */
    private fun getTransactionPair() {
        TransactionPairsBean.getAllTransactionPairs()
                .compose(netTfWithDialog())
                .flatMap {
                    if (it.success) {
                        val list = it.result
                        if (list != null && list.size > 0) {
                            //获取第一个计价货币的所有交易对
                            TransactionPairsBean.getTransactionPairs(list[0].mainCurrency, userBean?.userId?.getValue())
                                    .subscribeOn(Schedulers.io())
                        } else {
                            Flowable.error<String>(Throwable("没有交易对"))
                        }
                    } else {
                        Flowable.error<String>(Throwable("请求失败"))
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap {
                    it as Result<MutableList<TransactionPairsBean>>
                    if (it.success) {
                        currentTransactionPair = it.result[0]
                        refreshLayout()
                        if (userBean != null) {
                            //获取订单
                            orderPending(currentTransactionPair?.id!!, userBean?.userId!!)
                        }
                        //获取盘口
                        getKData()
                        //获取交易对的挂单信息
                        RestOrderBean.getPendingOrderInfo(currentTransactionPair?.id!!, userBean?.userId)
                                .subscribeOn(Schedulers.io())
                    } else {
                        Flowable.error<String>(Throwable("请求失败"))
                    }

                }.observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it as Result<RestOrderBean>
                    if (it.success) {
                        restOrderBean = it.result
                        switchType(currentType)
                    }
                }, onError)

    }

    /**
     * 获取当前委托
     */
    private fun orderPending(currencyId: String, userId: String?) {
        OrderPendBean.orderPending(currencyId, userId)
                .compose(netTf())
                .subscribe({
                    if (it.success) {
                        currentOrderAdapter.group = it.result
                        currentOrderLv.adapter = currentOrderAdapter
                    }
                }, onError)
    }

    private fun getKData() {
        binder?.getKDataRequest(currentTransactionPair!!)
    }


    override fun onResume() {
        super.onResume()
        activity!!.bindService(Intent(activity, KLineService::class.java), connection, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        super.onDestroy()
        binder?.close()
        activity!!.unbindService(connection)
    }

    fun changePair() {
        handicapBuyAdapter.clearGroup(true)
        handicapBuyLv.adapter = handicapBuyAdapter
        handicapSaleAdapter.clearGroup(true)
        handicapSaleLv.adapter = handicapSaleAdapter
        binder?.changePair(currentTransactionPair!!)
    }

    val connection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            Log.i("zwy", "onServiceDisconnected")
            isBinder = false
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.i("zwy", "onServiceConnected")
            binder = service as KLineService.KBinder?
            isBinder = true
            binder?.initKSocket(KLineParam.MARKET_MYSELF) { lineK: LineKBean?, handicap: Handicap?, latestDeal: List<NewDealBean>?, quotes: Array<String>?, depth: Depth? ->
                if (handicap != null) {
                    val buyList = mutableListOf<HandicapBean>()
                    val saleList = mutableListOf<HandicapBean>()
                    handicap?.asks?.forEachIndexed { index, strings ->
                        saleList.add(HandicapBean(index + 1, strings[0], strings[1], ""))
                    }
                    handicap?.bids?.forEachIndexed { index, strings ->
                        buyList.add(HandicapBean(index + 1, strings[0], strings[1], ""))
                    }
                    handicapBuyAdapter.group = buyList.take(5).toMutableList()
                    handicapBuyLv.adapter = handicapBuyAdapter
                    handicapSaleAdapter.group = saleList.take(5).toMutableList()
                    handicapSaleLv.adapter = handicapSaleAdapter
                }
            }
        }
    }
}
