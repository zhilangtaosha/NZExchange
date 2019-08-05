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
import com.nze.nzexchange.database.dao.impl.SoketPairDaoImpl
import com.nze.nzexchange.tools.getNColor
import com.nze.nzexchange.widget.CommonTopBar
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import kotlinx.android.synthetic.main.activity_bibi_all_order.*
import kotlinx.android.synthetic.main.activity_buy_confirm.*

class BibiAllOrderActivity : NBaseActivity(), PullToRefreshBase.OnRefreshListener<ListView> {
    val mSoketPairDao by lazy { SoketPairDaoImpl() }
    val topBar: CommonTopBar by lazy { ctb_abao }
    val currentTv: TextView by lazy { tv_current_abao }
    val currentView: View by lazy { view_current_abao }
    val historyTv: TextView by lazy { tv_history_abao }
    val historyView: View by lazy { view_history_abao }
    var currency: String? = null
    var mainCurrency: String? = null
    var status: Int? = null
    var transactionType: Int = 0
    var mCurrentPair = "*"
    var mHistoryPair = "*"
    var mCurrentSide = 0
    var mHistorySide = 0

    val filterPopup by lazy {
        BibiFilterPopup(this).apply {
            onFilterClick = {
                currency = it.currency
                mainCurrency = it.mainCurrency
                status = it.orderStatus
                transactionType = it.tradeType
//                orderTracking(it.currency, it.mainCurrency, userBean?.userId, it.orderStatus, it.tradeType)

                if (mSelect == SELECT_CURRENT) {
                    if (!currency.isNullOrEmpty() && !mainCurrency.isNullOrEmpty()) {
                        mCurrentPair = "${currency}${mainCurrency}"
                    } else {
                        if (from == FROM_BIBI) {
                            mCurrentPair = pair
                        } else {
                            mCurrentPair = "*"
                        }
                    }
                    mCurrentSide = transactionType
                    queryCurrentOrder()
                } else {
                    if (!currency.isNullOrEmpty() && !mainCurrency.isNullOrEmpty()) {
                        mHistoryPair = "${currency}${mainCurrency}"
                    } else {
                        if (from == FROM_BIBI) {
                            mHistoryPair = pair
                        } else {
                            mHistoryPair = "*"
                        }
                    }
                    mHistorySide = transactionType
                    queryHistoryOrder()
                }
                this.dismiss()
            }
        }
    }
    val ptrLv: PullToRefreshListView by lazy { ptrlv_abao }
    lateinit var listView: ListView
    val orderAdapter: BibiAllOrderAdapter by lazy {
        BibiAllOrderAdapter(this).apply {
            cancelClick = { position, item ->
                binder?.orderCancel("${item.currency}${item.mainCurrency}", item.id)
            }
        }
    }
    val historyAdapter: BibiHistoryOrderAdapter by lazy {
        BibiHistoryOrderAdapter(this)
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
            mCurrentPair = pair
            mHistoryPair = pair
        }

        if (from == FROM_BIBI) {
            topBar.setTitle("全部订单")
            filterPopup.showPair(false)
        } else {
            topBar.setTitle("订单管理")
            filterPopup.showPair(true)
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
            listView.adapter = orderAdapter
            mSelect = SELECT_CURRENT
            select(mSelect)
        }
        historyTv.setOnClickListener {
            listView.adapter = historyAdapter
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
            queryHistoryOrder()
        }
    }

    override fun onResume() {
        super.onResume()
        bindService(Intent(this, SoketService::class.java), connection, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        binder?.removeCallBack("order")
        unbindService(connection)
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


//            binder?.addMarketCallBack("order") {
//                mMarketList.addAll(it)
//                queryCurrentOrder()
//            }
            binder?.addCurrentOrderCallBack("order", { orderList ->
                NLog.i("订单查询")
                stopAllView()

                if (orderList.size > 0) {
                    Flowable.create<MutableList<SoketOrderBean>>({
                        orderList.forEach { orderBean ->
                            run market@{
                                //                                mMarketList.forEach { marketBean ->
//                                    marketBean.list.forEach { rankBean ->
//                                        if (rankBean.getPair() == orderBean.market) {
//                                            orderBean.currency = rankBean.getCurrency()
//                                            orderBean.mainCurrency = rankBean.getMainCurrency()
//                                            return@market
//                                        }
//                                    }
//                                }
                                val rankList = mSoketPairDao.getRankList()
                                rankList.forEach { rankBean ->
                                    if (rankBean.getPair() == orderBean.market) {
                                        orderBean.currency = rankBean.getCurrency()
                                        orderBean.mainCurrency = rankBean.getMainCurrency()
                                        return@market
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
                } else if (mSelect == SELECT_CURRENT && mCurrentPage == 0 && orderList.size <= 0) {
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
//                        val i = currentOrderList.indexOfFirst { item ->
//                            item.id == it.order.id
//                        }
                        var bean: SoketOrderBean? = null
                        var i = -1
                        currentOrderList.forEachIndexed { index, item ->
                            if (item.id == it.order.id) {
                                bean = item
                                i = index
                                return@forEachIndexed
                            }
                        }
                        if (i >= 0) {
                            it.order.currency = bean?.currency
                            it.order.mainCurrency = bean?.mainCurrency
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
                    SoketSubscribeOrderBean.EVENT_CANCEL -> {
                        val i = currentOrderList.indexOfFirst { item ->
                            item.id == it.order.id
                        }
                        if (i >= 0) {
                            currentOrderList.removeAt(i)
                            orderAdapter.removeItem(i)
                        }
                        if (currentOrderList.size <= 0) {
                            queryCurrentOrder()
                        }
                    }
                }
            }, { rs, bean ->
                //取消订单

            })
            binder?.addHistoryOrderCallBack { orderList ->
                stopAllView()
                if (orderList.size > 0) {
                    Flowable.create<MutableList<SoketOrderBean>>({
                        orderList.forEach { orderBean ->
                            run market@{
                                //                                mMarketList.forEach { marketBean ->
//                                    marketBean.list.forEach { rankBean ->
//                                        if (rankBean.getPair() == orderBean.market) {
//                                            orderBean.currency = rankBean.getCurrency()
//                                            orderBean.mainCurrency = rankBean.getMainCurrency()
//                                            return@market
//                                        }
//                                    }
//                                }

                                val rankList = mSoketPairDao.getRankList()
                                rankList.forEach { rankBean ->
                                    if (rankBean.getPair() == orderBean.market) {
                                        orderBean.currency = rankBean.getCurrency()
                                        orderBean.mainCurrency = rankBean.getMainCurrency()
                                        return@market
                                    }
                                }
                            }

                        }
                        it.onNext(orderList)
                    }, BackpressureStrategy.DROP)
                            .compose(netTfWithDialog())
                            .subscribe {
                                if (mHistoryPage == 0) {
                                    historyOrderList.clear()
                                    historyOrderList.addAll(it)
                                    historyAdapter.group = it
                                    listView.adapter = historyAdapter
                                } else {
                                    historyOrderList.addAll(it)
                                    historyAdapter.addItems(it)
                                    ptrLv.onPullUpRefreshComplete()
                                }
                            }

                } else if (mSelect == SELECT_HISTORY && mHistoryPage == 0 && orderList.size <= 0) {
                    showNODataView("当前没有委托单")
                } else {
                    ptrLv.onPullUpRefreshComplete()
                }
            }
//            binder?.queryMarket()
            queryCurrentOrder()
        }
    }

    private fun select(select: Int) {
        stopAllView()
        currentTv.isSelected = select == SELECT_CURRENT
        currentView.visibility = if (select == SELECT_CURRENT) View.VISIBLE else View.GONE

        historyTv.isSelected = select == SELECT_HISTORY
        historyView.visibility = if (select == SELECT_HISTORY) View.VISIBLE else View.GONE
        if (select == SELECT_CURRENT) {
//            if (mMarketList.size > 0) {
//                if (orderAdapter.count > 0) {
//                    listView.adapter = orderAdapter
//                } else {
            mCurrentPage = 0
            mCurrentSide = 0
            if (from == FROM_BIBI) {
                mCurrentPair = pair
            } else {
                mCurrentPair = "*"
            }
            queryCurrentOrder()
//                }
//            } else {
//                binder?.queryMarket()
//            }

        } else {
            mHistoryPage = 0
            mHistorySide = 0
            if (from == FROM_BIBI) {
                mHistoryPair = pair
            } else {
                mHistoryPair = "*"
            }
            queryHistoryOrder()
        }
    }

    fun queryCurrentOrder() {
        binder?.queryCurrentOrder("order", mCurrentPair, mCurrentPage * 20, 20, mCurrentSide)
    }

    fun queryHistoryOrder() {
        binder?.queryHistoryOrder(mHistoryPair, 0, 0, mHistoryPage * 20, 20, mHistorySide)
    }
}
