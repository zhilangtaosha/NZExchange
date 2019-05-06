package com.nze.nzexchange.controller.my.asset.withdraw

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.view.View
import android.widget.TextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.TransactionListBean
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.extend.formatForCurrency
import com.nze.nzexchange.tools.TimeTool
import kotlinx.android.synthetic.main.activity_withdraw_detail.*

class WithdrawDetailActivity : NBaseActivity() {
    val withdrawAmountTv: TextView by lazy { tv_withdraw_amount_awd }
    val styleTv: TextView by lazy { tv_style_awd }
    val addressKeyTv: TextView by lazy { tv_address_key_awd }
    val addressTv: TextView by lazy { tv_address_awd }
    val statusTv: TextView by lazy { tv_status_awd }
    val serviceChargeTv: TextView by lazy { tv_service_charge_awd }
    val tradeIdTv: TextView by lazy { tv_trade_id_awd }
    val tradeHashTv: TextView by lazy { tv_trade_hash_awd }
    val dateTv: TextView by lazy { tv_date_awd }


    var recordBean: TransactionListBean? = null
    var currecy: String? = null
    var type = TYPE_RECHARGE

    companion object {
        val TYPE_RECHARGE = 0
        val TYPE_WITHDRAW = 1
        fun skip(context: Context, recordBean: TransactionListBean, currency: String, type: Int) {
            context.startActivity(Intent(context, WithdrawDetailActivity::class.java)
                    .putExtra(IntentConstant.PARAM_DETAIL, recordBean)
                    .putExtra(IntentConstant.PARAM_CURRENCY, currency)
                    .putExtra(IntentConstant.PARAM_TYPE, type)
            )
        }
    }

    override fun getRootView(): Int = R.layout.activity_withdraw_detail

    override fun initView() {
        intent?.let {
            recordBean = it.getParcelableExtra(IntentConstant.PARAM_DETAIL)
            currecy = it.getStringExtra(IntentConstant.PARAM_CURRENCY)
            type = it.getIntExtra(IntentConstant.PARAM_TYPE, TYPE_RECHARGE)
        }

        if (type == TYPE_RECHARGE) {
            addressKeyTv.text = "充币地址"
        } else {
            addressKeyTv.text = "提币地址"
        }

        recordBean?.let {
            withdrawAmountTv.text = "-${it.amount.formatForCurrency()}${currecy}"
            addressTv.text = it.to
            serviceChargeTv.text = it.fee.formatForCurrency()
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
