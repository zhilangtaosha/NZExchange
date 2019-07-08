package com.nze.nzexchange.controller.my.asset.legal

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.ui.BaseActivity
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.*
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.my.asset.presenter.LegalP
import com.nze.nzexchange.controller.my.paymethod.presenter.PayMethodPresenter
import com.nze.nzexchange.controller.my.paymethod.presenter.PayMethodView
import com.nze.nzexchange.extend.formatForLegal
import com.nze.nzexchange.extend.setDrawables
import com.nze.nzexchange.tools.TimeTool
import kotlinx.android.synthetic.main.activity_withdraw_reject.*

class WithdrawHistoryDetailActivity : NBaseActivity(), PayMethodView {
    val payMehodP: PayMethodPresenter by lazy { PayMethodPresenter(this, this) }
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

    var historyBean: LegalWithdrawHistoryBean? = null
    var userBean = UserBean.loadFromApp()

    override fun getRootView(): Int = R.layout.activity_withdraw_reject


    companion object {
        fun skip(context: Context, bean: LegalWithdrawHistoryBean) {
            context.startActivity(Intent(context, WithdrawHistoryDetailActivity::class.java)
                    .putExtra(IntentConstant.PARAM_HISTORY, bean))
        }
    }

    override fun initView() {
        intent?.let {
            historyBean = it.getParcelableExtra(IntentConstant.PARAM_HISTORY)
        }

        historyBean?.let {
            actionTv.text = "转出进度"
            applyTv.text = "提现申请"
//            bankTv.text = it.checkpayType

            amountTv.text = it.pickfundApplyamt.formatForLegal()
            statusTv.text = it.getStatus()
            orderNumTv.text = it.pickfundCode
            foundTimeTv.text = TimeTool.format(TimeTool.PATTERN2, it.pickfundCreateTime)

            applyTimeTv.text = TimeTool.format(TimeTool.PATTERN2, it.pickfundCreateTime)
            processTimeTv.text = TimeTool.format(TimeTool.PATTERN2, it.pickfundCreateTime)

            when (it.pickfundStatus) {
                100 -> {//"待审核"

                }
                101, 102 -> {//"审核中"
                    processView.visibility = View.GONE
                    rejectTv.visibility = View.GONE
                    rejectIv.visibility = View.GONE
                    rejectTimeTv.visibility = View.GONE
                    reasonKeyTv.text = "转出账户"
                }
                990 -> {//"已完成"
                    reasonKeyTv.text = "转出账户"
                    rejectIv.setImageResource(R.mipmap.hook_icon3)
                    rejectTv.text = "提现完成"
                    rejectTimeTv.text = TimeTool.format(TimeTool.PATTERN2, it.auditDataFlowDataVos[2].flowTime)
                }
                9901 -> {//"审核失败"
                    rejectTv.text = "审核失败"
                    rejectTimeTv.text = TimeTool.format(TimeTool.PATTERN2, it.auditDataFlowDataVos[1].flowTime)
                    reasonKeyTv.text = "驳回原因"
                    rejectReasonTv.text = it.auditAuditdataDataVo?.auditdataContent
                }
            }

            payMehodP.getAllPayMethod(userBean!!, { rs ->
                val payMethodBean = rs.result
                if (!payMethodBean.accmoneyBankcard.isNullOrEmpty()) {
                    bankTv.setDrawables(R.mipmap.bank_icon, null, null, null)
                    bankTv.text = payMethodBean.accmoneyBanktype
                    if (it.pickfundStatus == 101 || it.pickfundStatus == 102 || it.pickfundStatus == 990) {
                        val s = payMethodBean.accmoneyBankcard!!
                        rejectReasonTv.text = "${payMethodBean.accmoneyBanktype}(${s.substring(s.length - 4)})"
                    }
                }
            }, onError)
        }

    }

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {
    }

    override fun getOverridePendingTransitionMode(): BaseActivity.TransitionMode = BaseActivity.TransitionMode.DEFAULT

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
