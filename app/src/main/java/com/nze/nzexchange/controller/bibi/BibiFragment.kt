package com.nze.nzexchange.controller.bibi


import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Handler
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
import com.nze.nzexchange.extend.*
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
                giveEt.setText(it.cost)
            }
        }
    }
    val handicapBuyAdapter by lazy {
        HandicapAdapter(activity!!, HandicapAdapter.BUY).apply {
            onHandicapItemClick = {
                giveEt.setText(it.cost)
            }
        }
    }
    val currentOrderAdapter by lazy {
        BibiCurentOrderAdapter(activity!!).apply {
            cancelClick = { position, item ->
                OrderPendBean.cancelOrder(item.id, item.userId, currentTransactionPair?.id!!, null, userBean!!.tokenReqVo.tokenUserId, userBean!!.tokenReqVo.tokenUserKey)
                        .compose(netTfWithDialog())
                        .subscribe({
                            if (it.success) {
                                orderPending(currentTransactionPair?.id!!, userBean?.userId!!)
                            } else {
                                if (it.isCauseNotEmpty()) {
                                    AuthorityDialog.getInstance(activity!!)
                                            .show("取消当前委托需要完成以下设置，请检查"
                                                    , it.cause) {

                                            }
                                }
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


    private val itemDepth: MutableList<String> = mutableListOf<String>("1", "2", "3", "4", "5", "6", "7", "8")
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
                        totalTransactionTv.text = "交易额 ${total.formatForCurrency()} ${currentTransactionPair?.mainCurrency}"
                    } else if ((it.isNullOrEmpty() || get.isNullOrEmpty()) && transactionType == TRANSACTIONTYPE_LIMIT) {
                        totalTransactionTv.text = "交易额 0 ${currentTransactionPair?.mainCurrency}"
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
                        totalTransactionTv.text = "交易额 ${total.formatForCurrency()} ${currentTransactionPair?.mainCurrency}"
                    } else if ((it.isNullOrEmpty() || give.isNullOrEmpty()) && transactionType == TRANSACTIONTYPE_LIMIT) {
                        totalTransactionTv.text = "交易额 0${currentTransactionPair?.mainCurrency}"
                    } else if (it.isNotEmpty() && transactionType == TRANSACTIONTYPE_MARKET) {
                        val input = it.toString().toDouble()
                        totalTransactionTv.text = "交易额 ${input.formatForCurrency()} ${currentTransactionPair?.mainCurrency}"
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
            availableTv.setTextFromHtml("可用<font color=\"#0DA287\">${restOrderBean?.mainCurrency?.available?.formatForCurrency()
                    ?: "--"}${currentTransactionPair?.mainCurrency ?: "--"}</font>")
            buyIsb.visibility = View.VISIBLE
            saleIsb.visibility = View.GONE
            transactionBtn.setBgByDrawable(ContextCompat.getDrawable(activity!!, R.drawable.selector_btn_9d81_bg)!!)
            transactionBtn.text = "买入${currentTransactionPair!!.currency}"
            seekbarValueTv.text = "${buyIsb.progress}%"
            saleIsb.setProgress(0F)
            if (transactionType == TRANSACTIONTYPE_LIMIT) {
                getEt.hint = "数量(${currentTransactionPair?.currency})"
            } else {
                getEt.hint = "交易额(${currentTransactionPair?.mainCurrency})"
            }
        } else {
            buyTv.isSelected = false
            saleTv.isSelected = true
            availableTv.setTextFromHtml("可用<font color=\"#FF4A5F\">${restOrderBean?.currency?.available?.formatForCurrency()
                    ?: "--"}${currentTransactionPair?.currency ?: "--"}</font>")
            buyIsb.visibility = View.GONE
            saleIsb.visibility = View.VISIBLE
            transactionBtn.setBgByDrawable(ContextCompat.getDrawable(activity!!, R.drawable.selector_btn_4a5f_bg)!!)
            transactionBtn.text = "卖出${currentTransactionPair!!.currency}"
            seekbarValueTv.text = "${saleIsb.progress}%"
            buyIsb.setProgress(0F)
            getEt.hint = "数量(${currentTransactionPair?.currency})"
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
            if (userBean != null)
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
            showNODataView("当前没有委托")
            currentOrderLv.adapter = currentOrderAdapter
            loopAction?.close()
            loopAction = null
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
                if (transactionType == TRANSACTIONTYPE_LIMIT) {
                    if (!checkPrice()) return
                }
                if (!checkNum()) {
                    showToast("请填写交易数量")
                    return
                }
                CheckPermission.getInstance()
                        .commonCheck(activity as NBaseActivity, CheckPermission.BIBI_TRADE, "进行币币交易需要完成以下设置，请检查", onPass = {
                            fundPopup.showPopupWindow()
                        })


            }
            R.id.iv_kline_bibi -> {
                startActivity(Intent(activity, KLineActivity::class.java).putExtra(IntentConstant.PARAM_TRANSACTION_PAIR, currentTransactionPair))
            }

            R.id.tv_last_cost_bibi -> {
                giveEt.setText(lastCostTv.text)
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
            LimitTransactionBean.limitTransaction(currentType, user.userId, currentTransactionPair?.id!!, number, giveEt.getContent().toDouble(), user.tokenReqVo.tokenUserId, user.tokenReqVo.tokenUserKey, pwd)
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
            LimitTransactionBean.marketTransaction(currentType, user.userId, currentTransactionPair?.id!!, number, user.tokenReqVo.tokenUserId, user.tokenReqVo.tokenUserKey, pwd)
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
        if (s.isNullOrEmpty() || (s.isNotEmpty() && s.toDouble() <= 0)) {
            getEt.requestFocus()
            return false
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
                giveEt.hint = "价格(${currentTransactionPair?.mainCurrency})"
                giveEt.setText(currentTransactionPair?.exchangeRate?.formatForCurrency())
                giveReduceTv.visibility = View.VISIBLE
                giveAddTv.visibility = View.VISIBLE
                try {
                    val price = giveEt.getContent().toDouble()
                    val num = getEt.getContent().toDouble()
                    val total = price.mul(num)
                    totalTransactionTv.text = "交易额${total.formatForCurrency()}${currentTransactionPair?.mainCurrency}"
                } catch (e: Exception) {
                    totalTransactionTv.text = "交易额0${currentTransactionPair?.mainCurrency}"
                }
                if (currentType == TYPE_BUY) {
                    getEt.hint = "数量(${currentTransactionPair?.currency})"
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
                    getEt.hint = "交易额(${currentTransactionPair?.mainCurrency})"
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
            depthTv.text = "深度$item"
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
            if (transactionType == TRANSACTIONTYPE_LIMIT && restOrderBean != null && restOrderBean!!.mainCurrency != null) {
                val give = giveEt.getContent()
                if (give.isNotEmpty()) {
                    val price = give.toDouble()
//                    val total = restOrderBean!!.mainCurrency!!.available * progress!! / 100
                    val total = restOrderBean!!.mainCurrency!!.available.mul(progress!!.toDouble()).divByFloor(100.toDouble(), 8)
                    if (price > 0) {
                        val input = DoubleMath.divByFloor(total, price, 4).toString()
                        getEt.setText(input)
                        getEt.setSelection(input.length)
                    } else {
                        getEt.setText("0")
                        getEt.setSelection(1)
                    }
                }
            } else if (transactionType == TRANSACTIONTYPE_MARKET && restOrderBean != null && restOrderBean!!.mainCurrency != null) {
//                val total = restOrderBean!!.mainCurrency!!.available * progress!! / 100
//                val s = total.retain4ByFloor()
                val s = restOrderBean!!.mainCurrency!!.available.mul(progress!!.toDouble()).divByFloor(100.toDouble(), 4).toString()
                getEt.setText(s)
                getEt.setSelection(s.length)
            }
        } else {
            if (restOrderBean != null && restOrderBean!!.currency != null) {
                val s = restOrderBean!!.currency!!.available.mul(progress!!.toDouble()).divByFloor(100.toDouble(), 4).retain4ByFloor()
                getEt.setText(s)
                getEt.setSelection(s.length)
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
            if (transactionType == TRANSACTIONTYPE_LIMIT) {
                giveEt.hint = "价格(${it.mainCurrency})"
                giveEt.setText(it.exchangeRate.formatForCurrency())
            }
            if (transactionType == TRANSACTIONTYPE_LIMIT) {
                getEt.hint = "数量(${currentTransactionPair?.currency})"
            } else {
                getEt.hint = "交易额(${currentTransactionPair?.mainCurrency})"
            }
            currentOrderAdapter.mainCurrency = it.mainCurrency
            currentOrderAdapter.currency = it.currency
            if (currentType == TYPE_BUY) {
                availableTv.setTextFromHtml("可用<font color=\"#0DA287\">--${it.mainCurrency}</font>")
            } else {
                availableTv.setTextFromHtml("可用<font color=\"#FF4A5F\">--${it.currency}</font>")
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
//                        lastPriceTv.text = "≈${it.result}CNY"
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
                            if (currentTransactionPair == null) {
                                TransactionPairsBean.getTransactionPairs(list[0].mainCurrency, userBean?.userId?.getValue())
                                        .subscribeOn(Schedulers.io())
                            } else {
                                Flowable.empty()
                            }
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
                        getPendingOrderInfo(currentTransactionPair?.id!!)
                        changePair()
                        if (userBean != null) {
                            //获取订单
                            orderPending(currentTransactionPair?.id!!, userBean?.userId!!)
                        } else {
                            showNODataView("当前没有登录")
                        }

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

    var loopAction: NLoopAction? = null
    /**
     * 获取当前委托
     */
    private fun orderPending(currencyId: String, userId: String?) {
        if (loopAction == null)
            loopAction = NLoopAction.getInstance((activity as NBaseActivity?)!!)
        loopAction?.loop {
            OrderPendBean.orderPending(currencyId, userId)
                    .compose(netTf())
                    .subscribe({
                        val list = it.result
                        if (it.success && list != null && list.size > 0) {
                            stopAllView()
                            currentOrderAdapter.group = it.result
                            currentOrderLv.adapter = currentOrderAdapter
                        } else {
                            showNODataView("当前没有委托")
                        }
                    }, {
                        showNODataView("当前没有委托")
                    })
        }

    }

    private fun getKData() {
//        binder?.getKDataRequest(currentTransactionPair!!)
    }


    override fun onPause() {
        super.onPause()

    }

    override fun onResume() {
        super.onResume()
        activity!!.bindService(Intent(activity, SoketService::class.java), connection, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        super.onDestroy()
        binder?.close()
        activity!!.unbindService(connection)
    }

    fun changePair() {
        lastCostTv.text = "0"
        lastPriceTv.text = "0CNY"
        handicapBuyAdapter.clearGroup(true)
        handicapBuyLv.adapter = handicapBuyAdapter
        handicapSaleAdapter.clearGroup(true)
        handicapSaleLv.adapter = handicapSaleAdapter
        binder?.subscribeDepthAndToday(KLineParam.AMOUNT_DEPTH_5, KLineParam.DEPTH_2, "${currentTransactionPair?.currency?.toUpperCase()}${currentTransactionPair?.mainCurrency?.toUpperCase()}")
    }

    val connection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            Log.i("zwy", "onServiceDisconnected")
            isBinder = false
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.i("zwy", "onServiceConnected")
            binder = service as SoketService.SoketBinder
            isBinder = true
            binder?.initSocket(
                    "${currentTransactionPair?.currency?.toUpperCase()}${currentTransactionPair?.mainCurrency?.toUpperCase()}",
                    KLineParam.MARKET_MYSELF,
                    {
                        //查询k线

                    },
                    {
                        //订阅k线

                    },
                    {
                        //订阅今日行情
                        lastCostTv.text = it.last.formatForPrice()
                        lastPriceTv.text = "≈${it.last.mul(mainCurrencyPrice).formatForLegal()}CNY"
                    },
                    { mDepthBuyList, mDepthSellList ->
                        mDepthSellList.sortBy { it.price }
                        mDepthBuyList.sortByDescending { it.price }
                        //订阅深度
                        val buyList = mutableListOf<HandicapBean>()
                        val saleList = mutableListOf<HandicapBean>()
                        mDepthSellList.forEachIndexed { index, it ->
                            saleList.add(HandicapBean(index + 1, it.price.formatForPrice(), it.volume.formatForCurrency(), ""))
                        }
                        mDepthBuyList.forEachIndexed { index, it ->
                            buyList.add(HandicapBean(index + 1, it.price.formatForPrice(), it.volume.formatForCurrency(), ""))
                        }
                        handicapBuyAdapter.group = buyList.take(5).toMutableList()
                        handicapBuyLv.adapter = handicapBuyAdapter
                        handicapSaleAdapter.group = saleList.takeLast(5).toMutableList().asReversed()
                        handicapSaleLv.adapter = handicapSaleAdapter
                    },
                    {
                        //订阅最近成交列表

                    })

            changePair()
        }


    }
}
