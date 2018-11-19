package com.nze.nzexchange.controller.market

import android.view.View
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.controller.base.NBaseFragment

class MarketContentFragment : NBaseFragment() {
    override fun getRootView(): Int = R.layout.fragment_market_content

    override fun initView(rootView: View) {

    }

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {
    }

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