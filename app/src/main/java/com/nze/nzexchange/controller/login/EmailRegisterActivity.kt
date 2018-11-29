package com.nze.nzexchange.controller.login

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.extend.setTextFromHtml
import kotlinx.android.synthetic.main.activity_email_register.*

class EmailRegisterActivity : NBaseActivity() {
    override fun getRootView(): Int =R.layout.activity_email_register

    override fun initView() {

        tv_to_login_aer.setTextFromHtml("已有账号？<font color=\"#6D87A8\">登录</font>")
    }

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getOverridePendingTransitionMode(): TransitionMode =TransitionMode.DEFAULT

    override fun isBindEventBusHere(): Boolean =false

    override fun isBindNetworkListener(): Boolean =false

    override fun onNetworkConnected(type: NetUtils.NetType) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onNetworkDisConnected() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getContainerTargetView(): View? =null


}
