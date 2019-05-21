package com.nze.nzexchange.controller.my.asset.legal

import android.text.TextUtils.substring
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.NzeApp
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.LegalRechargeBean
import com.nze.nzexchange.bean.SetPayMethodBean
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.bean2.RechargeModeBean
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.my.paymethod.presenter.PayMethodPresenter
import com.nze.nzexchange.controller.my.paymethod.presenter.PayMethodView
import com.nze.nzexchange.extend.getContent
import com.nze.nzexchange.extend.setDrawables
import com.nze.nzexchange.tools.editjudge.EditLegalWatcher
import com.nze.nzexchange.widget.CommonButton
import com.nze.nzexchange.widget.CommonTopBar
import kotlinx.android.synthetic.main.activity_legal_recharge.*

class LegalRechargeActivity : NBaseActivity(), PayMethodView {
    val topBar: CommonTopBar by lazy { ctb_alr }
    val rechargeMode: TextView by lazy { tv_recharge_mode_alr }
    val amountEt: EditText by lazy {
        et_amount_alr
    }
    val nextBtn: CommonButton by lazy { btn_next_alr }
    val selectPopup: SelectRechargePopup by lazy {
        SelectRechargePopup(this).apply {
            onItemClick = {
                when (it.mode) {
                    RechargeModeBean.BANK -> {
                        if (!payMethodBean?.accmoneyBankcard.isNullOrEmpty()) {
                            val s = payMethodBean?.accmoneyBankcard!!
                            rechargeMode.setDrawables(R.mipmap.bank_icon, null, R.mipmap.right_arrow2, null)
                            rechargeMode.text = "${payMethodBean?.accmoneyBanktype}(${s.substring(s.length - 4)})"
                            rechargeType = RechargeModeBean.BANK
                        }
                    }
                    RechargeModeBean.OSKO -> {
                        if (!payMethodBean?.accmoneyFrBankcard.isNullOrEmpty()) {
                            rechargeMode.setDrawables(R.mipmap.osko_icon, null, R.mipmap.right_arrow2, null)
                            rechargeMode.text = "OSKO"
                            rechargeType = RechargeModeBean.OSKO
                        }
                    }
                    RechargeModeBean.BPAY -> {
                        if (!payMethodBean?.accmoneyBpaySn.isNullOrEmpty()) {
                            rechargeMode.setDrawables(R.mipmap.bpay_icon, null, R.mipmap.right_arrow2, null)
                            rechargeMode.text = "BPAY"
                            rechargeType = RechargeModeBean.BPAY
                        }
                    }
                }
            }
        }
    }
    val payMehodP by lazy { PayMethodPresenter(this, this) }
    var userBean = UserBean.loadFromApp()
    var payMethodBean: SetPayMethodBean? = null
    var checkpayAccount: String? = null
    var checkpayType: String? = LegalRechargeBean.TYPE_BANK
    var checkpayCode: String? = null
        get() {
            return "PD${System.currentTimeMillis()}"
        }
    var rechargeType = RechargeModeBean.BANK

    override fun getRootView(): Int = R.layout.activity_legal_recharge

    override fun initView() {
        topBar.setRightClick {
            skipActivity(LegalRechargeHistoryActivity::class.java)
        }
        amountEt.addTextChangedListener(EditLegalWatcher(amountEt))
        rechargeMode.setOnClickListener {
            selectPopup.showPopupWindow()
        }

        LegalRechargeBean.getLegalCode()
                .compose(netTf())
                .subscribe({
                    if (it.success)
                        checkpayCode = it.result
                }, onError)

        nextBtn.setOnCommonClick {
            if (checkpayAccount.isNullOrEmpty()) {
                showToast("请设置充值方式")
                return@setOnCommonClick
            }
            val amount = amountEt.getContent()
            if (amount.isNullOrEmpty() || amount.toDouble() == 0.0) {
                showToast("请输入充值金额")
                return@setOnCommonClick
            }
            LegalRechargeBean.legalRecharge(userBean!!, amount, null, null, null, null, checkpayType!!, checkpayCode!!)
                    .compose(netTfWithDialog())
                    .subscribe({
                        if (it.success) {
                            RechargeConfirmActivity.skip(this, rechargeType, checkpayCode!!, amount)
                            finish()
                        }
                    }, onError)
        }
    }

    override fun onResume() {
        super.onResume()
        getAllPayMethod()
    }

    fun getAllPayMethod() {
        payMehodP.getAllPayMethod(userBean!!, {
            if (it.success) {
                payMethodBean = it.result
                userBean!!.payMethod = payMethodBean
                NzeApp.instance.userBean = userBean
                payMethodBean?.let {
                    if (!it.accmoneyBankcard.isNullOrEmpty()) {
                        checkpayType = LegalRechargeBean.TYPE_BANK
                        rechargeType = RechargeModeBean.BANK
                        val s = it.accmoneyBankcard!!
                        checkpayAccount = s
                        rechargeMode.setDrawables(R.mipmap.bank_icon, null, R.mipmap.right_arrow2, null)
                        rechargeMode.text = "${it.accmoneyBanktype}(${s.substring(s.length - 4)})"
                    } else if (!it.accmoneyFrBankcard.isNullOrEmpty()) {
                        checkpayType = LegalRechargeBean.TYPE_OSKO
                        rechargeType = RechargeModeBean.OSKO
                        checkpayAccount = it.accmoneyFrBankcard
                        rechargeMode.setDrawables(R.mipmap.osko_icon, null, R.mipmap.right_arrow2, null)
                        rechargeMode.text = "OSKO"
                    } else if (!it.accmoneyBpaySn.isNullOrEmpty()) {
                        checkpayType = LegalRechargeBean.TYPE_BPAY
                        rechargeType = RechargeModeBean.BPAY
                        checkpayAccount = it.accmoneyBpaySn
                        rechargeMode.setDrawables(R.mipmap.bpay_icon, null, R.mipmap.right_arrow2, null)
                        rechargeMode.text = "BPAY"
                    } else {
                        rechargeMode.setDrawables(R.mipmap.add_icon, null, R.mipmap.right_arrow2, null)
                        rechargeMode.text = "请先绑定充值方式"
                    }
                }
                selectPopup.setRecharge(RechargeModeBean.getPayList(payMethodBean!!))
            }
        }, onError)
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

}
