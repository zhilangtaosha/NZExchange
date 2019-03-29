package com.nze.nzexchange.controller.my.safecenter

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
import kotlinx.android.synthetic.main.activity_bind_email.*

class BindEmailActivity : NBaseActivity(), View.OnClickListener {


    private val emailEt: ClearableEditText by lazy { et_email_abe }
    private val verifyEt: ClearableEditText by lazy { et_verify_abe }
    private val verifyBtn: VerifyButton by lazy { btn_verify_abe }
    private val confirmBtn: CommonButton by lazy { btn_confirm_abe }
    private var checkcodeId: String? = null
    private var userBean = UserBean.loadFromApp()

    override fun getRootView(): Int = R.layout.activity_bind_email

    override fun initView() {

        verifyBtn.setVerifyClick(this)
        confirmBtn.setOnCommonClick(this)
        confirmBtn.initValidator()
                .add(emailEt, EmptyValidation())
                .add(verifyEt, EmptyValidation())
                .executeValidator()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_verify_abe -> {
                var email = emailEt.getContent()
                if (email.isNullOrEmpty()) {
                    showToast("请输入邮箱号码")
                    return
                }
                VerifyBean.getVerifyCodeNet(email, VerifyBean.TYPE_COMMON)
                        .compose(netTfWithDialog())
                        .subscribe({
                            if (it.success) {
                                verifyBtn.startVerify()
                                checkcodeId = it.result.checkcodeId
                                showToast("验证码已经发送到$email")
                            }
                        }, onError)
            }
            R.id.btn_confirm_abe -> {
                if (checkcodeId.isNullOrEmpty()) {
                    showToast("请先获取验证码")
                    return
                }
                if (confirmBtn.validate()) {
                    CRetrofit.instance
                            .userService()
                            .bindEmail(userBean!!.tokenReqVo.tokenUserId, userBean!!.tokenReqVo.tokenUserKey, checkcodeId!!, verifyEt.getContent(), emailEt.getContent())
                            .compose(netTfWithDialog())
                            .subscribe({
                                NLog.i("")
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
