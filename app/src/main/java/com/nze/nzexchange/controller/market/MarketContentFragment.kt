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
import com.nze.nzexchange.bean.TransactionPairBean
import com.nze.nzexchange.bean.TransactionPairsBean
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.config.EventCode
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.controller.base.NBaseFragment
import com.nze.nzexchange.controller.bibi.BibiSideContentFragment
import com.nze.nzexchange.controller.login.selectcountry.SideBar.b
import com.nze.nzexchange.database.dao.impl.PairDaoImpl
import com.nze.nzexchange.http.HttpConfig
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

        listView.setOnItemClickListener { parent, view, position, id ->
            startActivity(Intent(activity, KLineActivity::class.java)
                    .putExtra(IntentConstant.PARAM_TRANSACTION_PAIR, lvAdapter.getItem(position)))
        }
    }

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {
        if (eventCenter.eventCode == EventCode.CODE_LOGIN_SUCCUSS || eventCenter.eventCode == EventCode.CODE_LOGOUT_SUCCESS) {
            userBean = UserBean.loadFromApp()
            refreshData()
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
        ptrLv.doPullRefreshing(true, 200)
    }


    override fun onPullDownToRefresh(refreshView: PullToRefreshBase<ListView>?) {
        getDataFromNet()
    }

    override fun onPullUpToRefresh(refreshView: PullToRefreshBase<ListView>?) {
    }

    var disposable: Disposable? = null
    /**
     * 5秒刷新一下数据
     */
    override fun getDataFromNet() {
        if (disposable != null && !disposable?.isDisposed!!)
            disposable!!.dispose()
        var b = 1
        if (HttpConfig.isLoop&&false) {
            b = HttpConfig.LOOP_NUM
        }
        disposable = Flowable.interval(0,HttpConfig.LOOP_INTERVAL_TIME, TimeUnit.SECONDS)
                .filter { b > 0 }
                .compose(netTf())
                .subscribe {
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
                                    val list = it.result
                                    lvAdapter.group = list
                                    ptrLv.onPullDownRefreshComplete()
                                }
                                b--
                            }, {
                                NLog.i("getTransactionPairs error....")
                                ptrLv.onPullDownRefreshComplete()
                            })
                }

    }

    override fun onFirstRequest() {
        getDataFromNet()
    }
    
}