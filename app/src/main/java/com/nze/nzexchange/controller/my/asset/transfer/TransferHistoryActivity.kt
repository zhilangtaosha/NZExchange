package com.nze.nzexchange.controller.my.asset.transfer

import android.view.View
import android.widget.ListView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.widget.pulltorefresh.PullToRefreshListView
import com.nze.nzeframework.widget.pulltorefresh.internal.PullToRefreshBase
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.TransferRecordBean
import com.nze.nzexchange.bean.UserBean
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
    private val itemLimit: MutableList<String> by lazy {
        mutableListOf<String>().apply {
            add("全部")
            add("OTC账户到币币账户")
            add("币币账户到OTC账户")
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
                        from = TransferRecordBean.ACCOUNT_OTC
                        to = TransferRecordBean.ACCOUNT_BIBI
                    }
                    2 -> {
                        from = TransferRecordBean.ACCOUNT_BIBI
                        to = TransferRecordBean.ACCOUNT_OTC
                    }
                }
                transferRecord(userBean?.userId!!, from, to)
                showLoad()
            }
        }
    }

    override fun getRootView(): Int = R.layout.activity_transfer_history

    override fun initView() {
        topBar.setRightClick {
            filterPopup.showPopupWindow()
        }
        ptrLv.setOnRefreshListener(this)
        ptrLv.isPullLoadEnabled = false
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
        transferRecord(userBean?.userId!!, from, null)
    }

    override fun onPullUpToRefresh(refreshView: PullToRefreshBase<ListView>?) {
    }

    fun transferRecord(userId: String, from: String? = null, to: String? = null) {
        TransferRecordBean.transferRecord(userId, from, to)
                .compose(netTf())
                .subscribe({
                    if (it.success) {
                        historyAdapter.group = it.result
                    }
                    ptrLv.onPullDownRefreshComplete()
                    dismissLoad()
                }, {
                    ptrLv.onPullDownRefreshComplete()
                    dismissLoad()
                })
    }
}
