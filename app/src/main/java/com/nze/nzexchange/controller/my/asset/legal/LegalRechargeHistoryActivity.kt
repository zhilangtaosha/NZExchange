package com.nze.nzexchange.controller.my.asset.legal

import android.view.View
import android.widget.ListView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.widget.pulltorefresh.PullToRefreshListView
import com.nze.nzeframework.widget.pulltorefresh.internal.PullToRefreshBase
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.my.asset.legal.presenter.LegalP
import com.nze.nzexchange.widget.CommonTopBar
import kotlinx.android.synthetic.main.activity_legal_withdraw_history.*

/**
 * 法币充值记录
 */
class LegalRechargeHistoryActivity : NBaseActivity(), PullToRefreshBase.OnRefreshListener<ListView> {
    val topBar: CommonTopBar by lazy { ctb_alwh }
    val legalP: LegalP by lazy { LegalP(this) }

    val ptrLv: PullToRefreshListView by lazy { ptrlv_alwh }
    lateinit var listView: ListView
    val historyAdapter by lazy { LegalRechargeHistoryAdapter(this) }

    var userBean = UserBean.loadFromApp()

    override fun getRootView(): Int = R.layout.activity_legal_withdraw_history
    override fun initView() {
        topBar.setTitle("充值记录")
        ptrLv.setPullLoadEnabled(false)
        ptrLv.setOnRefreshListener(this)
        listView = ptrLv.refreshableView
        listView.adapter = historyAdapter

        ptrLv.doPullRefreshing(true, 200)
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
        page = 1
        getHistory()
    }

    override fun onPullUpToRefresh(refreshView: PullToRefreshBase<ListView>?) {
        page++
        getHistory()
    }

    fun getHistory() {
        legalP.getRechargeHistory(userBean!!, page, PAGE_SIZE, {
            if (it.success) {
                historyAdapter.group = it.result
            } else {
                showToast(it.message)
            }
            ptrLv.onPullDownRefreshComplete()
            ptrLv.onPullUpRefreshComplete()
        }, {
            ptrLv.onPullDownRefreshComplete()
            ptrLv.onPullUpRefreshComplete()
        })
    }
}
