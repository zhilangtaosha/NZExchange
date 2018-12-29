package com.nze.nzexchange.controller.my.setting

import android.graphics.drawable.ColorDrawable
import android.view.View
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.widget.pulltorefresh.PullToRefreshListView
import com.nze.nzexchange.R
import com.nze.nzexchange.bean2.ExchangeRateBean
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.tools.getNColor
import kotlinx.android.synthetic.main.activity_exchange_rate_set.*

class ExchangeRateSetActivity : NBaseActivity() {
    val ptrLv: PullToRefreshListView by lazy { ptrlv_aers }
    val rateAdapter: ExchangeRateAdapter by lazy { ExchangeRateAdapter(this) }

    override fun getRootView(): Int = R.layout.activity_exchange_rate_set

    override fun initView() {
        val listView = ptrLv.refreshableView
        listView.adapter = rateAdapter
        listView.divider = ColorDrawable(getNColor(R.color.color_line))
        listView.dividerHeight = 1
        rateAdapter.group = ExchangeRateBean.getList()

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

}
