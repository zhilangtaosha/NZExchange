package com.nze.nzexchange.controller.market

import android.view.View
import android.widget.ListView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.widget.pulltorefresh.PullToRefreshListView
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.TransactionPairBean
import com.nze.nzexchange.controller.base.NBaseFragment
import kotlinx.android.synthetic.main.fragment_market_content.view.*

class MarketContentFragment : NBaseFragment() {
    lateinit var ptrLv: PullToRefreshListView
    val lvAdapter: MarketLvAdapter by lazy {
        MarketLvAdapter(activity!!)
    }

    override fun getRootView(): Int = R.layout.fragment_market_content

    override fun initView(rootView: View) {
        ptrLv = rootView.ptrlv_fmc
        val listView: ListView = ptrLv.refreshableView
        listView.adapter = lvAdapter
        lvAdapter.group = TransactionPairBean.getList()
    }

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {
    }

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