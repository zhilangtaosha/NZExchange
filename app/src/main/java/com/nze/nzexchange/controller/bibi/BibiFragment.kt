package com.nze.nzexchange.controller.bibi


import android.view.View
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter

import com.nze.nzexchange.R
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.base.NBaseFragment
import kotlinx.android.synthetic.main.fragment_bibi.view.*

class BibiFragment : NBaseFragment() {
    private val sidePopup: BibiSidePopup by lazy { BibiSidePopup(mBaseActivity, fragmentManager!!) }


    companion object {
        @JvmStatic
        fun newInstance() = BibiFragment()
    }

    override fun getRootView(): Int = R.layout.fragment_bibi

    override fun initView(rootView: View) {
        rootView.btn_bibi.setOnClickListener {
            sidePopup.showPopupWindow()
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


}
