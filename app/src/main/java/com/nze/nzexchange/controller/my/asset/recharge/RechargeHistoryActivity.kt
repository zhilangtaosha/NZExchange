package com.nze.nzexchange.controller.my.asset.recharge

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.tool.NLog
import com.nze.nzeframework.widget.pulltorefresh.PullToRefreshRecyclerView
import com.nze.nzeframework.widget.pulltorefresh.internal.PullToRefreshBase
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.TransactionListBean
import com.nze.nzexchange.bean.UserAssetBean
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.bean2.RechargeHistoryBean
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.config.RrefreshType
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.tools.TimeTool
import com.nze.nzexchange.tools.getNColor
import com.nze.nzexchange.widget.recyclerview.Divider
import kotlinx.android.synthetic.main.activity_recharge_history.*

class RechargeHistoryActivity : NBaseActivity(), PullToRefreshBase.OnRefreshListener<RecyclerView> {


    val ptrrv: PullToRefreshRecyclerView by lazy { ptrrv_arh }
    val rcv: RecyclerView by lazy { ptrrv.refreshableView }
    val layoutManager: LinearLayoutManager by lazy { LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) }
    val rcvAdapter: RechargeHistoryAdapter by lazy {
        RechargeHistoryAdapter(this).apply {
            setDetailClick { position, item ->
                startActivity(Intent(this@RechargeHistoryActivity, RechargeDetailActivity::class.java)
                        .putExtra(IntentConstant.PARAM_DETAIL, item))
            }
//            setDetailClick {
//                skipActivity(RechargeDetailActivity::class.java)
//            }
        }
    }

    val historyList: MutableList<TransactionListBean> by lazy { mutableListOf<TransactionListBean>() }

    var userBean: UserBean? = UserBean.loadFromApp()
    var userAssetBean: UserAssetBean? = null

    override fun getRootView(): Int = R.layout.activity_recharge_history

    override fun initView() {
        intent?.let {
            userAssetBean = it.getParcelableExtra(IntentConstant.PARAM_ASSET)
        }
        ptrrv.setOnRefreshListener(this)
        ptrrv.isPullLoadEnabled = true
        rcv.layoutManager = layoutManager
        rcv.adapter = rcvAdapter

        val divider = Divider(divider = ColorDrawable(getNColor(R.color.color_line)))
        divider.setHeight(2)
        rcv.addItemDecoration(divider)

        ptrrv.doPullRefreshing(true, 200)

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

    override fun onPullDownToRefresh(refreshView: PullToRefreshBase<RecyclerView>?) {
        page = 1
        refreshType = RrefreshType.PULL_DOWN
        getTransactionList(userBean?.userId!!, userAssetBean?.currency!!, page, PAGE_SIZE)
    }

    override fun onPullUpToRefresh(refreshView: PullToRefreshBase<RecyclerView>?) {
        page++
        refreshType = RrefreshType.PULL_UP
        getTransactionList(userBean?.userId!!, userAssetBean?.currency!!, page, PAGE_SIZE)
    }

    fun getTransactionList(userId: String, currency: String, pageNumber: Int, pageSize: Int) {
        TransactionListBean.getTransactionList(userId, currency, pageNumber, pageSize)
                .compose(netTfWithDialog())
                .subscribe({
                    if (it.success) {
                        val list = it.result
                        val tmpList: MutableList<RechargeHistoryBean> = mutableListOf()
                        list.groupBy {
                            val month = TimeTool.format(TimeTool.PATTERN3, it.datetime)
                            month
                        }.forEach {
                            NLog.i(it.toString())
                            val current = TimeTool.format(TimeTool.PATTERN3, TimeTool.date())
                            if (it.key == current) {
                                tmpList.add(RechargeHistoryBean(title = "本月", isTitle = true))
                            } else {
                                tmpList.add(RechargeHistoryBean(title = it.key, isTitle = true))
                            }
                            it.value.forEach {
                                tmpList.add(RechargeHistoryBean(
                                        month = TimeTool.format(TimeTool.PATTERN4, it.datetime),
                                        time = TimeTool.format(TimeTool.PATTERN5, it.datetime),
                                        currency = userAssetBean?.currency!!,
                                        rechargeAmount = it.number,
                                        address = it.address,
                                        datetime = it.datetime,
                                        isTitle = false
                                ))
                            }
                        }

                        when (refreshType) {
                            RrefreshType.INIT -> {
                                historyList.clear()
                                rcvAdapter.setData(tmpList)
                                ptrrv.onPullDownRefreshComplete()
                            }
                            RrefreshType.PULL_DOWN -> {
                                historyList.clear()
                                rcvAdapter.setData(tmpList)
                                ptrrv.onPullDownRefreshComplete()
                            }
                            RrefreshType.PULL_UP -> {
                                rcvAdapter.addAllItem(tmpList)
                                ptrrv.onPullUpRefreshComplete()
                            }
                        }
                        historyList.addAll(it.result)
                        if (historyList.size >= it.totalSize)
                            ptrrv.setHasMoreData(false)
                    }
                }, onError)
    }
}
