package com.nze.nzexchange.controller.my.asset.withdraw

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ListView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.bean2.CoinAddressBean
import com.nze.nzexchange.controller.base.NBaseActivity
import kotlinx.android.synthetic.main.activity_selet_address_list.*

/***
 * 地址列表
 */
class SelectCurrencyAddressListActivity : NBaseActivity() {
    val selectLv: ListView by lazy { lv_select_asal }
    val selectAdapter: SelectCurrencyAddressAdapter by lazy { SelectCurrencyAddressAdapter(this, "ETH") }
    val addBtn: Button by lazy { btn_add_asal }


    override fun getRootView(): Int = R.layout.activity_selet_address_list

    override fun initView() {
        selectLv.adapter = selectAdapter
        selectAdapter.group = CoinAddressBean.getList()[0].coinAddress

        addBtn.setOnClickListener {

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
