package com.nze.nzexchange.controller.my.asset.transfer

import android.view.View
import android.widget.ListView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.widget.pulltorefresh.PullToRefreshListView
import com.nze.nzexchange.R
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.common.CommonListPopup
import com.nze.nzexchange.widget.CommonTopBar
import kotlinx.android.synthetic.main.activity_transfer_history.*

class TransferHistoryActivity : NBaseActivity() {
    val topBar: CommonTopBar by lazy { ctb_ath }
    val ptrLv: PullToRefreshListView by lazy { ptrlv_ath }
    val historyAdapter: TransferHistoryAdapter by lazy { TransferHistoryAdapter(this) }
    lateinit var listView: ListView

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
                    }
                    1 -> {
                    }
                    2 -> {
                    }
                }
            }
        }
    }

    override fun getRootView(): Int = R.layout.activity_transfer_history

    override fun initView() {
        topBar.setRightClick {
            filterPopup.showPopupWindow()
        }

        listView = ptrLv.refreshableView
        listView.adapter = historyAdapter
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

}
