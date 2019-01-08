package com.nze.nzexchange.controller.bibi

import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.ListView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.widget.basepopup.BasePopupWindow
import com.nze.nzeframework.widget.pulltorefresh.PullToRefreshListView
import com.nze.nzeframework.widget.pulltorefresh.internal.PullToRefreshBase
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.HandicapBean
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.tools.getNColor
import com.nze.nzexchange.widget.CommonTopBar
import kotlinx.android.synthetic.main.activity_bibi_all_order.*
import kotlinx.android.synthetic.main.fragment_otc_content.view.*

class BibiAllOrderActivity : NBaseActivity(), PullToRefreshBase.OnRefreshListener<ListView> {


    val topBar: CommonTopBar by lazy { ctb_abao }
    val filterPopup by lazy { BibiFilterPopup(this) }
    val ptrLv: PullToRefreshListView by lazy { ptrlv_abao }
    val orderAdapter: BibiCurentOrderAdapter by lazy { BibiCurentOrderAdapter(this) }

    override fun getRootView(): Int = R.layout.activity_bibi_all_order

    override fun initView() {
        topBar.setRightClick {
            if (!filterPopup.isShowing)
                filterPopup.showPopupWindow(topBar)
        }
        filterPopup.setOnBeforeShowCallback { popupRootView, anchorView, hasShowAnima ->
            if (anchorView != null) {
                filterPopup.offsetY = anchorView.height
                return@setOnBeforeShowCallback true
            }
            false
        }


        ptrLv.setPullLoadEnabled(true)
        ptrLv.setOnRefreshListener(this)
        val listView = ptrLv.refreshableView
        listView.divider = ColorDrawable(getNColor(R.color.color_line))
        listView.dividerHeight = 1

        listView.adapter = orderAdapter
    }

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getOverridePendingTransitionMode(): TransitionMode = TransitionMode.DEFAULT

    override fun isBindEventBusHere(): Boolean = false

    override fun isBindNetworkListener(): Boolean = false

    override fun onNetworkConnected(type: NetUtils.NetType) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onNetworkDisConnected() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getContainerTargetView(): View? = null

    override fun onPullDownToRefresh(refreshView: PullToRefreshBase<ListView>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPullUpToRefresh(refreshView: PullToRefreshBase<ListView>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
