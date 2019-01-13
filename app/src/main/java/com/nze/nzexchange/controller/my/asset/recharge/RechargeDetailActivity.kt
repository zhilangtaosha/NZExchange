package com.nze.nzexchange.controller.my.asset.recharge

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.bean2.RechargeHistoryBean
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.tools.TimeTool
import kotlinx.android.synthetic.main.activity_recharge_detail.*

class RechargeDetailActivity : NBaseActivity() {
    val currencyUnitTv:TextView by lazy { tv_currency_unit_ard }
    val currencyAmountTv:TextView by lazy { tv_currency_amount_ard }
    val dateTv:TextView by lazy { tv_date_ard }
    val addressTv:TextView by lazy { tv_address_ard }
    val statusTv:TextView by lazy { tv_status_ard }


    var historyBean: RechargeHistoryBean? = null

    override fun getRootView(): Int = R.layout.activity_recharge_detail

    override fun initView() {
        intent?.let {
            historyBean = it.getParcelableExtra(IntentConstant.PARAM_DETAIL)
        }

        historyBean?.let {
            currencyUnitTv.text=it.currency
            currencyAmountTv.text=it.rechargeAmount
            dateTv.text=TimeTool.format(TimeTool.PATTERN_DEFAULT,it.datetime)
            addressTv.text=it.address

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
