package com.nze.nzexchange.controller.my.asset.legal

import android.view.View
import android.widget.ListView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.widget.pulltorefresh.PullToRefreshListView
import com.nze.nzeframework.widget.pulltorefresh.internal.PullToRefreshBase
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.config.RrefreshType
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.my.asset.presenter.LegalP
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
        ptrLv.setPullLoadEnabled(true)
        ptrLv.setOnRefreshListener(this)
        listView = ptrLv.refreshableView
        listView.adapter = historyAdapter

        ptrLv.doPullRefreshing(true, 200)

        listView.setOnItemClickListener { parent, view, position, id ->
            RechargeHistoryDetailActivity.skip(this, historyAdapter.getItem(position)!!)
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

    override fun getContainerTargetView(): View? = ptrLv

    override fun onPullDownToRefresh(refreshView: PullToRefreshBase<ListView>?) {
        refreshType = RrefreshType.PULL_DOWN
        page = 1
        getHistory()
    }

    override fun onPullUpToRefresh(refreshView: PullToRefreshBase<ListView>?) {
        refreshType = RrefreshType.PULL_UP
        page++
        getHistory()
    }

    fun getHistory() {
        legalP.getRechargeHistory(userBean!!, page, PAGE_SIZE, {
            if (it.success) {
                val list = it.result
                when (refreshType) {
                    RrefreshType.INIT -> {
                        if (list != null && list.size > 0) {
                            historyAdapter.group = list
                        } else {
                            showNODataView("没有提现记录")
                        }
                        ptrLv.onPullDownRefreshComplete()
                    }
                    RrefreshType.PULL_DOWN -> {
                        if (list != null && list.size > 0) {
                            historyAdapter.group = list
                        } else {
                            showNODataView("没有提现记录")
                        }
                        ptrLv.onPullDownRefreshComplete()

                    }
                    RrefreshType.PULL_UP -> {
                        if (list != null && list.size > 0) {
                            historyAdapter.addItems(list)
                        }
                        ptrLv.onPullUpRefreshComplete()
                    }
                }
            } else {
                showToast(it.message)
                ptrLv.onPullDownRefreshComplete()
                ptrLv.onPullUpRefreshComplete()
            }
        }, {
            showNODataView("没有提现记录")
            ptrLv.onPullDownRefreshComplete()
            ptrLv.onPullUpRefreshComplete()
        })
    }
}
