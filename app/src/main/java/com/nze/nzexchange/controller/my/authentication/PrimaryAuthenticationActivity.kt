package com.nze.nzexchange.controller.my.authentication

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.TextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.RealNameAuthenticationBean
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.login.SelectCountryActivity
import com.nze.nzexchange.extend.getContent
import com.nze.nzexchange.http.CRetrofit
import com.nze.nzexchange.validation.EmptyValidation
import com.nze.nzexchange.validation.IDValidation
import com.nze.nzexchange.widget.CommonButton
import com.nze.nzexchange.widget.clearedit.ClearableEditText
import kotlinx.android.synthetic.main.activity_primary_authentication.*

/**
 * 初级认证
 */
class PrimaryAuthenticationActivity : NBaseActivity() {
    val countryTv: TextView by lazy { tv_country_apa }
    val nameEt: ClearableEditText by lazy { et_name_apa }
    val credentialsEt: ClearableEditText by lazy { et_credentials_apa }
    val nextBtn: CommonButton by lazy {
        btn_next_ap.apply {
            setOnCommonClick {
                //                skipActivity(PrimaryAuthenticationCompleteActivity::class.java)
                if (nextBtn.validate())
                    primaryAuthentication()
            }
        }
    }

    val REQUEST_CODE = 0x112
    var countryName: String = "中国"
        set(value) {
            field = value
            countryTv.text = value
        }
    var countryNumber = "+86"
    var userBean: UserBean? = UserBean.loadFromApp()
    var realNameAuthenticationBean: RealNameAuthenticationBean? = null

    override fun getRootView(): Int = R.layout.activity_primary_authentication

    override fun initView() {
        realNameAuthenticationBean = intent.getParcelableExtra<RealNameAuthenticationBean>(AuthenticationHomeActivity.INTENT_REAL_NAME_BEAN)

        nextBtn.initValidator()
                .add(nameEt, EmptyValidation())
                .add(credentialsEt, IDValidation())
                .executeValidator()

        countryTv.setOnClickListener {
            startActivityForResult(Intent(this@PrimaryAuthenticationActivity, SelectCountryActivity::class.java), REQUEST_CODE)
        }


    }

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {
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

    override fun getContainerTargetView(): View? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            data?.run {
                countryName = getStringExtra(IntentConstant.PARAM_COUNTRY_NAME)
                countryNumber = getStringExtra(IntentConstant.PARAM_COUNTRY_NUMBER)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun primaryAuthentication() {
        userBean!!.tokenReqVo.let {
            CRetrofit.instance
                    .userService()
                    .primaryAuthentication(it.tokenUserId, it.tokenUserKey, it.tokenSystreeId, nameEt.getContent(), credentialsEt.getContent(), countryNumber.substring(1))
                    .compose(netTfWithDialog())
                    .subscribe({
                        if (it.success) {
                            realNameAuthenticationBean?.membName = nameEt.getContent()
                            realNameAuthenticationBean?.membIdentitycard = credentialsEt.getContent()
                            realNameAuthenticationBean?.membCountry = countryNumber.substring(1)
                            startActivity(Intent(this, RealNameAuthenticationActivity::class.java)
                                    .putExtra(AuthenticationHomeActivity.INTENT_REAL_NAME_BEAN, realNameAuthenticationBean))

                            finish()
                        } else {
                            showToast(it.message)
                        }
                    }, onError)
        }

    }
}
