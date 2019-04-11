package com.nze.nzexchange.controller.market


import android.os.Bundle
import android.view.View
import android.widget.ListView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.tool.NLog
import com.nze.nzeframework.widget.pulltorefresh.PullToRefreshListView
import com.nze.nzeframework.widget.pulltorefresh.internal.PullToRefreshBase
import com.nze.nzexchange.NzeApp
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.TransactionPairsBean
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.config.EventCode
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.controller.base.NBaseFragment
import kotlinx.android.synthetic.main.fragment_market_content.view.*
import kotlinx.android.synthetic.main.fragment_market_optional.view.*


class MarketOptionalFragment : NBaseFragment(), PullToRefreshBase.OnRefreshListener<ListView> {

    lateinit var ptrLv: PullToRefreshListView
    var mainCurrency: String? = null
    val lvAdapter: MarketLvAdapter by lazy {
        MarketLvAdapter(activity!!)
    }

    var userBean: UserBean? = UserBean.loadFromApp()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mainCurrency = it.getString(IntentConstant.PARAM_CURRENCY)
        }

    }

    companion object {
        @JvmStatic
        fun newInstance(currency: String) =
                MarketOptionalFragment().apply {
                    arguments = Bundle().apply {
                        putString(IntentConstant.PARAM_CURRENCY, currency)
                    }
                }
    }

    override fun getRootView(): Int = R.layout.fragment_market_optional

    override fun initView(rootView: View) {
        ptrLv = rootView.plv_fmo
        ptrLv.setOnRefreshListener(this)
        val listView: ListView = ptrLv.refreshableView
        listView.adapter = lvAdapter
    }

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {
        if (eventCenter.eventCode == EventCode.CODE_LOGIN_SUCCUSS) {
            userBean = UserBean.loadFromApp()
            refreshData()
        }
    }

    override fun isBindEventBusHere(): Boolean = true

    override fun isBindNetworkListener(): Boolean = false

    override fun onNetworkConnected(type: NetUtils.NetType) {
    }

    override fun onNetworkDisConnected() {
    }

    override fun getContainerTargetView(): View? = null

    fun refreshData() {
        ptrLv.doPullRefreshing(true, 200)
    }


    override fun onPullDownToRefresh(refreshView: PullToRefreshBase<ListView>?) {
        getDataFromNet()
    }

    override fun onPullUpToRefresh(refreshView: PullToRefreshBase<ListView>?) {
    }

    override fun getDataFromNet() {
        TransactionPairsBean.getOptionalTransactionPair(userBean?.userId!!)
                .map {
                    it.apply {
                        result.map {
                            it.optional = 1
                        }
                    }
                }
                .compose(netTf())
                .subscribe({
                    if (it.success) {
                        lvAdapter.group = it.result
                        ptrLv.onPullDownRefreshComplete()
                    }
                }, onError)
    }
}
