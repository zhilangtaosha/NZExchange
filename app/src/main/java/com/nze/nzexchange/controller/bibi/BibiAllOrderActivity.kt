package com.nze.nzexchange.controller.bibi

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.drawable.ColorDrawable
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.ListView
import android.widget.TextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.tool.NLog
import com.nze.nzeframework.ui.BaseActivity
import com.nze.nzeframework.widget.pulltorefresh.PullToRefreshListView
import com.nze.nzeframework.widget.pulltorefresh.internal.PullToRefreshBase
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.*
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.config.RrefreshType
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.tools.getNColor
import com.nze.nzexchange.widget.CommonTopBar
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import kotlinx.android.synthetic.main.activity_bibi_all_order.*

class BibiAllOrderActivity : NBaseActivity(), PullToRefreshBase.OnRefreshListener<ListView> {

    val topBar: CommonTopBar by lazy { ctb_abao }
    val currentTv: TextView by lazy { tv_current_abao }
    val currentView: View by lazy { view_current_abao }
    val historyTv: TextView by lazy { tv_history_abao }
    val historyView: View by lazy { view_history_abao }
    var currency: String? = null
    var mainCurrency: String? = null
    var status: Int? = null
    var transactionType: Int? = null
    val filterPopup by lazy {
        BibiFilterPopup(this).apply {
            onFilterClick = {
                currency = it.currency
                mainCurrency = it.mainCurrency
                status = it.orderStatus
                transactionType = it.tradeType
//                orderTracking(it.currency, it.mainCurrency, userBean?.userId, it.orderStatus, it.tradeType)
                this.dismiss()
            }
        }
    }
    val ptrLv: PullToRefreshListView by lazy { ptrlv_abao }
    lateinit var listView: ListView
    val orderAdapter: BibiAllOrderAdapter by lazy {
        BibiAllOrderAdapter(this).apply {
            cancelClick = { position, item ->
            }
        }
    }
    val historyAdapter: BibiAllOrderAdapter by lazy {
        BibiAllOrderAdapter(this)
    }
    val SELECT_CURRENT = 1
    val SELECT_HISTORY = 2
    var mSelect = SELECT_CURRENT
    private val mMarketList: MutableList<SoketMarketBean> by lazy { mutableListOf<SoketMarketBean>() }
    private var mCurrentPage = 0
    private var mHistoryPage = 0

    companion object {
        val FROM = "from"
        val FROM_BIBI = 0
        val FROM_MY = 1
        fun toAllOrderActivity(activity: BaseActivity, from: Int, pair: String) {
            val intent = Intent(activity, BibiAllOrderActivity::class.java)
            intent.putExtra(FROM, from)
            intent.putExtra(IntentConstant.PARAM_TRANSACTION_PAIR, pair)
            activity.startActivity(intent)
        }
    }

    var from = FROM_BIBI
    var userBean: UserBean? = UserBean.loadFromApp()
    val currentOrderList: MutableList<SoketOrderBean> by lazy { mutableListOf<SoketOrderBean>() }
    val historyOrderList: MutableList<SoketOrderBean> by lazy { mutableListOf<SoketOrderBean>() }
    var pair = "*"

    override fun getRootView(): Int = R.layout.activity_bibi_all_order

    override fun initView() {
        topBar.setRightClick {
            if (!filterPopup.isShowing)
                filterPopup.showPopupWindow(topBar)
        }
        intent.let {
            from = it.getIntExtra(FROM, FROM_BIBI)
            pair = it.getStringExtra(IntentConstant.PARAM_TRANSACTION_PAIR)
        }

        if (from == FROM_BIBI) {
            topBar.setTitle("全部委托")
        } else {
            topBar.setTitle("订单管理")
        }
        filterPopup.setOnBeforeShowCallback { popupRootView, anchorView, hasShowAnima ->
            if (anchorView != null) {
                filterPopup.offsetY = anchorView.height
                return@setOnBeforeShowCallback true
            }
            false
        }

        ptrLv.isPullRefreshEnabled = false
        ptrLv.isPullLoadEnabled = true
        ptrLv.setOnRefreshListener(this)
        listView = ptrLv.refreshableView
        listView.divider = ColorDrawable(getNColor(R.color.color_line))
        listView.dividerHeight = 1

        listView.adapter = orderAdapter
        currentTv.setOnClickListener {
            mSelect = SELECT_CURRENT
            select(mSelect)
        }
        historyTv.setOnClickListener {
            mSelect = SELECT_HISTORY
            select(mSelect)
        }
        select(mSelect)
    }

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getOverridePendingTransitionMode(): BaseActivity.TransitionMode = BaseActivity.TransitionMode.DEFAULT

    override fun isBindEventBusHere(): Boolean = false

    override fun isBindNetworkListener(): Boolean = false

    override fun onNetworkConnected(type: NetUtils.NetType) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onNetworkDisConnected() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getContainerTargetView(): View? = listView


    override fun onPullDownToRefresh(refreshView: PullToRefreshBase<ListView>?) {
        refreshType = RrefreshType.PULL_DOWN
    }

    override fun onPullUpToRefresh(refreshView: PullToRefreshBase<ListView>?) {
        refreshType = RrefreshType.PULL_UP
        if (mSelect == SELECT_CURRENT) {
            mCurrentPage++
            queryCurrentOrder()
        } else {
            mHistoryPage++
        }
    }

    override fun onResume() {
        super.onResume()
        bindService(Intent(this, SoketService::class.java), connection, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        unbindService(connection)
        binder?.removeCallBack("order")
        super.onDestroy()
    }

    var binder: SoketService.SoketBinder? = null
    var isBinder = false

    val connection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            Log.i("zwy", "onServiceDisconnected")
            isBinder = false
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.i("zwy", "order onServiceConnected")
            binder = service as SoketService.SoketBinder
            isBinder = true


            binder?.addMarketCallBack("order") {
                mMarketList.addAll(it)
                queryCurrentOrder()
            }
            binder?.addCurrentOrderCallBack("order", { orderList ->
                NLog.i("订单查询")
                stopAllView()

                if (orderList.size > 0) {
                    Flowable.create<MutableList<SoketOrderBean>>({
                        orderList.forEach { orderBean ->
                            run market@{
                                mMarketList.forEach { marketBean ->
                                    marketBean.list.forEach { rankBean ->
                                        if (rankBean.getPair() == orderBean.market) {
                                            orderBean.currency = rankBean.getCurrency()
                                            orderBean.mainCurrency = rankBean.getMainCurrency()
                                            return@market
                                        }
                                    }
                                }
                            }

                        }
                        it.onNext(orderList)
                    }, BackpressureStrategy.DROP)
                            .compose(netTfWithDialog())
                            .subscribe {
                                if (mCurrentPage == 0) {
                                    currentOrderList.clear()
                                    currentOrderList.addAll(it)
                                    orderAdapter.group = it
                                    listView.adapter = orderAdapter
                                    binder?.subscribeOrder(pair)
                                } else {
                                    currentOrderList.addAll(it)
                                    orderAdapter.addItems(it)
                                    ptrLv.onPullUpRefreshComplete()
                                }
                            }

                } else if (mCurrentPage == 0 && orderList.size <= 0) {
                    showNODataView("当前没有委托单")
                    binder?.subscribeOrder(pair)
                } else {
                    ptrLv.onPullUpRefreshComplete()
                }
            }, {
                NLog.i("订单更新")
                when (it.event) {
                    SoketSubscribeOrderBean.EVENT_DEAL -> {
                        stopAllView()
                        currentOrderList.add(0, it.order)
                        orderAdapter.addItem(0, it.order)
                    }
                    SoketSubscribeOrderBean.EVENT_UPDATE -> {
                        val i = currentOrderList.indexOfFirst { item ->
                            item.id == it.order.id
                        }
                        if (i >= 0) {
                            currentOrderList.set(i, it.order)
                            orderAdapter.setItem(i, it.order)
                        }
                    }
                    SoketSubscribeOrderBean.EVENT_FINISH -> {
                        val i = currentOrderList.indexOfFirst { item ->
                            item.id == it.order.id
                        }
                        if (i >= 0) {
                            currentOrderList.removeAt(i)
                            orderAdapter.removeItem(i)
                        }
                    }
                }
            }, {
                //取消订单

            })
            binder?.addHistoryOrderCallBack { orderList ->
                stopAllView()
                if (orderList.size > 0) {
                    Flowable.create<MutableList<SoketOrderBean>>({
                        orderList.forEach { orderBean ->
                            run market@{
                                mMarketList.forEach { marketBean ->
                                    marketBean.list.forEach { rankBean ->
                                        if (rankBean.getPair() == orderBean.market) {
                                            orderBean.currency = rankBean.getCurrency()
                                            orderBean.mainCurrency = rankBean.getMainCurrency()
                                            return@market
                                        }
                                    }
                                }
                            }

                        }
                        it.onNext(orderList)
                    }, BackpressureStrategy.DROP)
                            .compose(netTfWithDialog())
                            .subscribe {
                                historyAdapter.group = it
                                listView.adapter = historyAdapter
                            }

                } else {
                    showNODataView("当前没有委托单")
                }
            }
            binder?.queryMarket()
        }
    }

    private fun select(select: Int) {
        currentTv.isSelected = select == SELECT_CURRENT
        currentView.visibility = if (select == SELECT_CURRENT) View.VISIBLE else View.GONE

        historyTv.isSelected = select == SELECT_HISTORY
        historyView.visibility = if (select == SELECT_HISTORY) View.VISIBLE else View.GONE
        if (select == SELECT_CURRENT) {
            if (mMarketList.size > 0) {
                if (orderAdapter.count > 0) {
                    listView.adapter = orderAdapter
                } else {
                    queryCurrentOrder()
                }
            } else {
                binder?.queryMarket()
            }

        } else {
            queryHistoryOrder()
        }
    }

    fun queryCurrentOrder() {
        binder?.queryCurrentOrder(pair, mCurrentPage * 20, 20)
    }

    fun queryHistoryOrder() {
        historyOrderList.clear()
        binder?.queryHistoryOrder(pair, 0, 0, 0, 20, 0)
    }
}
