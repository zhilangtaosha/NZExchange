package com.nze.nzexchange.controller.my.safecenter

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.tool.NLog
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.bean.VerifyBean
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.extend.getContent
import com.nze.nzexchange.http.CRetrofit
import com.nze.nzexchange.validation.EmptyValidation
import com.nze.nzexchange.widget.CommonButton
import com.nze.nzexchange.widget.VerifyButton
import com.nze.nzexchange.widget.clearedit.ClearableEditText
import kotlinx.android.synthetic.main.activity_set_fund_password.*

class SetFundPasswordActivity : NBaseActivity(), View.OnClickListener {


    val pwdEt: ClearableEditText by lazy { et_password_asfp }
    val pwdConfirmEt: ClearableEditText by lazy { et_password_confirm_asfp }
    val verifyEt: ClearableEditText by lazy { et_verify_asfp }
    val verifyBtn: VerifyButton by lazy { btn_verify_asfp }
    val confirmBtn: CommonButton by lazy { btn_confirm_asfp }
    var userBean = UserBean.loadFromApp()
    var checkcodeId: String? = null

    override fun getRootView(): Int = R.layout.activity_set_fund_password

    override fun initView() {

        confirmBtn.initValidator()
                .add(pwdEt, EmptyValidation())
                .add(pwdConfirmEt, EmptyValidation())
                .add(verifyEt, EmptyValidation())
                .executeValidator()

        verifyBtn.setOnClickListener(this)
        confirmBtn.setOnCommonClick(this)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_verify_asfp -> {
                VerifyBean.getVerifyCodeNet(userBean?.userEmail!!, VerifyBean.TYPE_COMMON)
                        .compose(netTfWithDialog())
                        .subscribe({
                            if (it.success) {
                                verifyBtn.startVerify()
                                checkcodeId = it.result.checkcodeId
                                showToast("验证码已经发送到${userBean?.userEmail}")
                            }
                        }, onError)
            }
            R.id.btn_confirm_asfp -> {

                if (checkcodeId == null)
                    showToast("验证码获取错误,请重新获取")
                if (confirmBtn.validate()) {
                    val pwdStr: String = pwdEt.getContent()
                    val pwdConfirm: String = pwdConfirmEt.getContent()
                    if (pwdStr != pwdConfirm) {
                        showToast("两次密码不一致")
                        return
                    }
                    CRetrofit.instance
                            .userService()
                            .setBuspw(checkcodeId!!, verifyEt.getContent(), userBean?.userEmail!!, pwdEt.getContent())
                            .subscribe({
                                NLog.i(it.message)
                            }, onError)
                }
            }
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
