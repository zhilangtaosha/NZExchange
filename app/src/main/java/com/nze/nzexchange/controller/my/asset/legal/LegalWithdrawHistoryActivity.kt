package com.nze.nzexchange.controller.my.asset.legal

import android.view.View
import android.widget.ListView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.widget.pulltorefresh.PullToRefreshListView
import com.nze.nzeframework.widget.pulltorefresh.internal.PullToRefreshBase
import com.nze.nzexchange.R
import com.nze.nzexchange.controller.base.NBaseActivity
import kotlinx.android.synthetic.main.activity_legal_withdraw_history.*

class LegalWithdrawHistoryActivity : NBaseActivity(), PullToRefreshBase.OnRefreshListener<ListView> {


    val ptrLv: PullToRefreshListView by lazy { ptrlv_alwh }
    lateinit var listView: ListView
    val historyAdapter by lazy { LegalWithdrawHistoryAdapter(this) }
    val list: MutableList<String> by lazy {
        mutableListOf<String>().apply {
            for (i in 1..10) {
                add("")
            }
        }
    }

    override fun getRootView(): Int = R.layout.activity_legal_withdraw_history
    override fun initView() {
        ptrLv.setPullLoadEnabled(false)
        ptrLv.setOnRefreshListener(this)
        listView = ptrLv.refreshableView
        listView.adapter = historyAdapter
        historyAdapter.group = list

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

    override fun getContainerTargetView(): View? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPullDownToRefresh(refreshView: PullToRefreshBase<ListView>?) {
    }

    override fun onPullUpToRefresh(refreshView: PullToRefreshBase<ListView>?) {
    }
}
