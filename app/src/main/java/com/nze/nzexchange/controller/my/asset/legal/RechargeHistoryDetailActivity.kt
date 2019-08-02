package com.nze.nzexchange.controller.my.asset.legal

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.CompanyPaymentBean
import com.nze.nzexchange.bean.LegalRechargeBean
import com.nze.nzexchange.bean.LegalRechargeHistoryBean
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.my.asset.presenter.LegalP
import com.nze.nzexchange.extend.formatForLegal
import com.nze.nzexchange.extend.setDrawables
import com.nze.nzexchange.tools.TimeTool
import kotlinx.android.synthetic.main.activity_withdraw_reject.*

class RechargeHistoryDetailActivity : NBaseActivity() {
    val legalP: LegalP by lazy { LegalP(this) }
    val bankTv: TextView by lazy { tv_bank_aws }
    val amountTv: TextView by lazy { tv_amount_aws }
    val statusTv: TextView by lazy { tv_status_aws }
    val applyTv: TextView by lazy { tv_apply_aws }
    val applyTimeTv: TextView by lazy { tv_apply_time_aws }
    val processTimeTv: TextView by lazy { tv_process_time_aws }
    val rejectTimeTv: TextView by lazy { tv_reject_time_aws }
    val rejectReasonTv: TextView by lazy { tv_reject_reason_aws }
    val foundTimeTv: TextView by lazy { tv_found_time_aws }
    val orderNumTv: TextView by lazy { tv_order_num_aws }
    val processView: View by lazy { view_process_aws }
    val rejectTv: TextView by lazy { tv_reject_aws }
    val actionTv: TextView by lazy { tv_action_aws }
    val rejectIv: ImageView by lazy { iv_success_aws }
    val reasonKeyTv: TextView by lazy { tv_reason_key_aws }
    val companyPayList: MutableList<CompanyPaymentBean> by lazy { mutableListOf<CompanyPaymentBean>() }

    var historyBean: LegalRechargeHistoryBean? = null

    override fun getRootView(): Int = R.layout.activity_withdraw_reject


    companion object {
        fun skip(context: Context, bean: LegalRechargeHistoryBean) {
            context.startActivity(Intent(context, RechargeHistoryDetailActivity::class.java)
                    .putExtra(IntentConstant.PARAM_HISTORY, bean))
        }
    }

    override fun initView() {
        intent?.let {
            historyBean = it.getParcelableExtra(IntentConstant.PARAM_HISTORY)
        }

        historyBean?.let {
            actionTv.text = "转入进度"
            applyTv.text = "充值申请"
            bankTv.text = it.checkpayType

            amountTv.text = it.checkpayAmt.formatForLegal()
            statusTv.text = it.getStatus()
            orderNumTv.text = it.checkpayCode
            foundTimeTv.text = TimeTool.format(TimeTool.PATTERN2, it.checkpayCreateTime)


            applyTimeTv.text = TimeTool.format(TimeTool.PATTERN2, it.checkpayCreateTime)
            processTimeTv.text = TimeTool.format(TimeTool.PATTERN2, it.checkpayCreateTime)

            if (it.checkpayStatus == 101 || it.checkpayStatus == 102 || it.checkpayStatus == 990) {
                legalP.getCompanyPaymethod({ rs ->
                    if (rs.success) {
                        companyPayList.addAll(rs.result)
                        when (it.checkpayType) {
                            LegalRechargeBean.TYPE_BPAY -> {
                                bankTv.setDrawables(R.mipmap.bpay_icon, null, null, null)
                                val bpay = companyPayList[1]
                                rejectReasonTv.text = "BPAY账号(${bpay.contShow3})"
                                bankTv.text = bpay.contShow1
                            }
                            LegalRechargeBean.TYPE_BANK -> {
                                bankTv.setDrawables(R.mipmap.bank_icon, null, null, null)
                                val bank = companyPayList[2]
                                rejectReasonTv.text = "${bank.contShow1}(${bank.contShow3})"
                                bankTv.text = bank.contShow1
                            }
                            LegalRechargeBean.TYPE_OSKO -> {
                                bankTv.setDrawables(R.mipmap.osko_icon, null, null, null)
                                val osko = companyPayList[0]
                                rejectReasonTv.text = "OSKO账号(${osko.contShow3})"
                                bankTv.text = osko.contShow1
                            }
                        }
                    } else {
                        showToast(rs.message)
                    }
                }, onError)
            }
            when (it.checkpayStatus) {
                100 -> {//"待审核"

                }
                101, 102 -> {//"审核中"
                    processView.visibility = View.GONE
                    rejectTv.visibility = View.GONE
                    rejectIv.visibility = View.GONE
                    rejectTimeTv.visibility = View.GONE
                    reasonKeyTv.text = "转入账户"
                }
                990 -> {//"已完成"
                    reasonKeyTv.text = "转入账户"
                    rejectIv.setImageResource(R.mipmap.hook_icon3)
                    rejectTv.text = "充值完成"
                    rejectTimeTv.text = TimeTool.format(TimeTool.PATTERN2, it.auditDataFlowDataVos[2].flowTime)
                }
                9901 -> {//"审核失败"
                    rejectTv.text = "审核失败"
                    rejectTimeTv.text = TimeTool.format(TimeTool.PATTERN2, it.auditDataFlowDataVos[1].flowTime)
                    reasonKeyTv.text = "驳回原因"
                    rejectReasonTv.text = it.auditAuditdataDataVo.auditdataContent
                }
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
