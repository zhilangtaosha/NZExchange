package com.nze.nzexchange.controller.my.asset

import android.view.View
import android.widget.Button
import android.widget.TextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.my.asset.recharge.RechargeCurrencyActivity
import com.nze.nzexchange.controller.my.asset.withdraw.WithdrawCurrencyActivity
import kotlinx.android.synthetic.main.activity_concrete_currency_asset.*

/**
 * 具体某种币的资产页面
 */
class ConcreteCurrencyAssetActivity : NBaseActivity(), View.OnClickListener {


    val currencyNameTv: TextView by lazy { tv_currency_name_acca }
    val availableValueTv: TextView by lazy { tv_available_value_acca }
    val freezValueTv: TextView by lazy { tv_freeze_value_acca }
    val moneyValueTv: TextView by lazy { tv_money_value_acca }
    val rechargeBtn: Button by lazy { btn_recharge_acca }
    val withdrawBtn: Button by lazy { btn_withdraw_acca }

    override fun getRootView(): Int = R.layout.activity_concrete_currency_asset

    override fun initView() {
        rechargeBtn.setOnClickListener(this)
        withdrawBtn.setOnClickListener(this)
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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_recharge_acca -> {
                skipActivity(RechargeCurrencyActivity::class.java)
            }
            R.id.btn_withdraw_acca -> {
                skipActivity(WithdrawCurrencyActivity::class.java)
            }
        }
    }

}
