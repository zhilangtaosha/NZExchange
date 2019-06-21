package com.nze.nzexchange.controller.my.asset.transfer

import android.view.View
import android.widget.ListView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.widget.pulltorefresh.PullToRefreshListView
import com.nze.nzeframework.widget.pulltorefresh.internal.PullToRefreshBase
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.TransferRecordBean
import com.nze.nzexchange.bean.TransferRecordBean.Companion.transferRecord
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.config.RrefreshType
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.common.CommonListPopup
import com.nze.nzexchange.widget.CommonTopBar
import kotlinx.android.synthetic.main.activity_transfer_history.*

class TransferHistoryActivity : NBaseActivity(), PullToRefreshBase.OnRefreshListener<ListView> {


    val topBar: CommonTopBar by lazy { ctb_ath }
    val ptrLv: PullToRefreshListView by lazy { ptrlv_ath }
    val historyAdapter: TransferHistoryAdapter by lazy { TransferHistoryAdapter(this) }
    lateinit var listView: ListView
    var userBean = UserBean.loadFromApp()
    var from: String? = null
    var to: String? = null
    var token: String? = null
    private val itemLimit: MutableList<String> by lazy {
        mutableListOf<String>().apply {
            add("全部")
            add("法币账户到币币账户")
            add("币币账户到法币账户")
            if (token == null) {
                add("OTC账户到币币账户")
                add("币币账户到OTC账户")
            }
        }
    }

    val filterPopup: CommonListPopup by lazy {
        CommonListPopup(this).apply {
            addAllItem(itemLimit)
            setOnItemClick { position, item ->
                when (position) {
                    0 -> {
                        from = null
                        to = null
                    }
                    1 -> {
                        from = TransferRecordBean.ACCOUNT_LEGAL
                        to = TransferRecordBean.ACCOUNT_BIBI
                    }
                    2 -> {
                        from = TransferRecordBean.ACCOUNT_BIBI
                        to = TransferRecordBean.ACCOUNT_LEGAL
                    }
                    3 -> {
                        from = TransferRecordBean.ACCOUNT_OTC
                        to = TransferRecordBean.ACCOUNT_BIBI
                    }
                    4 -> {
                        from = TransferRecordBean.ACCOUNT_BIBI
                        to = TransferRecordBean.ACCOUNT_OTC
                    }

                }
                refreshType = RrefreshType.PULL_DOWN
                page = 1
                transferRecord(userBean?.userId!!, from, to)
            }
        }
    }


    override fun getRootView(): Int = R.layout.activity_transfer_history

    override fun initView() {
        intent?.let {
            token = it.getStringExtra(IntentConstant.PARAM_CURRENCY)
        }
        topBar.setRightClick {
            filterPopup.showPopupWindow()
        }
        ptrLv.setOnRefreshListener(this)
        ptrLv.isPullLoadEnabled = true
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

    override fun getContainerTargetView(): View? = ptrLv

    override fun onPullDownToRefresh(refreshView: PullToRefreshBase<ListView>?) {
        refreshType = RrefreshType.PULL_DOWN
        page = 1
        transferRecord(userBean?.userId!!, from, to)
    }

    override fun onPullUpToRefresh(refreshView: PullToRefreshBase<ListView>?) {
        refreshType = RrefreshType.PULL_UP
        page++
        transferRecord(userBean?.userId!!, from, to)
    }

    fun transferRecord(userId: String, from: String? = null, to: String? = null) {
        TransferRecordBean.transferRecord(userId, page, PAGE_SIZE, from, to, token)
                .compose(netTf())
                .subscribe({
                    stopAllView()
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
                                historyAdapter.addItems(list)
                                ptrLv.onPullUpRefreshComplete()

                            }
                        }
                    }

                }, {
                    ptrLv.onPullDownRefreshComplete()
                    ptrLv.onPullUpRefreshComplete()
                })
    }
}
