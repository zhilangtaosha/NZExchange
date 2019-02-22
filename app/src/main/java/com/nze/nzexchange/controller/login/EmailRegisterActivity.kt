package com.nze.nzexchange.controller.login

import android.content.Intent
import android.view.View
import android.widget.CheckBox
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.NzeApp
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.LoginBean
import com.nze.nzexchange.bean.RegisterBean
import com.nze.nzexchange.config.EventCode
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.extend.*
import com.nze.nzexchange.bean.Result
import com.nze.nzexchange.bean.VerifyBean
import com.nze.nzexchange.tools.MD5Tool
import com.nze.nzexchange.validation.EmptyValidation
import com.nze.nzexchange.widget.CommonButton
import com.nze.nzexchange.widget.VerifyButton
import com.nze.nzexchange.widget.clearedit.ClearableEditText
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_email_register.*
import org.greenrobot.eventbus.EventBus

class EmailRegisterActivity : NBaseActivity(), View.OnClickListener {
    private val emailEt: ClearableEditText by lazy { et_email_aer }
    private val verifyEt: ClearableEditText by lazy { et_verify_aer }
    private val verifyTv: VerifyButton by lazy { tv_verify_aer }
    private val pwdEt: ClearableEditText by lazy { et_pwd_aer }
    private val pwdCb: CheckBox by lazy { cb_pwd_aer }
    private val agreeCb: CheckBox by lazy { cb_agree_aer }
    private val registerBtn: CommonButton by lazy { btn_register_aer }

    private var checkcodeId: String? = null

    override fun getRootView(): Int = R.layout.activity_email_register

    override fun initView() {
        ctb_aer.setRightClick {
            this@EmailRegisterActivity.finish()
        }
        tv_to_login_aer.setTextFromHtml("已有账号？<font color=\"#6D87A8\">登录</font>")
        tv_go_phone_aer.setOnClickListener(this)
        tv_to_login_aer.setOnClickListener(this)
        verifyTv.setVerifyClick(this)
        pwdCb.getCheckListener()
                .subscribe {
                    if (it) {
                        pwdEt.hidePassword()
                    } else {
                        pwdEt.showPassword()
                    }
                }

        registerBtn.setOnCommonClick(this)
        registerBtn.initValidator()
                .add(emailEt, EmptyValidation())
                .add(verifyEt, EmptyValidation())
                .add(pwdEt, EmptyValidation())
                .executeValidator()


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
            R.id.tv_go_phone_aer -> {
                skipActivity(PhoneRegisterActivity::class.java)
                this@EmailRegisterActivity.finish()
            }
            R.id.tv_to_login_aer -> {
                val intent = Intent(this@EmailRegisterActivity, LoginActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
            R.id.tv_verify_aer -> {
                VerifyBean.getVerifyCodeNet(emailEt.getContent(), VerifyBean.TYPE_REGISTER)
                        .compose(netTfWithDialog())
                        .subscribe({
                            if (it.success) {
                                verifyTv.startVerify()
                                checkcodeId = it.result.checkcodeId
                                showToast("验证码已经发送到${emailEt.getContent()}")
                            }
                        }, onError)
            }
            R.id.btn_register_aer -> {
                if (checkcodeId.isNullOrEmpty()) {
                    showToast("请先获取验证码")
                    return
                }
                if (registerBtn.validate()) {
                    val pwdStr = MD5Tool.getMd5_32(pwdEt.getContent())
                    RegisterBean.registerNet(null, emailEt.getContent(), null, pwdStr, checkcodeId, verifyEt.getContent())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .flatMap {
                                if (it.success) {
                                    showToast("注册成功，正在自动登录..")
                                    LoginBean.login(emailEt.getContent(), pwdStr)
                                            .subscribeOn(Schedulers.io())
                                } else {
                                    Flowable.error<String>(Throwable(it.message))
                                }
                            }
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                val rs = it as Result<LoginBean>
                                if (rs.success) {
                                    showToast("登录成功")
                                    NzeApp.instance.userBean = rs.result.cloneToUserBean()
                                    EventBus.getDefault().post(EventCenter<Boolean>(EventCode.CODE_LOGIN_SUCCUSS, true))
                                    this@EmailRegisterActivity.finish()
                                }
                            }, {
                                it.message?.let { showToast(it) }
                            })


                }
            }
        }
    }
}
