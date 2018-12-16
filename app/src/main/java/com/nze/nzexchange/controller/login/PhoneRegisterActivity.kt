package com.nze.nzexchange.controller.login

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.NzeApp
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.LoginBean
import com.nze.nzexchange.bean.RegisterBean
import com.nze.nzexchange.config.EventCode
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.extend.getContent
import com.nze.nzexchange.extend.setTextFromHtml
import com.nze.nzexchange.bean.Result
import com.nze.nzexchange.tools.MD5Tool
import com.nze.nzexchange.validation.EmptyValidation
import com.nze.nzexchange.widget.CommonButton
import com.nze.nzexchange.widget.VerifyButton
import com.nze.nzexchange.widget.clearedit.ClearableEditText
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_phone_register.*
import org.greenrobot.eventbus.EventBus

class PhoneRegisterActivity : NBaseActivity(), View.OnClickListener {
    val REQUEST_CODE = 0x112
    var countryName = "中国"
    var countryNumber = "+86"
    val countryTv: TextView by lazy { tv_country_apr }
    val countryCodeTv: TextView by lazy { tv_country_code_apr }
    val phoneEt: ClearableEditText by lazy { et_phone_apr }
    val verifyEt: ClearableEditText by lazy { et_verify_apr }
    val verifyButton: VerifyButton by lazy { tv_verify_apr }
    val pwdEt: ClearableEditText by lazy { et_pwd_apr }
    val pwdCb: CheckBox by lazy { cb_pwd_apr }
    val agreeCb: CheckBox by lazy { cb_agree_apr }
    val registerBtn: CommonButton by lazy { btn_register_apr }

    override fun getRootView(): Int = R.layout.activity_phone_register

    override fun initView() {
        ctb_apr.setRightClick {
            this@PhoneRegisterActivity.finish()
        }
        tv_to_login_apr.setTextFromHtml("已有账号？<font color=\"#6D87A8\">登录</font>")
        tv_go_email_apr.setOnClickListener(this)
        tv_to_login_apr.setOnClickListener(this)
        countryTv.setOnClickListener(this)

        registerBtn.initValidator()
                .add(phoneEt, EmptyValidation())
                .add(verifyEt, EmptyValidation())
                .add(pwdEt, EmptyValidation())
                .executeValidator()

        verifyButton.setVerifyClick(this)
        registerBtn.setOnCommonClick(this)
    }

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getOverridePendingTransitionMode(): TransitionMode = TransitionMode.DEFAULT

    override fun isBindEventBusHere(): Boolean = false

    override fun isBindNetworkListener(): Boolean = false

    override fun onNetworkConnected(type: NetUtils.NetType) {
    }

    override fun onNetworkDisConnected() {
    }

    override fun getContainerTargetView(): View? = null

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_go_email_apr -> {
                skipActivity(EmailRegisterActivity::class.java)
                this@PhoneRegisterActivity.finish()
            }
            R.id.tv_to_login_apr -> {
                val intent = Intent(this@PhoneRegisterActivity, LoginActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
            R.id.tv_country_apr -> {
                startActivityForResult(Intent(this@PhoneRegisterActivity, SelectCountryActivity::class.java), REQUEST_CODE)
            }
            R.id.tv_verify_apr -> {

            }
            R.id.btn_register_apr -> {
                if (registerBtn.validate()) {
                    val pwdStr = MD5Tool.getMd5_32(pwdEt.getContent())
                    RegisterBean.registerNet(phoneEt.getContent(), null, null, pwdStr, verifyEt.getContent(), null)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .flatMap {
                                if (it.success) {
                                    showToast("注册成功，正在自动登录..")
                                    LoginBean.login(phoneEt.getContent(), pwdStr)
                                            .subscribeOn(Schedulers.io())
                                } else {
                                    Flowable.error<String>(Throwable("注册失败"))
                                }
                            }
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                val rs = it as Result<LoginBean>
                                if (rs.success) {
                                    showToast("登录成功")
                                    NzeApp.instance.userBean = rs.result.cloneToUserBean()
                                    EventBus.getDefault().post(EventCenter<Boolean>(EventCode.CODE_LOGIN_SUCCUSS, true))
                                    this@PhoneRegisterActivity.finish()
                                }
                            }, {
                                it.message?.let { showToast(it) }
                            })

                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            data?.run {
                countryName = getStringExtra(IntentConstant.PARAM_COUNTRY_NAME)
                countryNumber = getStringExtra(IntentConstant.PARAM_COUNTRY_NUMBER)
            }
            countryTv.text = countryName
            countryCodeTv.text = countryNumber
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
