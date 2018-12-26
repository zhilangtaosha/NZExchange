package com.nze.nzexchange.controller.my.safecenter

import android.view.View
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.widget.CommonButton
import com.nze.nzexchange.widget.VerifyButton
import com.nze.nzexchange.widget.clearedit.ClearableEditText
import kotlinx.android.synthetic.main.activity_bind_email.*

class BindEmailActivity : NBaseActivity() {
    val emailEt: ClearableEditText by lazy { et_email_abe }
    val verifyEt: ClearableEditText by lazy { et_verify_abe }
    val verifyBtn: VerifyButton by lazy { btn_verify_abe }
    val confirmBtn: CommonButton by lazy { btn_confirm_abe }

    override fun getRootView(): Int = R.layout.activity_bind_email

    override fun initView() {

        confirmBtn.setOnCommonClick {
            skipActivity(BindEmailVerifyActivity::class.java)
        }
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
