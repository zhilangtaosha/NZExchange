package com.nze.nzexchange.controller.login

import android.app.Activity
import android.content.Intent
import android.view.View
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.extend.setTextFromHtml
import kotlinx.android.synthetic.main.activity_email_register.*
import kotlinx.android.synthetic.main.activity_phone_register.*

class PhoneRegisterActivity : NBaseActivity(), View.OnClickListener {
    val REQUEST_CODE = 0x112
    var countryName = "中国"
    var countryNumber = "+86"


    override fun getRootView(): Int = R.layout.activity_phone_register

    override fun initView() {

        tv_to_login_apr.setTextFromHtml("已有账号？<font color=\"#6D87A8\">登录</font>")
        tv_go_email_apr.setOnClickListener(this)
        tv_to_login_apr.setOnClickListener(this)
        tv_country_apr.setOnClickListener(this)
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
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            data?.run {
                countryName = getStringExtra(IntentConstant.PARAM_COUNTRY_NAME)
                countryNumber = getStringExtra(IntentConstant.PARAM_COUNTRY_NUMBER)
            }
            tv_country_apr.text = countryName
            tv_country_code_apr.text = countryNumber
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
