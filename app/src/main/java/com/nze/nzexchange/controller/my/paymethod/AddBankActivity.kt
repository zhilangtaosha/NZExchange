package com.nze.nzexchange.controller.my.paymethod

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.utils.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.controller.base.NBaseActivity

/**
 * 添加银行卡
 */
class AddBankActivity : NBaseActivity() {
    override fun getRootView(): Int = R.layout.activity_add_card

    override fun initView() {

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