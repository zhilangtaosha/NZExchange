package com.nze.nzexchange.controller.bibi

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.ListView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.tool.NLog
import com.nze.nzeframework.ui.BaseActivity
import com.nze.nzeframework.widget.basepopup.BasePopupWindow
import com.nze.nzeframework.widget.pulltorefresh.PullToRefreshListView
import com.nze.nzeframework.widget.pulltorefresh.internal.PullToRefreshBase
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.HandicapBean
import com.nze.nzexchange.bean.OrderPendBean
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.config.RrefreshType
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.common.AuthorityDialog
import com.nze.nzexchange.tools.getNColor
import com.nze.nzexchange.widget.CommonTopBar
import kotlinx.android.synthetic.main.activity_bibi_all_order.*
import kotlinx.android.synthetic.main.fragment_otc_content.view.*

class BibiAllOrderActivity : NBaseActivity(), PullToRefreshBase.OnRefreshListener<ListView> {


    val topBar: CommonTopBar by lazy { ctb_abao }
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
                orderTracking(it.currency, it.mainCurrency, userBean?.userId, it.orderStatus, it.tradeType)
                this.dismiss()
            }
        }
    }
    val ptrLv: PullToRefreshListView by lazy { ptrlv_abao }
    val orderAdapter: BibiCurentOrderAdapter by lazy {
        BibiCurentOrderAdapter(this).apply {
            cancelClick = { position, item ->
                //撤销
                OrderPendBean.cancelOrder(item.id, item.userId, null, item.market, userBean!!.tokenReqVo.tokenUserId, userBean!!.tokenReqVo.tokenUserKey)
                        .compose(netTfWithDialog())
                        .subscribe({
                            if (it.success) {
                                ptrLv.doPullRefreshing(true, 200)
                            } else {
                                if (it.isCauseNotEmpty()) {
                                    AuthorityDialog.getInstance(this@BibiAllOrderActivity)
                                            .show("取消当前委托需要完成以下设置，请检查"
                                                    , it.cause) {

                                            }
                                }
                            }
                        }, onError)
            }
        }
    }

    companion object {
        val FROM = "from"
        val FROM_BIBI = 0
        val FROM_MY = 1
        fun toAllOrderActivity(activity: BaseActivity, from: Int) {
            val intent = Intent(activity, BibiAllOrderActivity::class.java)
            intent.putExtra(FROM, from)
            activity.startActivity(intent)
        }
    }


    var userBean: UserBean? = UserBean.loadFromApp()


    override fun getRootView(): Int = R.layout.activity_bibi_all_order

    override fun initView() {
        topBar.setRightClick {
            if (!filterPopup.isShowing)
                filterPopup.showPopupWindow(topBar)
        }
        val from = intent.getIntExtra(FROM, FROM_BIBI)
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


        ptrLv.isPullLoadEnabled = false
        ptrLv.setOnRefreshListener(this)
        val listView = ptrLv.refreshableView
        listView.divider = ColorDrawable(getNColor(R.color.color_line))
        listView.dividerHeight = 1

        listView.adapter = orderAdapter
        ptrLv.doPullRefreshing(true, 200)
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

    override fun getContainerTargetView(): View? = null


    override fun onPullDownToRefresh(refreshView: PullToRefreshBase<ListView>?) {
        refreshType = RrefreshType.PULL_DOWN
        orderTracking(currency, mainCurrency, userBean?.userId, status, transactionType)
    }

    override fun onPullUpToRefresh(refreshView: PullToRefreshBase<ListView>?) {
        refreshType = RrefreshType.PULL_UP
    }


    fun orderTracking(currency: String?, mainCurrency: String?, userId: String?, status: Int?, transactionType: Int?) {
        OrderPendBean.orderTracking(currency, mainCurrency, userId, status, transactionType)
                .compose(netTf())
                .subscribe({
                    val list = it.result
                    if (list != null && list.size > 0) {
                        orderAdapter.group = list
                    }
                    ptrLv.onPullDownRefreshComplete()
                }, {
                    ptrLv.onPullDownRefreshComplete()
                })
    }

}
