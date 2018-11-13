package com.nze.nzexchange.controller.main


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.utils.EventCenter

import com.nze.nzexchange.R
import com.nze.nzexchange.controller.base.NBaseFragment

class BibiFragment : NBaseFragment() {

    companion object {
        @JvmStatic
        fun newInstance() = BibiFragment()
    }

    override fun getRootView(): Int = R.layout.fragment_bibi

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
