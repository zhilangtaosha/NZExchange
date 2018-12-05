package com.nze.nzexchange.controller.bibi


import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter

import com.nze.nzexchange.R
import com.nze.nzexchange.bean.HandicapBean
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.base.NBaseFragment
import com.nze.nzexchange.extend.setBgByDrawable
import com.nze.nzexchange.extend.setTextFromHtml
import com.nze.nzexchange.widget.LinearLayoutAsListView
import com.warkiz.widget.IndicatorSeekBar
import kotlinx.android.synthetic.main.fragment_bibi.*
import kotlinx.android.synthetic.main.fragment_bibi.view.*

class BibiFragment : NBaseFragment(), View.OnClickListener {


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
    val buyIsb: IndicatorSeekBar by lazy { rootView.isb_buy_bibi }
    val saleIsb: IndicatorSeekBar by lazy { rootView.isb_sale_bibi }
    val totalTransactionTv: TextView by lazy { rootView.tv_total_transaction_bibi }
    val transactionBtn: Button by lazy { rootView.btn_transaction_bibi }
    val handicapSaleLv: LinearLayoutAsListView by lazy { rootView.lv_handicap_sale_bibi }
    val lastCostTv: TextView by lazy { rootView.tv_last_cost_bibi }
    val lastPriceTv: TextView by lazy { rootView.tv_last_price_bibi }
    val handicapBuyLv: LinearLayoutAsListView by lazy { rootView.lv_handicap_buy_bibi }
    val depthTv: TextView by lazy { rootView.tv_depth_bibi }
    val allOrderTv: TextView by lazy { rootView.tv_all_order_bibi }
    val currentOrderLv: LinearLayoutAsListView by lazy { rootView.lv_current_order_bibi }
    val handicapSaleAdapter by lazy { HandicapAdapter(activity!!, HandicapAdapter.SALE) }
    val handicapBuyAdapter by lazy { HandicapAdapter(activity!!, HandicapAdapter.BUY) }
    val currentOrderAdapter by lazy { BibiCurentOrderAdapter(activity!!) }

    val TYPE_BUY = 0
    val TYPE_SALE = 1
    var currentType = TYPE_BUY

    private val sidePopup: BibiSidePopup by lazy { BibiSidePopup(mBaseActivity, fragmentManager!!) }


    companion object {
        @JvmStatic
        fun newInstance() = BibiFragment()
    }

    override fun getRootView(): Int = R.layout.fragment_bibi

    override fun initView(rootView: View) {
        this.rootView = rootView
        moreTv.setOnClickListener(this)
        buyTv.setOnClickListener(this)
        saleTv.setOnClickListener(this)
        allOrderTv.setOnClickListener(this)

        handicapSaleAdapter.group = HandicapBean.getList()
        handicapBuyAdapter.group = HandicapBean.getList()
        handicapSaleLv.adapter = handicapSaleAdapter
        handicapBuyLv.adapter = handicapBuyAdapter

        currentOrderAdapter.group = HandicapBean.getList()
        currentOrderLv.adapter = currentOrderAdapter

        switchType(currentType)
    }


    fun switchType(type: Int) {
        if (type == TYPE_BUY) {
            buyTv.isSelected = true
            saleTv.isSelected = false
            availableTv.setTextFromHtml("可用<font color=\"#0DA287\">0.568USDT</font>")
            buyIsb.visibility = View.VISIBLE
            saleIsb.visibility = View.GONE
            transactionBtn.setBgByDrawable(ContextCompat.getDrawable(activity!!, R.drawable.selector_btn_9d81_bg)!!)
            transactionBtn.text = "买入BTC"
        } else {
            buyTv.isSelected = false
            saleTv.isSelected = true
            availableTv.setTextFromHtml("可用<font color=\"#FF4A5F\">0.568USDT</font>")
            buyIsb.visibility = View.GONE
            saleIsb.visibility = View.VISIBLE
            transactionBtn.setBgByDrawable(ContextCompat.getDrawable(activity!!, R.drawable.selector_btn_4a5f_bg)!!)
            transactionBtn.text = "买出BTC"
        }
    }

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {
    }


    override fun isBindEventBusHere(): Boolean = false

    override fun isBindNetworkListener(): Boolean = false

    override fun onNetworkConnected(type: NetUtils.NetType) {
    }

    override fun onNetworkDisConnected() {
    }

    override fun getContainerTargetView(): View? = null

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.more_bibi -> {
                sidePopup.showPopupWindow()
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
        }
    }
}
