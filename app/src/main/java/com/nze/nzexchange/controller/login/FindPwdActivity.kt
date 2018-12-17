package com.nze.nzexchange.controller.login

import android.view.View
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.VerifyBean
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.extend.getContent
import com.nze.nzexchange.http.CRetrofit
import com.nze.nzexchange.tools.MD5Tool
import com.nze.nzexchange.tools.RegularTool
import com.nze.nzexchange.validation.EmptyValidation
import com.nze.nzexchange.widget.CommonButton
import com.nze.nzexchange.widget.CommonTopBar
import com.nze.nzexchange.widget.VerifyButton
import com.nze.nzexchange.widget.clearedit.ClearableEditText
import kotlinx.android.synthetic.main.activity_find_pwd.*

class FindPwdActivity : NBaseActivity(), View.OnClickListener {


    val topBar: CommonTopBar by lazy { ctb_afp }
    val accountEt: ClearableEditText by lazy { et_account_afp }
    val verifyEt: ClearableEditText by lazy { et_verify_afp }
    val verifyBtn: VerifyButton by lazy { tv_verify_afp }
    val pwdEt: ClearableEditText by lazy { et_pwd_afp }
    val pwdAgainEt: ClearableEditText by lazy { et_pwd_again_afp }
    val confirmBtn: CommonButton by lazy { btn_confirm_afp }

    var checkcodeId: String = ""

    override fun getRootView(): Int = R.layout.activity_find_pwd

    override fun initView() {
        setWindowStatusBarColor(R.color.color_bg)
        topBar.setRightClick {
            this@FindPwdActivity.finish()
        }

        confirmBtn.initValidator()
                .add(accountEt, EmptyValidation())
                .add(verifyEt, EmptyValidation())
                .add(pwdEt, EmptyValidation())
                .add(pwdAgainEt, EmptyValidation())
                .executeValidator()
        verifyBtn.setVerifyClick(this)
        confirmBtn.setOnCommonClick(this)
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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_verify_afp -> {
                VerifyBean.getVerifyCodeNet(accountEt.getContent(), VerifyBean.TYPE_FIND_PASSWORD)
                        .compose(netTfWithDialog())
                        .subscribe({
                            if (it.success) {
                                verifyBtn.startVerify()
                                checkcodeId = it.result.checkcodeId
                                showToast("验证码已经发送到${accountEt.getContent()}")
                            }
                        }, onError)
            }
            R.id.btn_confirm_afp -> {

                if (pwdEt.getContent() != pwdAgainEt.getContent()) {
                    showToast("两次密码不一致")
                    return
                }
                val account = accountEt.getContent()
                var type = RegularTool.findAccountType(account)
                var userPhone = ""
                var userEmail = ""
                var userPassworUcode = MD5Tool.getMd5_32(pwdEt.getContent())
                if (type == RegularTool.ACCOUNT_TYPE_EMAIL) {
                    userEmail = account
                } else {
                    userPhone = account
                }
                CRetrofit.instance
                        .userService()
                        .findPassword(checkcodeId, verifyEt.getContent(), userPhone, userEmail, userPassworUcode)
                        .compose(netTfWithDialog())
                        .subscribe({
                            showToast(it.message)
                            if (it.success) {
                                this@FindPwdActivity.finish()
                            }
                        }, onError)
            }
        }
    }
}
