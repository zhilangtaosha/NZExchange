package com.nze.nzexchange.controller.my.paymethod

import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.NzeApp
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.SetPayMethodBean
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.config.EventCode
import com.nze.nzexchange.controller.base.NBaseActivity
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
    val nameValueTv: TextView by lazy { tv_name_value_aac }
    val cartNoValueEt: EditText by lazy { et_cardno_value_aac }
    val bankValueEt: EditText by lazy { et_bank_value_aac }
    val addressValueEt: EditText by lazy { et_address_value_aac }
    val saveBtn: CommonButton by lazy { btn_save_aac }
    val userBean: UserBean? by lazy { NzeApp.instance.userBean }


    override fun getRootView(): Int = R.layout.activity_add_card

    override fun initView() {
        userBean?.payMethod?.run {
            cartNoValueEt.setText(this.accmoneyBankcard?.getValue() ?: "")
            bankValueEt.setText(this.accmoneyBank?.getValue() ?: "")
            addressValueEt.setText(this.accmoneyBanktype?.getValue() ?: "")
        }
        saveBtn.initValidator()
                .add(cartNoValueEt, EmptyValidation())
                .add(bankValueEt, EmptyValidation())
                .add(addressValueEt, EmptyValidation())
                .executeValidator()
        saveBtn.setOnCommonClick {
            if (saveBtn.validate()) {
                userBean?.run {
                    SetPayMethodBean.setPayMethodNet(tokenReqVo.tokenUserId,
                            tokenReqVo.tokenUserKey,
                            addressValueEt.getContent(),
                            cartNoValueEt.getContent(),
                            bankValueEt.getContent(),
                            null, null, null, null)
                            .compose(netTfWithDialog())
                            .subscribe({
                                if (it.success) {
                                    val payMethodBean = it.result
                                    userBean?.payMethod?.accmoneyBank = payMethodBean.accmoneyBank
                                    userBean?.payMethod?.accmoneyBankcard = payMethodBean.accmoneyBankcard
                                    userBean?.payMethod?.accmoneyBanktype = payMethodBean.accmoneyBanktype
                                    NzeApp.instance.userBean = userBean
                                    this@AddBankActivity.finish()
                                }
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
