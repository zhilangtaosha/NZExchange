package com.nze.nzexchange.controller.my.asset.withdraw

import android.content.Intent
import android.view.View
import android.widget.ListView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.widget.pulltorefresh.PullToRefreshListView
import com.nze.nzeframework.widget.pulltorefresh.internal.PullToRefreshBase
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.TickRecordBean
import com.nze.nzexchange.bean.TransactionListBean
import com.nze.nzexchange.bean.UserAssetBean
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.config.RrefreshType
import com.nze.nzexchange.controller.base.NBaseActivity
import kotlinx.android.synthetic.main.activity_withdraw_history.*

class WithdrawHistoryActivity : NBaseActivity(), PullToRefreshBase.OnRefreshListener<ListView> {


    val ptrLv: PullToRefreshListView by lazy { ptrlv_awh }
    val listView: ListView by lazy {
        ptrLv.refreshableView.apply {
            setOnItemClickListener { parent, view, position, id ->
                //                skipActivity(WithdrawDetailActivity::class.java)
                WithdrawDetailActivity.skip(this@WithdrawHistoryActivity, historyAdapter.getItem(position)!!, userAssetBean?.currency!!, WithdrawDetailActivity.TYPE_WITHDRAW)
            }
        }
    }
    val historyAdapter: WithdrawHistoryAdapter by lazy { WithdrawHistoryAdapter(this) }
    var userBean: UserBean? = UserBean.loadFromApp()
    var userAssetBean: UserAssetBean? = null

    override fun getRootView(): Int = R.layout.activity_withdraw_history

    override fun initView() {
        intent?.let {
            userAssetBean = it.getParcelableExtra(IntentConstant.PARAM_ASSET)
        }
        ptrLv.isPullLoadEnabled = true
        ptrLv.setOnRefreshListener(this)
        listView.adapter = historyAdapter

        ptrLv.doPullRefreshing(true, 200)
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

    override fun getContainerTargetView(): View? = ptrLv

    override fun onPullDownToRefresh(refreshView: PullToRefreshBase<ListView>?) {
        refreshType = RrefreshType.PULL_DOWN
        page = 1
        tickRecord()
    }

    override fun onPullUpToRefresh(refreshView: PullToRefreshBase<ListView>?) {
        refreshType = RrefreshType.PULL_UP
        page++
        tickRecord()
    }

    fun tickRecord() {
        TransactionListBean.tickRecord(userBean?.userId!!, userAssetBean?.currency!!, page, PAGE_SIZE)
                .compose(netTf())
                .subscribe({
                    if (it.success) {
                        stopAllView()
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
                                historyAdapter.addItems(list)
                                ptrLv.onPullUpRefreshComplete()

                            }
                        }
                    }
                }, onError)
    }

}
