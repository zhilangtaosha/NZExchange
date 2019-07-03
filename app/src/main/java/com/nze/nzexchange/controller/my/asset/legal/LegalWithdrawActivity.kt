package com.nze.nzexchange.controller.my.asset.legal

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.jakewharton.rxbinding2.widget.RxTextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.*
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.common.FundPasswordPopup
import com.nze.nzexchange.controller.common.TipDialog
import com.nze.nzexchange.controller.common.VerifyPopup
import com.nze.nzexchange.controller.my.paymethod.presenter.PayMethodPresenter
import com.nze.nzexchange.controller.my.paymethod.presenter.PayMethodView
import com.nze.nzexchange.extend.*
import com.nze.nzexchange.http.CRetrofit
import com.nze.nzexchange.tools.editjudge.EditLegalWatcher
import com.nze.nzexchange.validation.EmptyValidation
import com.nze.nzexchange.widget.CommonButton
import com.nze.nzexchange.widget.CommonTopBar
import kotlinx.android.synthetic.main.activity_legal_withdraw.*

class LegalWithdrawActivity : NBaseActivity(), PayMethodView {
    val topBar: CommonTopBar by lazy { ctb_alw }
    val bankCardTv: TextView by lazy { tv_bank_card_alw }
    val limitTv: TextView by lazy { tv_limit_alw }
    val moneyEt: EditText by lazy {
        et_money_alw.apply {
            addTextChangedListener(EditLegalWatcher(this))
        }
    }
    val availableTv: TextView by lazy { tv_available_alw }
    val feeTv: TextView by lazy { tv_fee_alw }
    val nextBtn: CommonButton by lazy { btn_next }
    var userBean = UserBean.loadFromApp()
    val pmp: PayMethodPresenter by lazy { PayMethodPresenter(this, this) }
    var verifyCode: String? = null
    var verifyId: String? = null
    val verifyPopup by lazy {
        VerifyPopup(this).apply {
            account = if (!userBean!!.userPhone.isNullOrEmpty()) {
                userBean!!.userPhone
            } else {
                userBean!!.userEmail
            }
            onConfirmClick = { code, checkcodeId ->
                verifyCode = code
                verifyId = checkcodeId
                fundPopup.showPopupWindow()
            }
        }
    }
    val fundPopup: FundPasswordPopup by lazy {
        FundPasswordPopup(this).apply {
            onPasswordClick = {
                withdraw(it)
            }
        }
    }
    var feeList: MutableList<LegalFeeBean> = mutableListOf()
    lateinit var feeBean: LegalFeeBean
    lateinit var setPayMethodBean: SetPayMethodBean
    var realNameAuthenticationBean: RealNameAuthenticationBean? = null
    lateinit var accountBean: LegalAccountBean
    var lowLimit: Double = 0.0
    var hightLimit: Double = 0.0

    companion object {
        fun skip(context: Context, realNameAuthenticationBean: RealNameAuthenticationBean, accountBean: LegalAccountBean) {
            context.startActivity(Intent(context, LegalWithdrawActivity::class.java)
                    .putExtra(IntentConstant.PARAM_AUTHENTICATION, realNameAuthenticationBean)
                    .putExtra(IntentConstant.PARAM_ACCOUNT, accountBean)
            )
        }
    }

    override fun getRootView(): Int = R.layout.activity_legal_withdraw

    override fun initView() {
        intent?.let {
            realNameAuthenticationBean = it.getParcelableExtra(IntentConstant.PARAM_AUTHENTICATION)
            accountBean = it.getParcelableExtra(IntentConstant.PARAM_ACCOUNT)
        }
        topBar.setRightClick {
            skipActivity(LegalWithdrawHistoryActivity::class.java)
        }

        nextBtn.initValidator()
                .add(moneyEt, EmptyValidation())
                .executeValidator()
        nextBtn.setOnCommonClick {
            val money = moneyEt.getContent()
            if (!money.isNullOrEmpty()) {
                val m = money.toDouble()
                if (m > accountBean.accAbleAmount) {
                    showToast("当前账号可提现额为${accountBean.accAbleAmount}CNY")
                    return@setOnCommonClick
                }
                if (m < lowLimit) {
                    showToast("单次提现最小额为 ¥$lowLimit")
                    return@setOnCommonClick
                }
                if (m > hightLimit) {
                    showToast("单次提现最大额为 ¥$hightLimit")
                    return@setOnCommonClick
                }
                if (m > 0) {
                    verifyPopup.showPopupWindow()
                } else {
                    showToast("提现金额必须大于0")
                }

            }

        }

        RxTextView.textChanges(moneyEt)
                .subscribe {
                    if (!it.isNullOrEmpty()) {
                        var m = it.toString().toDouble()
//                        if (m > feeBean.feeAmthigh) {
//                            showToast("单日交易限额 ¥${feeBean.feeAmthigh}")
//                        }
                        var rate = 0.0
                        for (fee in feeList) {
                            if (m >= fee.feeAmtlow && m <= fee.feeAmthigh) {
                                rate = fee.feeRate
                                break
                            }
                        }
                        val rateMoney = (m * rate).formatForLegal2()
                        feeTv.text = "手续费 ${rateMoney}CNY"
                        availableTv.text = "可转金额${m.sub(rateMoney).formatForLegal()}CNY"
                    } else {
                        feeTv.text = "手续费 --CNY"
                        availableTv.text = "可转金额0CNY"
                    }
                }

        CRetrofit.instance
                .userService()
                .getLegalFee()
                .compose(netTfWithDialog())
                .subscribe({
                    if (it.success) {
                        feeList.addAll(it.result)
                        if (feeList.size > 0) {
                            val len = feeList.size
                            lowLimit = feeList[0].feeAmtlow
                            hightLimit = feeList[len - 1].feeAmthigh
                            limitTv.text = "单次交易限额 ¥$hightLimit"
                        }
                    }
                }, onError)

    }

    override fun onResume() {
        super.onResume()
        pmp.getAllPayMethod(userBean!!, {
            if (it.success) {
                setPayMethodBean = it.result
                if (!pmp.isAddBank(setPayMethodBean)) {
                    TipDialog.getInstance(this)
                            .show("提现必须设置银行收款方式", false, {
                                finish()
                            })
                } else {
                    val s = setPayMethodBean.accmoneyBankcard!!
                    bankCardTv.text = "${setPayMethodBean.accmoneyBanktype}(${s.substring(s.length - 4)})"
                }
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

    fun withdraw(pwd: String) {
        CRetrofit.instance
                .userService()
                .legalWithdraw(userBean!!.tokenReqVo.tokenUserId,
                        userBean!!.tokenReqVo.tokenUserKey,
                        moneyEt.getContent(),
                        setPayMethodBean.accmoneyBanktype!!,
                        setPayMethodBean.accmoneyBankcard!!,
                        realNameAuthenticationBean?.membName!!,
                        "",
                        pwd,
                        verifyId!!,
                        verifyCode!!
                ).compose(netTfWithDialog())
                .subscribe({
                    if (it.success) {
                        skipActivity(WithdrawPendingActivity::class.java)
                        finish()
                    } else {
                        showToast(it.message)
                    }
                }, onError)
    }
}
