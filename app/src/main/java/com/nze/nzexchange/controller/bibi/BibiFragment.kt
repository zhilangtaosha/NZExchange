package com.nze.nzexchange.controller.bibi


import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.tool.NLog
import com.nze.nzeframework.widget.basepopup.BasePopupWindow
import com.nze.nzexchange.NzeApp

import com.nze.nzexchange.R
import com.nze.nzexchange.bean.*
import com.nze.nzexchange.config.EventCode
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.base.NBaseFragment
import com.nze.nzexchange.controller.common.CommonListPopup
import com.nze.nzexchange.controller.login.LoginActivity
import com.nze.nzexchange.extend.formatForCurrency
import com.nze.nzexchange.extend.setBgByDrawable
import com.nze.nzexchange.extend.setShakeClickListener
import com.nze.nzexchange.extend.setTextFromHtml
import com.nze.nzexchange.widget.LinearLayoutAsListView
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams
import kotlinx.android.synthetic.main.fragment_bibi.*
import kotlinx.android.synthetic.main.fragment_bibi.view.*
import org.greenrobot.eventbus.EventBus

class BibiFragment : NBaseFragment(), View.OnClickListener, CommonListPopup.OnListPopupItemClick, OnSeekChangeListener {


    lateinit var rootView: View
    val moreTv: TextView by lazy { rootView.more_bibi }
    val klineIv: ImageView by lazy { rootView.iv_kline_bibi }
    val buyTv: TextView by lazy { rootView.tv_buy_bibi }
    val saleTv: TextView by lazy { rootView.tv_sale_bibi }
    val limitTv: TextView by lazy { rootView.tv_limit_bibi }
    val giveEt: EditText by lazy { rootView.et_give_bibi }
    val giveUnitTv: TextView by lazy { rootView.tv_give_unit_bibi }
    val priceTv: TextView by lazy { rootView.tv_price_bibi }
    val getEt: EditText by lazy { rootView.et_get_bibi }
    val getUnitTv: TextView by lazy { rootView.tv_get_unit_bibi }
    val availableTv: TextView by lazy { rootView.tv_available_bibi }
    val seekbarValueTv: TextView by lazy { rootView.tv_seekbar_value_bibi }
    val buyIsb: IndicatorSeekBar by lazy {
        rootView.isb_buy_bibi.apply {
            onSeekChangeListener = this@BibiFragment
        }
    }
    val saleIsb: IndicatorSeekBar by lazy {
        rootView.isb_sale_bibi.apply {
            onSeekChangeListener = this@BibiFragment
        }
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
    val currentOrderAdapter by lazy { BibiCurentOrderAdapter(activity!!) }

    val TYPE_BUY = 0
    val TYPE_SALE = 1
    var currentType = TYPE_BUY

    private val sidePopup: BibiSidePopup by lazy { BibiSidePopup(mBaseActivity, childFragmentManager!!) }
    private var currentTransactionPair: TransactionPairsBean? = null
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



        handicapSaleAdapter.group = HandicapBean.getList()
        handicapBuyAdapter.group = HandicapBean.getList()
        handicapSaleLv.adapter = handicapSaleAdapter
        handicapBuyLv.adapter = handicapBuyAdapter

        currentOrderAdapter.group = HandicapBean.getList()
        currentOrderLv.adapter = currentOrderAdapter

        switchType(currentType)
    }


    private fun switchType(type: Int) {
        if (type == TYPE_BUY) {
            buyTv.isSelected = true
            saleTv.isSelected = false
            availableTv.setTextFromHtml("可用<font color=\"#0DA287\">${restOrderBean?.mainCurrency?.available
                    ?: "--"}${currentTransactionPair?.mainCurrency ?: "--"}</font>")
            buyIsb.visibility = View.VISIBLE
            saleIsb.visibility = View.GONE
            transactionBtn.setBgByDrawable(ContextCompat.getDrawable(activity!!, R.drawable.selector_btn_9d81_bg)!!)
            transactionBtn.text = "买入BTC"
            seekbarValueTv.text = "${buyIsb.progress}%"
        } else {
            buyTv.isSelected = false
            saleTv.isSelected = true
            availableTv.setTextFromHtml("可用<font color=\"#FF4A5F\">${restOrderBean?.mainCurrency?.available
                    ?: "--"}${currentTransactionPair?.mainCurrency ?: "--"}</font>")
            buyIsb.visibility = View.GONE
            saleIsb.visibility = View.VISIBLE
            transactionBtn.setBgByDrawable(ContextCompat.getDrawable(activity!!, R.drawable.selector_btn_4a5f_bg)!!)
            transactionBtn.text = "买出BTC"
            seekbarValueTv.text = "${saleIsb.progress}%"
        }
        if (userBean == null)
            transactionBtn.text = "登录"
    }

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {
        if (eventCenter.eventCode == EventCode.CODE_SELECT_TRANSACTIONPAIR) {
            sidePopup.dismiss()
            currentTransactionPair = eventCenter.data as TransactionPairsBean
            refreshLayout()
        }
        if (eventCenter.eventCode == EventCode.CODE_LOGIN_SUCCUSS) {
            userBean = NzeApp.instance.userBean
            refreshLayout()
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
                skipActivity(BibiAllOrderActivity::class.java)
            }
            R.id.tv_limit_bibi -> {
                currentPopupType = POPUP_LIMIT
                limitPopup.showPopupWindow()
            }
            R.id.tv_depth_bibi -> {
                currentPopupType = POPUP_DEPTH
                depthPopup.showPopupWindow()
            }
            R.id.btn_transaction_bibi -> {
                if (userBean == null) {
                    skipActivity(LoginActivity::class.java)
                    return
                }
                if (currentType == TYPE_BUY) {

                } else {

                }
            }
        }
    }

    fun buyHandler(user: UserBean, number: Double, price: Double) {
        if (transactionType == TRANSACTIONTYPE_LIMIT) {
            LimitTransactionBean.limitTransaction(1, user.userId, currentTransactionPair?.id!!, number, price)
                    .compose(netTfWithDialog())
                    .subscribe({

                    }, onError)
        } else {

        }
    }

    fun saleHandler(user: UserBean, number: Double, price: Double) {
        if (transactionType == TRANSACTIONTYPE_LIMIT) {
            LimitTransactionBean.limitTransaction(0, user.userId, currentTransactionPair?.id!!, number, price)
                    .compose(netTfWithDialog())
                    .subscribe({

                    }, onError)
        } else {

        }
    }

    override fun clickItem(position: Int, item: String) {
        if (currentPopupType == POPUP_LIMIT) {
            limitTv.text = item
            transactionType = if (position == 0) {
                giveEt.isFocusable = true
                giveEt.isFocusableInTouchMode = true
                giveEt.hint = "价格"
                giveUnitTv.visibility = View.VISIBLE
                TRANSACTIONTYPE_LIMIT
            } else {
                giveEt.isFocusable = false
                giveEt.hint = "以当前最优价格交易"
                giveUnitTv.visibility = View.GONE
                TRANSACTIONTYPE_MARKET
            }
        } else {
            depthTv.text = "深度$item"
        }
    }

    //----------------------seekbar监听-------------------------
    override fun onSeeking(seekParams: SeekParams?) {
        seekbarValueTv.text = "${seekParams?.progress}%"
    }

    override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {
    }

    override fun onFirstRequest() {


    }

    private fun refreshLayout() {
        currentTransactionPair?.let {
            switchType(currentType)
            moreTv.text = it.transactionPair
            giveEt.setText(it.exchangeRate.formatForCurrency())
            giveUnitTv.text = it.mainCurrency
            getUnitTv.text = it.mainCurrency
            getPendingOrderInfo(it.id)
        }

    }

    //获取挂单信息
    private fun getPendingOrderInfo(currencyId: Int) {
        RestOrderBean.getPendingOrderInfo(currencyId, userBean?.userId)
                .compose(netTfWithDialog())
                .subscribe({
                    if (it.success) {
                        restOrderBean = it.result
                        switchType(currentType)
                    }
                }, onError)
    }
}
