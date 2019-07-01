package com.nze.nzexchange.controller.market

import android.content.Intent
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
import com.nze.nzexchange.bean.SoketMarketBean
import com.nze.nzexchange.bean.SoketRankBean
import com.nze.nzexchange.bean.TransactionPairsBean
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.config.EventCode
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.controller.base.NBaseFragment
import com.nze.nzexchange.database.dao.impl.PairDaoImpl
import com.nze.nzexchange.config.HttpConfig
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.common.NLoopAction
import com.nze.nzexchange.controller.common.presenter.CommonBibiP
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_market_content.view.*
import java.util.concurrent.TimeUnit

class MarketContentFragment : NBaseFragment(), PullToRefreshBase.OnRefreshListener<ListView> {

    lateinit var ptrLv: PullToRefreshListView

    val lvAdapter: MarketLvAdapter by lazy {
        MarketLvAdapter(activity!!)
    }
    var mainCurrency: String? = null
    var userBean: UserBean? = UserBean.loadFromApp()
    val pairDao = PairDaoImpl()
    val mMarketList: MutableList<SoketRankBean> by lazy { mutableListOf<SoketRankBean>() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mainCurrency = it.getString(IntentConstant.PARAM_CURRENCY)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(currency: String) =
                MarketContentFragment().apply {
                    arguments = Bundle().apply {
                        putString(IntentConstant.PARAM_CURRENCY, currency)
                    }
                }
    }

    override fun getRootView(): Int = R.layout.fragment_market_content

    override fun initView(rootView: View) {
        ptrLv = rootView.ptrlv_fmc
        ptrLv.setOnRefreshListener(this)
        val listView: ListView = ptrLv.refreshableView
        listView.adapter = lvAdapter
        ptrLv.isPullRefreshEnabled = false

        listView.setOnItemClickListener { parent, view, position, id ->
            val rankBean = lvAdapter.getItem(position)!!
            val pairsBean: TransactionPairsBean = TransactionPairsBean()
            pairsBean.setValueFromRankBean(rankBean)

            pairsBean.currency
            startActivity(Intent(activity, KLineActivity::class.java)
                    .putExtra(IntentConstant.PARAM_TRANSACTION_PAIR, pairsBean))
        }
    }

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {
        if (eventCenter.eventCode == EventCode.CODE_LOGIN_SUCCUSS || eventCenter.eventCode == EventCode.CODE_LOGOUT_SUCCESS) {
            userBean = UserBean.loadFromApp()
//            refreshData()
        }

    }

    override fun isBindEventBusHere(): Boolean = true

    override fun isBindNetworkListener(): Boolean = false

    override fun onNetworkConnected(type: NetUtils.NetType) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onNetworkDisConnected() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getContainerTargetView(): View? = null

    fun refreshData() {
//        ptrLv.doPullRefreshing(true, 200)
    }

    fun refreshData(marketList: MutableList<SoketRankBean>) {
        lvAdapter.group = marketList
    }

    override fun onPullDownToRefresh(refreshView: PullToRefreshBase<ListView>?) {
//        getDataFromNet()
    }

    override fun onPullUpToRefresh(refreshView: PullToRefreshBase<ListView>?) {
    }

    var loopAction: NLoopAction? = null
    /**
     * 5秒刷新一下数据
     */
    override fun getDataFromNet() {
        CommonBibiP.getInstance(activity as NBaseActivity)
                .currencyToLegal(mainCurrency!!, 1.0, {
                    if (it.success) {
                        lvAdapter.mainCurrencyLegal = it.result
                    } else {
                        lvAdapter.mainCurrencyLegal = 0.0
                    }
                }, onError)
        if (loopAction == null)
            loopAction = NLoopAction.getInstance((activity as NBaseActivity?)!!)
        loopAction?.loop {
            TransactionPairsBean.getTransactionPairs(mainCurrency!!, NzeApp.instance.userBean?.userId)
                    .map {
                        if (it.success) {
                            pairDao.addList(it.result)
                        }
                        it
                    }
                    .compose(netTf())
                    .subscribe({
                        if (it.success) {
//                            val list = it.result
//                            lvAdapter.group = list
//                            ptrLv.onPullDownRefreshComplete()
                        }
                    }, {
                        NLog.i("getTransactionPairs error....")
                        ptrLv.onPullDownRefreshComplete()
                    })
        }
    }

    override fun onFirstRequest() {
//        getDataFromNet()
    }

}