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
import com.nze.nzexchange.config.EventCode
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.config.KLineParam
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.base.NBaseFragment
import com.nze.nzexchange.controller.common.*
import com.nze.nzexchange.controller.common.presenter.CommonBibiP
import com.nze.nzexchange.controller.login.LoginActivity
import com.nze.nzexchange.controller.market.KLineActivity
import com.nze.nzexchange.database.dao.impl.SoketPairDaoImpl
import com.nze.nzexchange.extend.*
import com.nze.nzexchange.tools.DecimalDigitTool
import com.nze.nzexchange.tools.DoubleMath
import com.nze.nzexchange.tools.editjudge.EditCurrencyPriceWatcher
import com.nze.nzexchange.tools.editjudge.EditTextJudgeNumberWatcher
import com.nze.nzexchange.widget.LinearLayoutAsListView
import com.nze.nzexchange.widget.indicatorseekbar.IndicatorSeekBar
import com.nze.nzexchange.widget.indicatorseekbar.OnSeekChangeListener
import com.nze.nzexchange.widget.indicatorseekbar.SeekParams
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_bibi.view.*
import org.jetbrains.annotations.Nls

class BibiFragment : NBaseFragment(), View.OnClickListener, CommonListPopup.OnListPopupItemClick, OnSeekChangeListener {


    lateinit var rootView: View
    val tradeLayout: RelativeLayout by lazy { rootView.layout_trade_bibi }
    val svRoot: LinearLayout by lazy { rootView.root_sv_fb }
    val moreTv: TextView by lazy { rootView.more_bibi }
    val klineIv: ImageView by lazy { rootView.iv_kline_bibi }
    val buyTv: TextView by lazy { rootView.tv_buy_bibi }
    val saleTv: TextView by lazy { rootView.tv_sale_bibi }
    val limitTv: TextView by lazy { rootView.tv_limit_bibi }
    val giveReduceTv: TextView by lazy { rootView.tv_give_reduce_bibi }
    val giveAddTv: TextView by lazy { rootView.tv_give_add_bibi }
    val getReduceTv: TextView by lazy { rootView.tv_get_reduce_bibi }
    val getAddTv: TextView by lazy { rootView.tv_get_add_bibi }
    val giveEt: EditText by lazy {
        rootView.et_give_bibi
    }
    //    val giveUnitTv: TextView by lazy { rootView.tv_give_unit_bibi }
    val priceTv: TextView by lazy { rootView.tv_price_bibi }
    val getEt: EditText by lazy {
        rootView.et_get_bibi
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
                giveEt.setText(it.cost.toDouble().format(DecimalDigitTool.getDigit(currentTransactionPair?.moneyPrec
                        ?: 8)))
            }
        }
    }
    val handicapBuyAdapter by lazy {
        HandicapAdapter(activity!!, HandicapAdapter.BUY).apply {
            onHandicapItemClick = {
                giveEt.setText(it.cost.toDouble().format(DecimalDigitTool.getDigit(currentTransactionPair?.moneyPrec
                        ?: 8)))
            }
        }
    }
    val currentOrderAdapter by lazy {
        BibiCurrentOrderAdapter(activity!!).apply {
            cancelClick = { position, item ->
                binder?.orderCancel("${currentTransactionPair?.currency}${currentTransactionPair?.mainCurrency}", item.id)
            }
        }
    }

    val TYPE_BUY = 2
    val TYPE_SALE = 1
    var currentType = TYPE_BUY

    private val sidePopup: BibiSidePopup by lazy { BibiSidePopup(mBaseActivity, childFragmentManager!!) }
    private var currentTransactionPair: TransactionPairsBean? = null
    private var preCurrentTransactionPair: TransactionPairsBean? = null
    private var restOrderBean: RestOrderBean? = null

    val POPUP_LIMIT = 0//限价和市价的popup
    val POPUP_DEPTH = 1//深度popup
    var currentPopupType = POPUP_LIMIT//弹出的popup类型
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
    private var transactionType = TRANSACTIONTYPE_LIMIT//交易类型：限价和市价

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


    var binder: SoketService.SoketBinder? = null
    var isBinder = false

    val fundPopup: FundPasswordPopup by lazy {
        FundPasswordPopup(activity!!).apply {
            onPasswordClick = {
                btnHandler(userBean!!, getEt.getContent().toDouble(), if (!giveEt.getContent().isNullOrEmpty()) {
                    giveEt.getContent().toDouble()
                } else {
                    0.0
                }, it)
            }
        }
    }

    val currentOrderList: MutableList<SoketOrderBean> by lazy { mutableListOf<SoketOrderBean>() }
    val mAssetMap = hashMapOf<String, SoketAssetBean>()


    companion object {
        @JvmStatic
        fun newInstance() = BibiFragment().apply {
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
        lastCostTv.setOnClickListener(this)
        giveReduceTv.setOnClickListener(onGiveActionClick)
        giveAddTv.setOnClickListener(onGiveActionClick)
        getReduceTv.setOnClickListener(onGetActionClick)
        getAddTv.setOnClickListener(onGetActionClick)

        buyIsb.onSeekChangeListener = this
        saleIsb.onSeekChangeListener = this
        giveEt.addTextChangedListener(EditCurrencyPriceWatcher(giveEt))

        currentOrderLv.adapter = currentOrderAdapter

        RxTextView.textChanges(giveEt)
                .subscribe {
                    isGiveChangeRate = !isGiveClick
                    isGiveClick = false
                    val get = getEt.getContent()
                    if (it.isNotEmpty()) {
                        val price = it.toString().toDouble()
                        priceTv.text = "≈${price.mul(mainCurrencyPrice).formatForLegal()}CNY"
                    } else {
                        priceTv.text = "≈0CNY"
                    }
                    if (it.isNotEmpty() && get.isNotEmpty() && transactionType == TRANSACTIONTYPE_LIMIT) {
                        val input = it.toString().toDouble()
                        val num = get.toDouble()
                        val total = DoubleMath.mul(input, num)
                        totalTransactionTv.text = "${getString(R.string.total_money)} ${total.formatForCurrency()} ${currentTransactionPair?.mainCurrency}"
                    } else if ((it.isNullOrEmpty() || get.isNullOrEmpty()) && transactionType == TRANSACTIONTYPE_LIMIT) {
                        totalTransactionTv.text = "${getString(R.string.total_money)} 0 ${currentTransactionPair?.mainCurrency?.getValue()
                                ?: ""}"
                    }
                }

        RxTextView.textChanges(getEt)
                .subscribe {
                    isGetChangeRate = !isGetClick
                    isGetClick = false
                    val give = giveEt.getContent()
                    if (it.isNotEmpty() && give.isNotEmpty() && transactionType == TRANSACTIONTYPE_LIMIT) {
                        val input = it.toString().toDouble()
                        val price = give.toDouble()
                        val total = DoubleMath.mul(input, price)
                        totalTransactionTv.text = "${getString(R.string.total_money)} ${total.formatForCurrency()} ${currentTransactionPair?.mainCurrency}"
                    } else if ((it.isNullOrEmpty() || give.isNullOrEmpty()) && transactionType == TRANSACTIONTYPE_LIMIT) {
                        totalTransactionTv.text = "${getString(R.string.total_money)} 0${currentTransactionPair?.mainCurrency?.getValue()
                                ?: ""}"
                    } else if (it.isNotEmpty() && transactionType == TRANSACTIONTYPE_MARKET) {
                        val input = it.toString().toDouble()
                        totalTransactionTv.text = "${getString(R.string.total_money)} ${input.formatForCurrency()} ${currentTransactionPair?.mainCurrency}"
                    }

                }

//        getTransactionPair()
    }

    /**
     * 切换卖出和买入界面
     */
    private fun switchType(type: Int) {
        currentType = type
        if (type == TYPE_BUY) {
            buyTv.isSelected = true
            saleTv.isSelected = false
//            availableTv.setTextFromHtml("可用<font color=\"#0DA287\">${mAssetMap[currentTransactionPair?.mainCurrency]?.available?.formatForCurrency()
//                    ?: "--"}${currentTransactionPair?.mainCurrency ?: "--"}</font>")
            buyIsb.visibility = View.VISIBLE
            saleIsb.visibility = View.GONE
            transactionBtn.setBgByDrawable(ContextCompat.getDrawable(activity!!, R.drawable.selector_btn_9d81_bg)!!)
            transactionBtn.text = "${getString(R.string.buy)}(${currentTransactionPair!!.currency})"
            seekbarValueTv.text = "${buyIsb.progress}%"
            saleIsb.setProgress(0F)
            if (transactionType == TRANSACTIONTYPE_LIMIT) {
                getEt.hint = "${getString(R.string.amount)}(${currentTransactionPair?.currency})"
            } else {
                getEt.hint = "${getString(R.string.total_money)}(${currentTransactionPair?.mainCurrency})"
            }
        } else {
            buyTv.isSelected = false
            saleTv.isSelected = true
//            availableTv.setTextFromHtml("可用<font color=\"#FF4A5F\">${mAssetMap[currentTransactionPair?.currency]?.available?.formatForCurrency()
//                    ?: "--"}${currentTransactionPair?.currency ?: "--"}</font>")
            buyIsb.visibility = View.GONE
            saleIsb.visibility = View.VISIBLE
            transactionBtn.setBgByDrawable(ContextCompat.getDrawable(activity!!, R.drawable.selector_btn_4a5f_bg)!!)
            transactionBtn.text = "${getString(R.string.sell)}(${currentTransactionPair!!.currency})"
            seekbarValueTv.text = "${saleIsb.progress}%"
            buyIsb.setProgress(0F)
            getEt.hint = "${getString(R.string.amount)}(${currentTransactionPair?.currency})"
        }
        refreshAsset()


        getEt.setText("")
        buyIsb.setProgress(0f)
        if (userBean == null)
            transactionBtn.text = "${getString(R.string.login)}"
    }

    fun refreshAsset() {
        if (currentType == TYPE_BUY) {
            availableTv.setTextFromHtml("${getString(R.string.available)}<font color=\"#0DA287\">${mAssetMap[currentTransactionPair?.mainCurrency]?.available?.formatForCurrency()
                    ?: "--"}${currentTransactionPair?.mainCurrency ?: "--"}</font>")
        } else {
            availableTv.setTextFromHtml("${getString(R.string.available)}<font color=\"#FF4A5F\">${mAssetMap[currentTransactionPair?.currency]?.available?.formatForCurrency()
                    ?: "--"}${currentTransactionPair?.currency ?: "--"}</font>")
        }
    }

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {
        if (eventCenter.eventCode == EventCode.CODE_SELECT_TRANSACTIONPAIR) {
            NLog.i("bibifragment get select event....")
            preCurrentTransactionPair = currentTransactionPair
            currentTransactionPair = eventCenter.data as TransactionPairsBean
            refreshLayout()
            getPendingOrderInfo(currentTransactionPair?.id!!)
            if (userBean != null) {
                queryCurrentOrder()
                queryAsset()
                binder?.subscribeOrder("${currentTransactionPair?.currency}${currentTransactionPair?.mainCurrency}")
            }
//                orderPending(currentTransactionPair?.id!!, userBean?.userId)
            //切换交易对，切换盘口
            if (preCurrentTransactionPair == null || preCurrentTransactionPair?.id != currentTransactionPair?.id)
                changePair()

        }
        if (eventCenter.eventCode == EventCode.CODE_LOGIN_SUCCUSS) {
            userBean = UserBean.loadFromApp()
            getPendingOrderInfo(currentTransactionPair?.id!!)
//            orderPending(currentTransactionPair?.id!!, userBean?.userId)
            queryAsset()
            binder?.subscribeOrder("${currentTransactionPair?.currency}${currentTransactionPair?.mainCurrency}")
        }
        if (eventCenter.eventCode == EventCode.CODE_TRADE_BIBI) {
            val type: Int = eventCenter.data as Int
            switchType(type)
        }
        if (eventCenter.eventCode == EventCode.CODE_LOGOUT_SUCCESS) {
            NLog.i("bibi 退出登录")
            userBean = null
            restOrderBean = null
            mAssetMap.clear()
            switchType(currentType)
            showNODataView("当前没有委托")
//            currentOrderLv.adapter = currentOrderAdapter
        }
    }


    override fun isBindEventBusHere(): Boolean = true

    override fun isBindNetworkListener(): Boolean = false

    override fun onNetworkConnected(type: NetUtils.NetType) {
    }

    override fun onNetworkDisConnected() {
    }

    override fun getContainerTargetView(): View? = currentOrderLv

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.more_bibi -> {
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
                BibiAllOrderActivity.toAllOrderActivity(mBaseActivity!!, BibiAllOrderActivity.FROM_BIBI, "${currentTransactionPair?.currency}${currentTransactionPair?.mainCurrency}")
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
                if (transactionType == TRANSACTIONTYPE_LIMIT) {
                    if (!checkPrice()) return
                }
                if (!checkNum()) {

                    return
                }
                CheckPermission.getInstance()
                        .commonCheck(activity as NBaseActivity, CheckPermission.BIBI_TRADE, "进行币币交易需要完成以下设置，请检查", onPass = {
                            //                            fundPopup.showPopupWindow()
                            btnHandler(userBean!!, getEt.getContent().toDouble(), if (!giveEt.getContent().isNullOrEmpty()) {
                                giveEt.getContent().toDouble()
                            } else {
                                0.0
                            }, "")
                        })

            }
            R.id.iv_kline_bibi -> {
                startActivity(Intent(activity, KLineActivity::class.java).putExtra(IntentConstant.PARAM_TRANSACTION_PAIR, currentTransactionPair))
            }

            R.id.tv_last_cost_bibi -> {
                giveEt.setText(lastCostTv.text.toString().toDouble().format(DecimalDigitTool.getDigit(currentTransactionPair?.moneyPrec
                        ?: 8)))
                priceTv.text = "≈${lastCostTv.text.toString().toDouble().mul(mainCurrencyPrice).formatForLegal()}CNY"

            }
        }
    }

    private var isGiveClick = false
    private var isGiveChangeRate = false
    private var giveRate = 0.0
    private var giveRulue = "0.############"
    private val onGiveActionClick = object : View.OnClickListener {
        override fun onClick(v: View?) {
            val give = giveEt.getContent()
            var price = 0.0
            if (!give.isNullOrEmpty()) {
                price = give.toDouble()
                if (isGiveChangeRate && give.contains(".")) {
                    val decimal = give.substring(give.indexOf(".") + 1, give.length)
                    var s = "0."
                    giveRulue = "0.0"
                    val len = decimal.length
                    if (len > 1) {
                        for (i in 0 until len - 1) {
                            s = "${s}0"
                            giveRulue = "${giveRulue}0"
                        }
                    }
                    s = "${s}1"
                    giveRate = s.toDouble()
                } else if (isGiveChangeRate) {
                    giveRate = 1.0
                    giveRulue = "0"
                }
            }
            when (v?.id) {
                R.id.tv_give_reduce_bibi -> {
                    price = price.sub(giveRate)
                }
                R.id.tv_give_add_bibi -> {
                    price = price.add(giveRate)
                }
            }
            if (price < 0.0)
                price = 0.0
            isGiveClick = true
            val s = price.format(giveRulue)
            giveEt.setText(s)
            giveEt.setSelection(s.length)
        }
    }

    private var isGetClick = false
    private var isGetChangeRate = false
    private var getRate = 0.0
    private var getRulue = "0.####"
    private val onGetActionClick = object : View.OnClickListener {
        override fun onClick(v: View?) {
            val get = getEt.getContent()
            NLog.i("get>>>$get")
            var num = 0.0
            if (!get.isNullOrEmpty()) {
                num = get.toDouble()
                NLog.i("条件 ${isGetChangeRate}&&${get.contains(".")}")
                if (isGetChangeRate && get.contains(".")) {
                    val decimal = get.substring(get.indexOf(".") + 1, get.length)
                    var s = "0."
                    getRulue = "0.0"
                    val len = decimal.length
                    if (len > 1) {
                        for (i in 0 until len - 1) {
                            s = "${s}0"
                            getRulue = "${getRulue}0"
                        }
                    }
                    s = "${s}1"
                    getRate = s.toDouble()
                } else if (isGetChangeRate) {
                    getRate = 1.0
                    getRulue = "0"
                }
            } else {
                getRate = 1.0
                getRulue = "0"
            }
            when (v?.id) {
                R.id.tv_get_reduce_bibi -> {
                    num = num.sub(getRate)
                }
                R.id.tv_get_add_bibi -> {
                    num = num.add(getRate)
                }
            }
            if (num < 0.0)
                num = 0.0
            NLog.i("num>>$num")
            isGetClick = true
            val s = num.format(getRulue)
            getEt.setText(s)
            getEt.setSelection(s.length)
        }
    }

    /**
     * 下单：买入和出售
     */
    fun btnHandler(user: UserBean, number: Double, price: Double, pwd: String) {
        if (transactionType == TRANSACTIONTYPE_LIMIT) {//限价交易
            binder?.limitDeal(currentTransactionPair!!.getPair(), currentType, number, price)
//            LimitTransactionBean.limitTransaction(currentType, user.userId, currentTransactionPair?.id!!, number, giveEt.getContent().toDouble(), user.tokenReqVo.tokenUserId, user.tokenReqVo.tokenUserKey, pwd)
//                    .compose(netTfWithDialog())
//                    .subscribe({
//                        if (it.success) {
//                            showToast("下单成功")
//                            getPendingOrderInfo(currentTransactionPair?.id!!)
////                            orderPending(currentTransactionPair?.id!!, userBean?.userId!!)
//                        } else {
//                            showToast(it.message)
//                        }
//                    }, onError)
        } else {//市价交易
            binder?.marketDeal(currentTransactionPair!!.getPair(), currentType, number)
//            LimitTransactionBean.marketTransaction(currentType, user.userId, currentTransactionPair?.id!!, number, user.tokenReqVo.tokenUserId, user.tokenReqVo.tokenUserKey, pwd)
//                    .compose(netTfWithDialog())
//                    .subscribe({
//                        if (it.success) {
//                            showToast("下单成功")
//                            getPendingOrderInfo(currentTransactionPair?.id!!)
////                            orderPending(currentTransactionPair?.id!!, userBean?.userId!!)
//                        } else {
//                            showToast(it.message)
//
//                        }
//                    }, onError)
        }
    }


    fun checkNum(): Boolean {
        val s = getEt.getContent()
        if (s.isNullOrEmpty() || (s.isNotEmpty() && s.toDouble() <= 0)) {
            showToast("请填写交易数量")
            getEt.requestFocus()
            return false
        }
        val amount = s.toDouble()
        if (amount < currentTransactionPair!!.minAmount) {
            showToast("最小交易数量是${currentTransactionPair!!.minAmount}")
            getEt.requestFocus()
            return false
        }
        if (transactionType == TRANSACTIONTYPE_LIMIT) {
            if (amount < currentTransactionPair!!.minAmount) {
                showToast("最小交易数量是${currentTransactionPair!!.minAmount}")
                getEt.requestFocus()
                return false
            }
        } else {
            if (currentType == TYPE_BUY) {
                if (amount > mAssetMap[currentTransactionPair!!.mainCurrency]!!.available) {
                    showToast("当前${currentTransactionPair!!.mainCurrency}可用余额为${mAssetMap[currentTransactionPair!!.mainCurrency]!!.available}")
                    getEt.requestFocus()
                    return false
                }
            } else {
                if (amount > mAssetMap[currentTransactionPair!!.currency]!!.available) {
                    showToast("当前${currentTransactionPair!!.currency}可用余额为${mAssetMap[currentTransactionPair!!.currency]!!.available}")
                    getEt.requestFocus()
                    return false
                }
            }
        }



        return true
    }

    fun checkPrice(): Boolean {
        var s = giveEt.getContent()
        if (s.isNullOrEmpty() || s.toDouble() <= 0.0) {
            giveEt.requestFocus()
            showToast("请输入价格")
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
                totalTransactionTv.visibility = View.VISIBLE
                priceTv.visibility = View.VISIBLE
                giveEt.isFocusable = true
                giveEt.isFocusableInTouchMode = true
                giveEt.hint = "${getString(R.string.price)}(${currentTransactionPair?.mainCurrency})"
                giveEt.setText(currentTransactionPair?.exchangeRate?.format(DecimalDigitTool.getDigit(currentTransactionPair?.moneyPrec
                        ?: 8)))
                giveReduceTv.visibility = View.VISIBLE
                giveAddTv.visibility = View.VISIBLE
                try {
                    val price = giveEt.getContent().toDouble()
                    val num = getEt.getContent().toDouble()
                    val total = price.mul(num)
                    totalTransactionTv.text = "${getString(R.string.total_money)}${total.formatForCurrency()}${currentTransactionPair?.mainCurrency}"
                } catch (e: Exception) {
                    totalTransactionTv.text = "${getString(R.string.total_money)}0${currentTransactionPair?.mainCurrency?.getValue()
                            ?: ""}"
                }
                if (currentType == TYPE_BUY) {
                    getEt.hint = "${getString(R.string.amount)}(${currentTransactionPair?.currency})"
                }
                TRANSACTIONTYPE_LIMIT
            } else {
                totalTransactionTv.visibility = View.INVISIBLE
                priceTv.visibility = View.INVISIBLE
                giveEt.isFocusable = false
                giveEt.setText("")
                giveEt.hint = "以当前最优惠价格交易"
                giveReduceTv.visibility = View.GONE
                giveAddTv.visibility = View.GONE

//                totalTransactionTv.text = "交易额--${currentTransactionPair?.mainCurrency}"
                if (currentType == TYPE_BUY) {
                    getEt.hint = "${getString(R.string.total_money)}(${currentTransactionPair?.mainCurrency})"
                }
                TRANSACTIONTYPE_MARKET
            }

            if (currentType == TYPE_BUY) {
                if (buyIsb.progress > 0) {
                    buyIsb.setProgress(0f)
                }
            } else {
                if (saleIsb.progress > 0) {
                    saleIsb.setProgress(0f)
                }
            }

        } else {
            depthTv.text = "${getString(R.string.depth)}$item"
            val pair = "${currentTransactionPair?.currency?.toUpperCase()}${currentTransactionPair?.mainCurrency?.toUpperCase()}"
            when (item) {
                "1" -> {
                    binder?.subscribeDepthAndToday(KLineParam.AMOUNT_DEPTH_5, KLineParam.DEPTH_1, pair)
                }
                "2" -> {
                    binder?.subscribeDepthAndToday(KLineParam.AMOUNT_DEPTH_5, KLineParam.DEPTH_2, pair)
                }
                "3" -> {
                    binder?.subscribeDepthAndToday(KLineParam.AMOUNT_DEPTH_5, KLineParam.DEPTH_3, pair)
                }
                "4" -> {
                    binder?.subscribeDepthAndToday(KLineParam.AMOUNT_DEPTH_5, KLineParam.DEPTH_4, pair)
                }
                "5" -> {
                    binder?.subscribeDepthAndToday(KLineParam.AMOUNT_DEPTH_5, KLineParam.DEPTH_5, pair)
                }
                "6" -> {
                    binder?.subscribeDepthAndToday(KLineParam.AMOUNT_DEPTH_5, KLineParam.DEPTH_6, pair)
                }
                "7" -> {
                    binder?.subscribeDepthAndToday(KLineParam.AMOUNT_DEPTH_5, KLineParam.DEPTH_7, pair)
                }
                "8" -> {
                    binder?.subscribeDepthAndToday(KLineParam.AMOUNT_DEPTH_5, KLineParam.DEPTH_8, pair)
                }
            }
        }
    }

    //----------------------seekbar监听-------------------------
    override fun onSeeking(seekParams: SeekParams?) {
        val progress = seekParams?.progress
        seekbarValueTv.text = "${progress}%"
        if (!getEt.hasFocus())
            getEt.requestFocus()
        if (currentType == TYPE_BUY) {
            if (transactionType == TRANSACTIONTYPE_LIMIT && mAssetMap.size > 0) {
                val give = giveEt.getContent()
                if (give.isNotEmpty()) {
                    val price = give.toDouble()
//                    val total = restOrderBean!!.mainCurrency!!.available * progress!! / 100
                    val total = mAssetMap[currentTransactionPair!!.mainCurrency]!!.available.mul(progress!!.toDouble()).divByFloor(100.toDouble(), 8)
                    if (price > 0) {
                        val input = DoubleMath.divByFloor(total, price, currentTransactionPair?.stockPrec
                                ?: 8).toString()
                        getEt.setText(input)
                        getEt.setSelection(input.length)
                    } else {
                        getEt.setText("0")
                        getEt.setSelection(1)
                    }
                }
            } else if (transactionType == TRANSACTIONTYPE_MARKET && mAssetMap.size > 0) {
//                val total = restOrderBean!!.mainCurrency!!.available * progress!! / 100
//                val s = total.retain4ByFloor()
                val s = mAssetMap[currentTransactionPair!!.mainCurrency]!!.available.mul(progress!!.toDouble()).divByFloor(100.toDouble(), currentTransactionPair?.stockPrec
                        ?: 8).toString()
                getEt.setText(s)
                getEt.setSelection(s.length)
            }
        } else {
            if (mAssetMap.size > 0) {
                val s = mAssetMap[currentTransactionPair!!.currency]!!.available.mul(progress!!.toDouble()).divByFloor(100.toDouble(), 8).formatForCurrency()
                getEt.setText(s)
                getEt.setSelection(s.length)
            }
        }

    }

    override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {
    }


    var giveWatcher: EditTextJudgeNumberWatcher? = null
    var getWatcher: EditTextJudgeNumberWatcher? = null
    private fun refreshLayout() {
        NLog.i("bibifragment refresh")
        currentTransactionPair?.let {
            //            switchType(currentType)
            moreTv.text = it.transactionPair
            if (transactionType == TRANSACTIONTYPE_LIMIT) {
                giveEt.hint = "${getString(R.string.price)}(${it.mainCurrency})"
                if (giveWatcher != null)
                    giveEt.removeTextChangedListener(giveWatcher)
                giveWatcher = EditTextJudgeNumberWatcher(giveEt, it.moneyPrec)
                giveEt.setText(it.exchangeRate.format(DecimalDigitTool.getDigit(it.moneyPrec)))
                giveEt.addTextChangedListener(giveWatcher)
                if (getWatcher != null)
                    getEt.removeTextChangedListener(getWatcher)
                getWatcher = EditTextJudgeNumberWatcher(getEt, it.stockPrec)
                getEt.addTextChangedListener(getWatcher)
            }
            if (transactionType == TRANSACTIONTYPE_LIMIT) {
                getEt.hint = "${getString(R.string.amount)}(${currentTransactionPair?.currency})"
            } else {
                getEt.hint = "${getString(R.string.total_money)}(${currentTransactionPair?.mainCurrency})"
            }
            currentOrderAdapter.mainCurrency = it.mainCurrency
            currentOrderAdapter.currency = it.currency
            if (currentType == TYPE_BUY) {
                availableTv.setTextFromHtml("${getString(R.string.available)}<font color=\"#0DA287\">--${it.mainCurrency}</font>")
            } else {
                availableTv.setTextFromHtml("${getString(R.string.available)}<font color=\"#FF4A5F\">--${it.currency}</font>")
            }

        }

    }

    private var mainCurrencyPrice: Double = 0.0
    //获取挂单信息
    private fun getPendingOrderInfo(currencyId: String) {
        CommonBibiP.getInstance(activity as NBaseActivity)
                .currencyToLegal(currentTransactionPair?.mainCurrency!!, 1.0, {
                    if (it.success) {
                        mainCurrencyPrice = it.result
                    } else {
                        priceTv.text = "≈0CNY"
                    }
                    val price = giveEt.getContent()
                    if (!price.isNullOrEmpty()) {
                        priceTv.text = "≈${price.toDouble().mul(mainCurrencyPrice).formatForLegal()}CNY"
                    } else {
                        priceTv.text = "≈0CNY"
                    }
                }, {
                    priceTv.text = "≈0CNY"
                })
    }

    private fun getKData() {
//        binder?.getKDataRequest(currentTransactionPair!!)
    }


    fun changePair() {
        depthTv.text = "${getString(R.string.depth)}1"
        lastCostTv.text = "0"
        lastPriceTv.text = "0CNY"
        handicapBuyAdapter.clearGroup(true)
        handicapBuyLv.adapter = handicapBuyAdapter
        handicapSaleAdapter.clearGroup(true)
        handicapSaleLv.adapter = handicapSaleAdapter
        binder?.subscribeDepthAndToday(KLineParam.AMOUNT_DEPTH_5, KLineParam.DEPTH_1, "${currentTransactionPair?.currency?.toUpperCase()}${currentTransactionPair?.mainCurrency?.toUpperCase()}")
    }

    val connection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            Log.i("zwy", "onServiceDisconnected")
            isBinder = false
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.i("zwy", "bibi onServiceConnected")
            binder = service as SoketService.SoketBinder
            isBinder = true

            addCallBack()
            addOrderCallBack()

            binder?.queryMarket("bibi")
        }
    }

    fun addCallBack() {
        binder?.addCallBack("bibi",
                {
                    //查询k线

                },
                {
                    //订阅k线

                },
                {
                    //订阅今日行情
                    NLog.i("today 今日行情")
                    lastCostTv.text = it.last
                    lastPriceTv.text = "≈${it.last.toDouble().mul(mainCurrencyPrice).formatForLegal()}CNY"
                },
                { mDepthBuyList, mDepthSellList ->
                    mDepthSellList.sortBy { it.price }
                    mDepthBuyList.sortByDescending { it.price }
                    //订阅深度
                    val buyList = mutableListOf<HandicapBean>()
                    val saleList = mutableListOf<HandicapBean>()
                    mDepthSellList.forEachIndexed { index, it ->
                        saleList.add(HandicapBean(index + 1, it.priceStr, it.volueStr, ""))
                    }
                    mDepthBuyList.forEachIndexed { index, it ->
                        buyList.add(HandicapBean(index + 1, it.priceStr, it.volueStr, ""))
                    }
                    handicapBuyAdapter.group = buyList.take(5).toMutableList()
                    handicapBuyLv.adapter = handicapBuyAdapter
                    handicapSaleAdapter.group = saleList.asReversed().takeLast(5).toMutableList()
                    handicapSaleLv.adapter = handicapSaleAdapter
                },
                {
                    //订阅最近成交列表

                })
        binder?.addMarketCallBack("bibi") {
            NLog.i("BibiFragment market resut")
            //获取所有交易对
            currentTransactionPair = TransactionPairsBean()
            currentTransactionPair!!.setValueFromRankBean(it[0].list[0])
            refreshLayout()
            getPendingOrderInfo(currentTransactionPair?.id!!)
            switchType(currentType)
            changePair()
            if (userBean != null) {
                queryCurrentOrder()
                queryAsset()
                binder?.subscribeOrder("${currentTransactionPair?.currency}${currentTransactionPair?.mainCurrency}")
            } else {
                showNODataView("当前没有登录")
            }

        }

        binder?.addLimitDealCallBack {
            //下限价单00
            if (it.success) {
                showToast("下单成功")
            } else {
                showToast(it.getMsg())
            }
        }
        binder?.addMarketDealCallBack {
            //下市价单
            if (it.success) {
                showToast("下单成功")
            } else {
                showToast(it.getMsg())
            }
        }

        binder?.addAssetCallBack("bibi", {
            mAssetMap.clear()
            mAssetMap.putAll(it)
            switchType(currentType)
            binder?.subscribeAsset(mutableListOf<String>().apply {
                add(currentTransactionPair!!.currency)
                add(currentTransactionPair!!.mainCurrency)
            })

        }, {
            mAssetMap.clear()
            mAssetMap.putAll(it)
            refreshAsset()
        })
    }

    fun addOrderCallBack() {
        binder?.addCurrentOrderCallBack("bibi", {
            NLog.i("订单查询")
            stopAllView()
            if (it.size > 0) {
                currentOrderList.addAll(it)
                currentOrderAdapter.group = currentOrderList
                currentOrderLv.adapter = currentOrderAdapter
            } else {
                showNODataView("当前没有委托单")
            }

        }, {
            NLog.i("订单更新")
            when (it.event) {
                SoketSubscribeOrderBean.EVENT_DEAL -> {
                    stopAllView()
                    currentOrderList.add(0, it.order)
                    currentOrderAdapter.addItem(0, it.order)
                    currentOrderLv.adapter = currentOrderAdapter
                }
                SoketSubscribeOrderBean.EVENT_UPDATE -> {
                    val i = currentOrderList.indexOfFirst { item ->
                        item.id == it.order.id
                    }
                    if (i >= 0) {
                        currentOrderList.set(i, it.order)
                        currentOrderAdapter.setItem(i, it.order)
                        currentOrderLv.adapter = currentOrderAdapter
                    }
                }
                SoketSubscribeOrderBean.EVENT_FINISH -> {
                    val i = currentOrderList.indexOfFirst { item ->
                        item.id == it.order.id
                    }
                    if (i >= 0) {
                        currentOrderList.removeAt(i)
                        currentOrderAdapter.removeItem(i)
                    }
                    currentOrderLv.adapter = currentOrderAdapter
                    if (currentOrderList.size <= 0) {
                        queryCurrentOrder()
                    }
                }
//                SoketSubscribeOrderBean.EVENT_CANCEL -> {
//                    val i = currentOrderList.indexOfFirst { item ->
//                        item.id == it.order.id
//                    }
//                    if (i >= 0) {
//                        currentOrderList.removeAt(i)
//                        currentOrderAdapter.removeItem(i)
//                    }
//                    currentOrderLv.adapter = currentOrderAdapter
//                    if (currentOrderList.size <= 0) {
//                        queryCurrentOrder()
//                    }
//                }
            }
        }, { rs, bean ->
            if (rs) {
                val i = currentOrderList.indexOfFirst { item ->
                    item.id == bean!!.id
                }
                if (i >= 0) {
                    currentOrderList.removeAt(i)
                    currentOrderAdapter.removeItem(i)
                }
                currentOrderLv.adapter = currentOrderAdapter
                if (currentOrderList.size <= 0) {
                    queryCurrentOrder()
                }
            }
        })
    }

    fun queryCurrentOrder() {
        currentOrderAdapter.clearGroup(false)
        currentOrderList.clear()
        if (userBean != null)
            binder?.queryCurrentOrder("bibi", "${currentTransactionPair?.currency}${currentTransactionPair?.mainCurrency}", 0, 20, 0)
    }

    fun queryAsset() {
        if (userBean != null)
            binder?.queryAsset(mutableListOf<String>().apply {
                add(currentTransactionPair!!.currency)
                add(currentTransactionPair!!.mainCurrency)
            })

    }

    override fun onPause() {
        super.onPause()
        NLog.i("bibi onPause")
//        binder?.removeCallBack3("bibi")
    }

    override fun onResume() {
        super.onResume()
        NLog.i("bibi onResume")
//        if (!isBinder) {
        activity!!.bindService(Intent(activity, SoketService::class.java), connection, Context.BIND_AUTO_CREATE)
//        }
//        else {
//            if (currentTransactionPair != null) {
//                addCallBack()
//                addOrderCallBack()
//                queryCurrentOrder()
//                queryAsset()
//            } else {
//                binder?.queryMarket()
//            }
//        }
        getEt.setText("")
        buyIsb.setProgress(0f)
    }


    override fun onDestroy() {
        activity!!.unbindService(connection)
        super.onDestroy()
        NLog.i("bibi onDestroy")
    }

    override fun onInvisibleRequest() {
        NLog.i("bibi onInvisibleRequest")
        binder?.removeCallBack3("bibi")
        super.onInvisibleRequest()
    }


    override fun onVisibleRequest() {
        super.onVisibleRequest()
        NLog.i("bibi onVisibleRequest")
        if (isBinder) {
            if (currentTransactionPair != null) {
                addCallBack()
                addOrderCallBack()
                queryCurrentOrder()
                queryAsset()
            } else {
                binder?.queryMarket("bibi")
            }
        }
    }
}
