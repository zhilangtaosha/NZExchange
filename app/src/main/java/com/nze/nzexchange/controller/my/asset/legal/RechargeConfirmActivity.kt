package com.nze.nzexchange.controller.my.asset.legal

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.CompanyPaymentBean
import com.nze.nzexchange.bean.LegalRechargeBean
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.bean2.RechargeModeBean
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.controller.base.NBaseActivity
import kotlinx.android.synthetic.main.activity_recharge_confirm.*

/**
 * 法币充值确认页面
 */
class RechargeConfirmActivity : NBaseActivity(), View.OnClickListener {


    val moneyTv: TextView by lazy { tv_money_arc }
    val bankNameLayout: LinearLayout by lazy { layout_bank_name_arc }
    val bankNameTv: TextView by lazy { tv_bank_name_arc }
    val accountKeyTv: TextView by lazy { tv_account_key_arc }
    val accountValueTv: TextView by lazy { tv_account_value_arc }
    val copyIv: ImageView by lazy { iv_copy_arc }
    val payeeTv: TextView by lazy { tv_payee_arc }
    val codeTv: TextView by lazy { tv_code_arc }
    val cancelTv: TextView by lazy { tv_cancel_arc }
    val confirmTv: TextView by lazy { tv_confirm_arc }
    var type: String? = null
    var code: String? = null
    var amount: String? = null
    val companyPayList: MutableList<CompanyPaymentBean> by lazy { mutableListOf<CompanyPaymentBean>() }
    var userBean = UserBean.loadFromApp()

    companion object {
        fun skip(context: Context, type: String, code: String, amount: String) {
            context.startActivity(Intent(context, RechargeConfirmActivity::class.java)
                    .putExtra(IntentConstant.PARAM_TYPE, type)
                    .putExtra(IntentConstant.PARAM_CODE, code)
                    .putExtra(IntentConstant.PARAM_ACCOUNT, amount)
            )
        }
    }

    override fun getRootView(): Int = R.layout.activity_recharge_confirm
    override fun initView() {
        intent?.let {
            type = it.getStringExtra(IntentConstant.PARAM_TYPE)
            code = it.getStringExtra(IntentConstant.PARAM_CODE)
            amount = it.getStringExtra(IntentConstant.PARAM_ACCOUNT)
        }

        moneyTv.text = "¥${amount}"
        codeTv.text = "$code"
        CompanyPaymentBean.getCompanyPaymethod()
                .compose(netTfWithDialog())
                .subscribe({
                    if (it.success) {
                        companyPayList.addAll(it.result)
                        when (type) {
                            LegalRechargeBean.TYPE_BANK -> {
                                val bank = companyPayList[2]
                                bankNameTv.text = "${bank.contShow1}"
                                accountValueTv.text = "${bank.contShow3}"
                                payeeTv.text = "${bank.contShow4}"
                            }
                            LegalRechargeBean.TYPE_BPAY -> {
                                val bpay = companyPayList[1]
                                bankNameLayout.visibility = View.GONE
                                accountKeyTv.text = "BPAY账号"
                                accountValueTv.text = "${bpay.contShow3}"
                                payeeTv.text = "${bpay.contShow4}"
                            }
                            LegalRechargeBean.TYPE_OSKO  -> {
                                val osko = companyPayList[0]
                                bankNameLayout.visibility = View.GONE
                                accountKeyTv.text = "OSKO账号"
                                accountValueTv.text = "${osko.contShow3}"
                                payeeTv.text = "${osko.contShow4}"
                            }
                        }
                    } else {
                        showToast(it.message)
                    }
                }, onError)

        cancelTv.setOnClickListener(this)
        confirmTv.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_cancel_arc -> {
                finish()
            }
            R.id.tv_confirm_arc -> {
                LegalRechargeBean.legalRecharge(userBean!!, amount!!, null, null, null, null, type!!, code!!)
                        .compose(netTfWithDialog())
                        .subscribe({
                            if (it.success) {
                                finish()
                            }
                        }, onError)
            }
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


}
