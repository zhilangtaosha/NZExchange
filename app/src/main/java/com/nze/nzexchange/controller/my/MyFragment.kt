package com.nze.nzexchange.controller.my


import android.view.View
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.utils.EventCenter

import com.nze.nzexchange.R
import com.nze.nzexchange.controller.base.NBaseFragment
import com.nze.nzexchange.controller.my.paymethod.SetPayMethodActivity
import kotlinx.android.synthetic.main.fragment_my.*
import kotlinx.android.synthetic.main.fragment_my.view.*


class MyFragment : NBaseFragment(), View.OnClickListener {

    companion object {
        @JvmStatic
        fun newInstance() = MyFragment()
    }

    override fun getRootView(): Int = R.layout.fragment_my

    override fun initView(rootView: View) {
        rootView.tv_pay_method_my.setOnClickListener(this)
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
        when(v?.id){
            R.id.tv_pay_method_my->{
                skipActivity(SetPayMethodActivity::class.java)
            }
        }
    }
}
