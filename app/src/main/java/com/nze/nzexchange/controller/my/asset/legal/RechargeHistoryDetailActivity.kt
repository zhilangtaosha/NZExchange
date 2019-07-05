package com.nze.nzexchange.controller.my.asset.legal

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.TextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.LegalRechargeHistoryBean
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.extend.formatForLegal
import kotlinx.android.synthetic.main.activity_withdraw_reject.*

class RechargeHistoryDetailActivity : NBaseActivity() {
    val bankTv: TextView by lazy { tv_bank_aws }
    val amountTv: TextView by lazy { tv_amount_aws }
    val statusTv: TextView by lazy { tv_status_aws }
    val applyTimeTv: TextView by lazy { tv_apply_time_aws }
    val processTimeTv: TextView by lazy { tv_process_time_aws }
    val rejectTimeTv: TextView by lazy { tv_reject_time_aws }
    val rejectReasonTv: TextView by lazy { tv_reject_reason_aws }
    val foundTimeTv: TextView by lazy { tv_found_time_aws }
    val orderNumTv: TextView by lazy { tv_order_num_aws }

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
            amountTv.text = it.checkpayAmt.formatForLegal()
            statusTv.text=it.getStatus()
            
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
