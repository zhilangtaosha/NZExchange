package com.nze.nzexchange.controller.my.asset.legal

import android.view.View
import android.widget.RelativeLayout
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.controller.base.NBaseActivity
import kotlinx.android.synthetic.main.activity_bind_recharge.*

class BindRechargeActivity : NBaseActivity() {

    val bankLayout: RelativeLayout by lazy { layout_bank_abr }
    val bpayLayout: RelativeLayout by lazy { layout_bpay_abr }
    val oskoLayout: RelativeLayout by lazy { layout_osko_abr }

    override fun getRootView(): Int = R.layout.activity_bind_recharge

    override fun initView() {
    }

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {
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

    override fun getContainerTargetView(): View? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
