package com.nze.nzexchange.controller.my.asset

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ListView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.controller.base.NBaseActivity
import kotlinx.android.synthetic.main.activity_select_currency.*

class SelectCurrencyActivity : NBaseActivity() {
    val listView: ListView by lazy { lv_currency_asc }
    val currencyAdapter: SelectCurrencyAdapter by lazy { SelectCurrencyAdapter(this) }

    val currencyList: MutableList<String>
            by lazy { mutableListOf<String>("USDT", "BTC", "LTC", "ETH", "BCH", "DASH", "ETC", "EOS") }

    override fun getRootView(): Int = R.layout.activity_select_currency

    override fun initView() {
        listView.adapter = currencyAdapter
        currencyAdapter.group = currencyList
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
