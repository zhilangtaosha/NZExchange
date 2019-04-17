package com.nze.nzexchange.controller.my.asset

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.UserAssetBean
import com.nze.nzexchange.bean2.ConcreteAssetBean
import com.nze.nzexchange.config.AccountType
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.my.asset.recharge.RechargeCurrencyActivity
import com.nze.nzexchange.controller.my.asset.transfer.TransferActivity
import com.nze.nzexchange.controller.my.asset.withdraw.WithdrawCurrencyActivity
import com.nze.nzexchange.extend.formatForCurrency
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
    val transferBtn: Button by lazy { btn_transfer_acca }
    val tradeBtn: Button by lazy { btn_trade_acca }
    val view1: View by lazy { view1_acca }
    val view2: View by lazy { view2_acca }
    val view3: View by lazy { view3_acca }

    val listView: ListView by lazy { listView_acca }
    val concreteAdapter: ConcreteAssetAdapter by lazy { ConcreteAssetAdapter(this) }
    var userAssetBean: UserAssetBean? = null
    var type: Int = AccountType.BIBI
    var bundle: Bundle? = null
    lateinit var otcList: ArrayList<UserAssetBean>
    lateinit var bibiList: ArrayList<UserAssetBean>

    companion object {
        fun skip(context: Context, type: Int, userAssetBean: UserAssetBean, otcList: ArrayList<UserAssetBean>, bibiList: ArrayList<UserAssetBean>) {
            val intent = Intent(context, ConcreteCurrencyAssetActivity::class.java)
            val bundle = Bundle()
            bundle.putInt(IntentConstant.PARAM_TYPE, type)
            bundle.putParcelable(IntentConstant.PARAM_ASSET, userAssetBean)
            bundle.putParcelableArrayList(IntentConstant.PARAM_OTC_ACCOUNT, otcList)
            bundle.putParcelableArrayList(IntentConstant.PARAM_BIBI_ACCOUNT, bibiList)
            intent.putExtra(IntentConstant.PARAM_BUNDLE, bundle)
            context.startActivity(intent)
        }
    }

    override fun getRootView(): Int = R.layout.activity_concrete_currency_asset

    override fun initView() {
        intent?.let {
            bundle = it.getBundleExtra(IntentConstant.PARAM_BUNDLE)
        }
        bundle?.let {
            userAssetBean = it.getParcelable(IntentConstant.PARAM_ASSET)
            type = it.getInt(IntentConstant.PARAM_TYPE)
            otcList = it.getParcelableArrayList(IntentConstant.PARAM_OTC_ACCOUNT)
            bibiList = it.getParcelableArrayList(IntentConstant.PARAM_BIBI_ACCOUNT)
        }
        userAssetBean?.let {
            currencyNameTv.text = it.currency
            availableValueTv.text = it.available.formatForCurrency()
            freezValueTv.text = it.freeze.toString()

        }

        listView.adapter = concreteAdapter
        concreteAdapter.group = ConcreteAssetBean.getList()

        rechargeBtn.setOnClickListener(this)
        withdrawBtn.setOnClickListener(this)
        transferBtn.setOnClickListener(this)
        tradeBtn.setOnClickListener(this)
        swithLayout(type)
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
                startActivity(Intent(this@ConcreteCurrencyAssetActivity, RechargeCurrencyActivity::class.java)
                        .putExtra(IntentConstant.PARAM_ASSET, userAssetBean))
            }
            R.id.btn_withdraw_acca -> {
                startActivity(Intent(this@ConcreteCurrencyAssetActivity, WithdrawCurrencyActivity::class.java)
                        .putExtra(IntentConstant.PARAM_ASSET, userAssetBean))
            }
            R.id.btn_transfer_acca -> {
                TransferActivity.skip(this, bundle!!)
            }
            R.id.btn_trade_acca -> {
            }
        }
    }


    fun swithLayout(type: Int) {
        if (type == AccountType.OTC) {
            rechargeBtn.visibility = View.GONE
            view1.visibility = View.GONE
            withdrawBtn.visibility = View.GONE
            view2.visibility = View.GONE
            view3.visibility = View.VISIBLE
            tradeBtn.visibility = View.VISIBLE
        }
        if (!isContain()) {
            view2.visibility = View.GONE
            transferBtn.visibility = View.GONE
            view3.visibility = View.GONE
        }
    }

    fun isContain(): Boolean {
        var o = false
        for (it in otcList) {
            if (it.currency == userAssetBean!!.currency) {
                o = true
            }
        }
        var b = false
        for (it in bibiList) {
            if (it.currency == userAssetBean!!.currency) {
                b = true
            }
        }
        return o && b
    }
}
