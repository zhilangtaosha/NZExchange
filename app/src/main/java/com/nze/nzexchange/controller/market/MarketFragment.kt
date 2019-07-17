package com.nze.nzexchange.controller.market


import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.tool.NLog
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.SoketMarketBean
import com.nze.nzexchange.bean.SoketRankBean
import com.nze.nzexchange.bean.TransactionPairsBean
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.config.EventCode
import com.nze.nzexchange.controller.base.NBaseFragment
import com.nze.nzexchange.controller.bibi.BibiSideContentFragment
import com.nze.nzexchange.controller.bibi.BibiSideOptionalFragment
import com.nze.nzexchange.controller.bibi.SoketService
import com.nze.nzexchange.controller.login.LoginActivity
import com.nze.nzexchange.database.dao.impl.PairDaoImpl
import com.nze.nzexchange.tools.dp2px
import com.nze.nzexchange.tools.getNColor
import com.nze.nzexchange.widget.indicator.indicator.IndicatorViewPager
import com.nze.nzexchange.widget.indicator.indicator.ScrollIndicatorView
import com.nze.nzexchange.widget.indicator.indicator.slidebar.ColorBar
import com.nze.nzexchange.widget.indicator.indicator.transition.OnTransitionTextListener
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_market.view.*


class MarketFragment : NBaseFragment(), View.OnClickListener {


    lateinit var searchIv: ImageView
    lateinit var editIv: ImageView

    lateinit var indicatorViewPager: IndicatorViewPager
    lateinit var viewPager: ViewPager
    lateinit var scrollIndicatorView: ScrollIndicatorView
    val tabs by lazy { mutableListOf<String>() }
    val pages by lazy {
        mutableListOf<NBaseFragment>()
    }

    private val mainCurrencyList: MutableList<TransactionPairsBean> = mutableListOf()
    private val mMarketList: MutableList<SoketMarketBean> by lazy { mutableListOf<SoketMarketBean>() }
    val pairDao by lazy { PairDaoImpl() }

    companion object {
        @JvmStatic
        fun newInstance() = MarketFragment()
    }

    override fun getRootView(): Int = R.layout.fragment_market

    override fun initView(rootView: View) {
        searchIv = rootView.iv_search_market
        editIv = rootView.iv_edit_market
        searchIv.setOnClickListener(this)
        editIv.setOnClickListener(this)
        scrollIndicatorView = rootView.siv_market
        viewPager = rootView.vp_market

        scrollIndicatorView.onTransitionListener = OnTransitionTextListener().setColor(getNColor(R.color.color_main), getNColor(R.color.color_head))

        val colorBar = ColorBar(activity!!, getNColor(R.color.color_main), 3)
        colorBar.setWidth(dp2px(60F))
        scrollIndicatorView.setScrollBar(colorBar)

        viewPager.offscreenPageLimit = 2
        indicatorViewPager = IndicatorViewPager(scrollIndicatorView, viewPager)


//        getTransactionPair()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_search_market -> {
                skipActivity(PairSearchActivity::class.java)
            }
            R.id.iv_edit_market -> {
                skipActivity(OptionalEditActivity::class.java)
            }
        }
    }


    override fun <T> onEventComming(eventCenter: EventCenter<T>) {
        if (eventCenter.eventCode == EventCode.CODE_LOGIN_SUCCUSS || eventCenter.eventCode == EventCode.CODE_SELF_SELECT) {
            getOptionalFromNet()
        }
        if (eventCenter.eventCode == EventCode.CODE_LOGOUT_SUCCESS) {
            indicatorViewPager.setCurrentItem(1, false)
        }
    }


    override fun isBindEventBusHere(): Boolean = true

    override fun isBindNetworkListener(): Boolean = false

    override fun onNetworkConnected(type: NetUtils.NetType) {
    }

    override fun onNetworkDisConnected() {
    }

    override fun getContainerTargetView(): View? = null

    fun getTransactionPair() {
        TransactionPairsBean.getAllTransactionPairs()
                .compose(netTfWithDialog())
                .subscribe({
                    if (it.success) {
                        mainCurrencyList.addAll(it.result)
                        tabs.clear()
                        tabs.add("自选")
                        pages.clear()
                        pages.add(MarketOptionalFragment.newInstance(tabs[0]))
                        mainCurrencyList.forEach {
                            tabs.add(it.mainCurrency)
                            pages.add(MarketContentFragment.newInstance(it.mainCurrency))
                        }
                        viewPager.offscreenPageLimit = tabs.size
                        val indicatorAdapter = MarketIndicatorAdapter(fragmentManager!!, activity!!, tabs, pages)
                        indicatorViewPager.adapter = indicatorAdapter
                        indicatorViewPager.setOnIndicatorPageChangeListener { preItem, currentItem ->
                            if (currentItem == 0 && !UserBean.isLogin()) {
                                skipActivity(LoginActivity::class.java)
                                indicatorViewPager.setCurrentItem(1, true)
                            } else if (currentItem == 0 && UserBean.isLogin()) {
                                (pages[currentItem] as MarketOptionalFragment).refreshData()
                                searchIv.visibility = View.GONE
                                editIv.visibility = View.VISIBLE
                            } else if (currentItem != 0) {
                                searchIv.visibility = View.VISIBLE
                                editIv.visibility = View.GONE
                            }

                        }
                        indicatorViewPager.setCurrentItem(1, true)
                        if (pages.size > 2) {
                            for (i in 2..pages.size - 1) {
                                (pages[i] as MarketContentFragment).refreshData()
                            }
                        }
                    }
                }, onError)
    }

    override fun onResume() {
        super.onResume()
        activity!!.bindService(Intent(activity, SoketService::class.java), connection, Context.BIND_AUTO_CREATE)
    }


    override fun onDestroy() {
        activity!!.unbindService(connection)
        super.onDestroy()
    }

    override fun onInvisibleRequest() {
        super.onInvisibleRequest()
        binder?.removeCallBack("market")
    }

    override fun onVisibleRequest() {
        super.onVisibleRequest()
        if (isBinder)
            bindCallBack()
    }

    var binder: SoketService.SoketBinder? = null
    var isBinder = false

    val connection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            Log.i("zwy", "onServiceDisconnected")
            isBinder = false
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.i("zwy", "market onServiceConnected")
            binder = service as SoketService.SoketBinder
            isBinder = true
            bindCallBack()
            binder?.queryMarket()
        }
    }

    fun bindCallBack() {
        binder?.addMarketCallBack("market") {
            NLog.i("MarketFragment market resut")
            mMarketList.addAll(it)
            if (UserBean.isLogin())
                getOptionalFromNet()
            tabs.clear()
            tabs.add("自选")
            pages.clear()
            pages.add(MarketOptionalFragment.newInstance(tabs[0]))
            mMarketList.forEach {
                tabs.add(it.money)
                pages.add(MarketContentFragment.newInstance(it.money))
            }
            Observable.create<Int> {
                mMarketList.forEach {
                    val pairList = TransactionPairsBean.getListFromRankList(it.list.toMutableList())
                    pairDao.addList(pairList)
                }
                it.onNext(1)
                it.onComplete()
            }.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {

                    }
            viewPager.offscreenPageLimit = tabs.size
            val indicatorAdapter = MarketIndicatorAdapter(fragmentManager!!, activity!!, tabs, pages)
            indicatorViewPager.adapter = indicatorAdapter
            indicatorViewPager.setOnIndicatorPageChangeListener { preItem, currentItem ->
                if (currentItem == 0 && !UserBean.isLogin()) {
                    skipActivity(LoginActivity::class.java)
                    indicatorViewPager.setCurrentItem(1, true)
                } else if (currentItem == 0 && UserBean.isLogin()) {
                    (pages[currentItem] as MarketOptionalFragment).refreshData()
                    searchIv.visibility = View.GONE
                    editIv.visibility = View.VISIBLE
                } else if (currentItem != 0) {
                    searchIv.visibility = View.VISIBLE
                    editIv.visibility = View.GONE
                }

            }
            indicatorViewPager.setCurrentItem(1, true)
            if (pages.size > 2) {
                for (i in 1..pages.size - 1) {
                    (pages[i] as MarketContentFragment).refreshData(it[i - 1].list.toMutableList())
                }
            }
        }
    }

    val optionalList: MutableList<SoketRankBean> by lazy { mutableListOf<SoketRankBean>() }
    fun getOptionalFromNet() {
        TransactionPairsBean.getOptionalTransactionPair(UserBean.loadFromApp()?.userId!!)
                .map {
                    it.apply {
                        if (this.success) {
                            optionalList.clear()
                            mMarketList.forEach { mb ->
                                mb.list.forEach {
                                    it.optional = 0
                                    result.forEach { pb ->
                                        if (it.market == pb.transactionPair) {
                                            it.optional = 1
                                            optionalList.add(it)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                .compose(netTfWithDialog())
                .subscribe({
                    if (it.success) {
                        (pages[0] as MarketOptionalFragment).refreshData(optionalList)
                    }
                }, onError)
    }

}
