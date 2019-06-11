package com.nze.nzexchange.controller.login

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.TextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.ui.BaseActivity
import com.nze.nzexchange.NzeApp
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.LoginBean
import com.nze.nzexchange.config.EventCode
import com.nze.nzexchange.config.Preferences
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.extend.getContent
import com.nze.nzexchange.tools.MD5Tool
import com.nze.nzexchange.validation.EmptyValidation
import com.nze.nzexchange.widget.CommonButton
import com.nze.nzexchange.widget.clearedit.ClearableEditText
import kotlinx.android.synthetic.main.activity_login.*
import net.grandcentrix.tray.AppPreferences
import org.greenrobot.eventbus.EventBus

class LoginActivity : NBaseActivity(), View.OnClickListener {
    val cancelTv: TextView by lazy { tv_cancel_al }
    val accountEt: ClearableEditText by lazy { et_account_al }
    val passwordEt: ClearableEditText by lazy { et_password_al }
    val loginBtn: CommonButton by lazy { btn_login_al }
    val forgetTv: TextView by lazy { tv_forget_al }

    val appPreferences: AppPreferences by lazy { AppPreferences(this) }

    companion object {
        fun skip(context: Context) {
            context.startActivity(Intent(context, LoginActivity::class.java))
        }
    }

    override fun getRootView(): Int = R.layout.activity_login

    override fun initView() {
        setWindowStatusBarColor(R.color.color_bg)
        cancelTv.setOnClickListener {
            onBackPressed()
        }
        tv_register_al.setOnClickListener(this)
        forgetTv.setOnClickListener(this)
        loginBtn.setOnCommonClick(this)

        loginBtn.initValidator()
                .add(accountEt, EmptyValidation())
                .add(passwordEt, EmptyValidation())
                .executeValidator()

        btn_quick_my.setOnClickListener {
            skipActivity(QuickLoginActivity::class.java)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        EventBus.getDefault().post(EventCenter<Boolean>(EventCode.CODE_NO_LOGIN))
    }

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {
        if (eventCenter.eventCode == EventCode.CODE_LOGIN_SUCCUSS) {
            this@LoginActivity.finish()
        }
    }

    override fun getOverridePendingTransitionMode(): BaseActivity.TransitionMode = BaseActivity.TransitionMode.DEFAULT

    override fun isBindEventBusHere(): Boolean = true

    override fun isBindNetworkListener(): Boolean = false
    override fun onNetworkConnected(type: NetUtils.NetType) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onNetworkDisConnected() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getContainerTargetView(): View? = null

    override fun onResume() {
        super.onResume()
        val userName = appPreferences.getString(Preferences.LOGIN_USER_NAME, "")
        accountEt.setText(userName)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_forget_al -> {
                skipActivity(FindPwdActivity::class.java)
            }
            R.id.tv_register_al -> {
                skipActivity(EmailRegisterActivity::class.java)
            }
            R.id.btn_login_al -> {
                if (loginBtn.validate()) {
                    val userName = accountEt.getContent()
                    val pwdStr = MD5Tool.getMd5_32(passwordEt.getContent())
                    LoginBean.login(accountEt.getContent(), pwdStr)
                            .compose(netTfWithDialog())
                            .subscribe({
                                //                                showToast(it.message)
                                if (it.result.token != null) {
                                    NzeApp.instance.userBean = it.result.cloneToUserBean()
                                    EventBus.getDefault().post(EventCenter<Boolean>(EventCode.CODE_LOGIN_SUCCUSS, true))
                                    appPreferences.put(Preferences.LOGIN_USER_NAME, userName)
                                    this@LoginActivity.finish()
                                }

                            }, {
                                showToast("登录失败,登录账号或者密码错误")
                            })
                }
            }
        }
    }
}
