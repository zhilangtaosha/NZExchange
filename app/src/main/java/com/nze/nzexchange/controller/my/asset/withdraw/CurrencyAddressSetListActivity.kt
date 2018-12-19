package com.nze.nzexchange.controller.my.asset.withdraw

import android.view.View
import android.widget.ListView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.bean2.CoinAddressBean
import com.nze.nzexchange.controller.base.NBaseActivity
import kotlinx.android.synthetic.main.activity_coin_address_set_list.*

/**
 * 地址设置列表
 */
class CurrencyAddressSetListActivity : NBaseActivity() {

    val coinAddressLv: ListView by lazy { lv_coin_address_acasl }
    val coinAddressAdapter: CurrencyAddressSetAdapter by lazy { CurrencyAddressSetAdapter(this) }

    override fun getRootView(): Int = R.layout.activity_coin_address_set_list

    override fun initView() {
        coinAddressLv.adapter = coinAddressAdapter
        coinAddressAdapter.group = CoinAddressBean.getList()

        coinAddressAdapter.setClickListener {
            skipActivity(SelectCurrencyAddressListActivity::class.java)
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

    override fun getContainerTargetView(): View? = null

}
