package com.nze.nzexchange.controller.my.paymethod

import android.content.Context
import android.content.Intent
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.NzeApp
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.RealNameAuthenticationBean
import com.nze.nzexchange.bean.SetPayMethodBean
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.config.EventCode
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.config.PayMethod
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.common.FundPasswordPopup
import com.nze.nzexchange.controller.my.authentication.AuthenticationHomeActivity
import com.nze.nzexchange.controller.my.paymethod.presenter.PayMethodPresenter
import com.nze.nzexchange.extend.getContent
import com.nze.nzexchange.extend.getValue
import com.nze.nzexchange.validation.EmptyValidation
import com.nze.nzexchange.widget.CommonButton
import kotlinx.android.synthetic.main.activity_add_card.*
import org.greenrobot.eventbus.EventBus

/**
 * 添加银行卡
 */
class AddBankActivity : NBaseActivity() {
    val payMethodP by lazy { PayMethodPresenter(this) }
    val nameValueTv: TextView by lazy { tv_name_value_aac }
    val cartNoValueEt: EditText by lazy { et_cardno_value_aac }
    val bankValueEt: EditText by lazy { et_bank_value_aac }
    val addressValueEt: EditText by lazy { et_address_value_aac }
    val saveBtn: CommonButton by lazy { btn_save_aac }
    val userBean: UserBean? by lazy { NzeApp.instance.userBean }
    var realNameAuthenticationBean: RealNameAuthenticationBean? = null
    val fundPopup: FundPasswordPopup by lazy {
        FundPasswordPopup(this).apply {
            onPasswordClick = {
                setPayMethod(it)
            }
        }
    }

    companion object {
        fun skip(context: Context, authenticationBean: RealNameAuthenticationBean) {
            context.startActivity(Intent(context, AddBankActivity::class.java)
                    .putExtra(IntentConstant.INTENT_REAL_NAME_BEAN, authenticationBean))
        }
    }

    override fun getRootView(): Int = R.layout.activity_add_card

    override fun initView() {
        realNameAuthenticationBean = intent.getParcelableExtra<RealNameAuthenticationBean>(IntentConstant.INTENT_REAL_NAME_BEAN)
        realNameAuthenticationBean?.let {
            nameValueTv.text = it.membName
        }
        userBean?.payMethod?.run {
            cartNoValueEt.setText(this.accmoneyBankcard?.getValue() ?: "")
            bankValueEt.setText(this.accmoneyBanktype?.getValue() ?: "")
            addressValueEt.setText(this.accmoneyBank?.getValue() ?: "")
        }
        saveBtn.initValidator()
                .add(cartNoValueEt, EmptyValidation())
                .add(bankValueEt, EmptyValidation())
                .add(addressValueEt, EmptyValidation())
                .executeValidator()
        saveBtn.setOnCommonClick {
            fundPopup.showPopupWindow()
        }

        payMethodP.getTransaction(userBean!!)
                .compose(netTfWithDialog())
                .subscribe({
                    if (!it.success) {
                        showToast(it.message)
                        saveBtn.visibility = View.GONE
                        addressValueEt.inputType = InputType.TYPE_NULL
                        cartNoValueEt.inputType = InputType.TYPE_NULL
                        bankValueEt.inputType = InputType.TYPE_NULL

                    }
                }, onError)
    }

    fun setPayMethod(pwd: String) {
        userBean?.run {
            SetPayMethodBean.setPayMethodNet(tokenReqVo.tokenUserId,
                    tokenReqVo.tokenUserKey,
                    addressValueEt.getContent(),
                    cartNoValueEt.getContent(),
                    bankValueEt.getContent(),
                    null, null, null, null
                    , pwd, null, null, null, null, null)
                    .compose(netTfWithDialog())
                    .subscribe({
                        if (it.success) {
                            val payMethodBean = it.result
                            userBean?.payMethod?.accmoneyBank = payMethodBean.accmoneyBank
                            userBean?.payMethod?.accmoneyBankcard = payMethodBean.accmoneyBankcard
                            userBean?.payMethod?.accmoneyBanktype = payMethodBean.accmoneyBanktype
                            NzeApp.instance.userBean = userBean
                            this@AddBankActivity.finish()
                        } else {
                            showToast(it.message)
                        }
                    }, onError)

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
