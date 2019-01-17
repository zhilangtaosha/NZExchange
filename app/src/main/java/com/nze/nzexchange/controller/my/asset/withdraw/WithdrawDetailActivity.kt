package com.nze.nzexchange.controller.my.asset.withdraw

import android.view.View
import android.widget.TextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.TickRecordBean
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.tools.TimeTool
import kotlinx.android.synthetic.main.activity_withdraw_detail.*

class WithdrawDetailActivity : NBaseActivity() {
    val withdrawAmountTv: TextView by lazy { tv_withdraw_amount_awd }
    val styleTv: TextView by lazy { tv_style_awd }
    val addressTv: TextView by lazy { tv_address_awd }
    val statusTv: TextView by lazy { tv_status_awd }
    val serviceChargeTv: TextView by lazy { tv_service_charge_awd }
    val tradeIdTv: TextView by lazy { tv_trade_id_awd }
    val dateTv: TextView by lazy { tv_date_awd }

    var tickRecordBean: TickRecordBean? = null
    var currecy: String? = null

    override fun getRootView(): Int = R.layout.activity_withdraw_detail

    override fun initView() {
        intent?.let {
            tickRecordBean = it.getParcelableExtra(IntentConstant.PARAM_DETAIL)
            currecy = it.getStringExtra(IntentConstant.PARAM_CURRENCY)
        }

        tickRecordBean?.let {
            withdrawAmountTv.text = "-${it.number}${currecy}"
            addressTv.text = it.to
            serviceChargeTv.text = it.fee.toString()
            tradeIdTv.text = it.txid
            dateTv.text = TimeTool.format(TimeTool.PATTERN6, it.datetime)
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
