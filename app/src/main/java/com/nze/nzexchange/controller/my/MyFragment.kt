package com.nze.nzexchange.controller.my


import android.view.View
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.utils.EventCenter

import com.nze.nzexchange.R
import com.nze.nzexchange.controller.base.NBaseFragment


class MyFragment : NBaseFragment() {

    companion object {
        @JvmStatic
        fun newInstance() = MyFragment()
    }

    override fun getRootView(): Int = R.layout.fragment_my

    override fun initView(rootView: View) {
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
