package com.nze.nzexchange.controller.my.asset.recharge

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.TextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.TransactionListBean
import com.nze.nzexchange.bean2.RechargeHistoryBean
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.my.asset.withdraw.WithdrawDetailActivity
import com.nze.nzexchange.tools.TimeTool
import kotlinx.android.synthetic.main.activity_recharge_detail.*

class RechargeDetailActivity : NBaseActivity() {
    val amountTv: TextView by lazy { tv_recharge_amount_ard }
    val addressTv: TextView by lazy { tv_address_ard }
    val statusTv: TextView by lazy { tv_status_ard }
    val tradeIdTv: TextView by lazy { tv_trade_id_ard }
    val tradeHashTv: TextView by lazy { tv_trade_hash_ard }
    val dateTv: TextView by lazy { tv_date_awd }
    var historyBean: TransactionListBean? = null

    companion object {
        fun skip(context: Context, historyBean: TransactionListBean) {
            context.startActivity(Intent(context, RechargeDetailActivity::class.java)
                    .putExtra(IntentConstant.PARAM_DETAIL, historyBean)
            )
        }
    }

    override fun getRootView(): Int = R.layout.activity_recharge_detail

    override fun initView() {
        intent?.let {
            historyBean = it.getParcelableExtra(IntentConstant.PARAM_DETAIL)
        }

        historyBean?.let {
            amountTv.text = "${it.amount}${it.token}"
            addressTv.text = it.to
            statusTv.text = it.getStatus()
            tradeIdTv.text = it.blockNo
            tradeHashTv.text = it.txid
            dateTv.text = TimeTool.format(TimeTool.PATTERN6, it.createTime)

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
