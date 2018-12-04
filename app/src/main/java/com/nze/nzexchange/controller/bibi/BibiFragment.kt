package com.nze.nzexchange.controller.bibi


import android.view.View
import android.widget.TextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter

import com.nze.nzexchange.R
import com.nze.nzexchange.bean.HandicapBean
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.base.NBaseFragment
import com.nze.nzexchange.widget.LinearLayoutAsListView
import kotlinx.android.synthetic.main.fragment_bibi.view.*

class BibiFragment : NBaseFragment() {
    lateinit var buyTv: TextView
    lateinit var handicapSaleLv: LinearLayoutAsListView
    lateinit var handicapBuyLv: LinearLayoutAsListView
    lateinit var currentOrderLv: LinearLayoutAsListView
    val handicapSaleAdapter by lazy { HandicapAdapter(activity!!, HandicapAdapter.SALE) }
    val handicapBuyAdapter by lazy { HandicapAdapter(activity!!, HandicapAdapter.BUY) }
    val currentOrderAdapter by lazy { BibiCurentOrderAdapter(activity!!) }

    private val sidePopup: BibiSidePopup by lazy { BibiSidePopup(mBaseActivity, fragmentManager!!) }


    companion object {
        @JvmStatic
        fun newInstance() = BibiFragment()
    }

    override fun getRootView(): Int = R.layout.fragment_bibi

    override fun initView(rootView: View) {
        buyTv = rootView.tv_buy_bibi
        handicapSaleLv = rootView.lv_handicap_sale_bibi
        handicapBuyLv = rootView.lv_handicap_buy_bibi
        currentOrderLv = rootView.lv_current_order_bibi


        handicapSaleAdapter.group = HandicapBean.getList()
        handicapBuyAdapter.group = HandicapBean.getList()
        handicapSaleLv.adapter = handicapSaleAdapter
        handicapBuyLv.adapter = handicapBuyAdapter

        currentOrderAdapter.group = HandicapBean.getList()
        currentOrderLv.adapter = currentOrderAdapter


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


}
