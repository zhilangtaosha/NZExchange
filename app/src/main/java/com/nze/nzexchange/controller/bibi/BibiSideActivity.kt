package com.nze.nzexchange.controller.bibi

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.IBinder
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.View
import android.widget.EditText
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.tool.NLog
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.SoketMarketBean
import com.nze.nzexchange.bean.SoketRankBean
import com.nze.nzexchange.bean.TransactionPairsBean
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.config.EventCode
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.base.NBaseFragment
import com.nze.nzexchange.controller.login.LoginActivity
import com.nze.nzexchange.controller.market.MarketContentFragment
import com.nze.nzexchange.controller.market.MarketIndicatorAdapter
import com.nze.nzexchange.controller.market.MarketOptionalFragment
import com.nze.nzexchange.controller.otc.OtcIndicatorAdapter
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
import kotlinx.android.synthetic.main.activity_bibi_side.*

class BibiSideActivity : NBaseActivity(), NBaseFragment.OnFragmentInteractionListener {

    private val editText: EditText by lazy { cet_search_abs }
    private lateinit var viewPager: ViewPager
    private lateinit var scrollIndicatorView: ScrollIndicatorView
    private lateinit var indicatorViewPager: IndicatorViewPager
    private val mainCurrencyList: MutableList<TransactionPairsBean> = mutableListOf()
    private val mMarketList: MutableList<SoketMarketBean> by lazy { mutableListOf<SoketMarketBean>() }
    private val mSearchList: MutableList<SoketMarketBean> by lazy { mutableListOf<SoketMarketBean>() }
    val pairDao by lazy { PairDaoImpl() }
    private val tabs by lazy {
        mutableListOf<String>()
    }

    private val pages by lazy {
        mutableListOf<NBaseFragment>()
    }


    override fun getRootView(): Int = R.layout.activity_bibi_side

    override fun initView() {
        setWindowStatusBarColor(R.color.color_FF1F2532)
        layout_other_abs.setOnClickListener {
            finish()
        }

        viewPager = vp_abs
        scrollIndicatorView = siv_abs

        scrollIndicatorView.onTransitionListener = OnTransitionTextListener().setColor(getNColor(R.color.color_FF6D87A8), getNColor(R.color.color_FFA3AAB9))

        val colorBar = ColorBar(this, getNColor(R.color.color_FF6D87A8), 3)
        colorBar.setWidth(dp2px(41F))
        scrollIndicatorView.setScrollBar(colorBar)


        indicatorViewPager = IndicatorViewPager(scrollIndicatorView, viewPager)

        RxTextView.textChanges(editText)
                .map { text ->
                    mSearchList.clear()
                    mMarketList.forEach {
                        val l = it.list.filter {
                            it.market.contains(text.toString().toUpperCase()) || it.market.contains(text.toString().toLowerCase())
                        }
                        mSearchList.add(SoketMarketBean(it.money, l))
                    }
                    mSearchList
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    for (i in 1..pages.size - 1) {
                        (pages[i] as BibiSideContentFragment).refreshData(it[i - 1].list.toMutableList())
                    }
                }

    }

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {
        if (eventCenter.eventCode == EventCode.CODE_LOGIN_SUCCUSS || eventCenter.eventCode == EventCode.CODE_SELF_SELECT) {
            getOptionalFromNet()
        }
        if (eventCenter.eventCode == EventCode.CODE_SELECT_TRANSACTIONPAIR) {
            finish()
        }
    }

    override fun getOverridePendingTransitionMode(): TransitionMode = TransitionMode.LEFT

    override fun isBindEventBusHere(): Boolean = true

    override fun isBindNetworkListener(): Boolean = false

    override fun onNetworkConnected(type: NetUtils.NetType) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onNetworkDisConnected() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getContainerTargetView(): View? = null

    override fun onFragmentInteraction(uri: Uri) {

    }

    override fun onResume() {
        super.onResume()
        bindService(Intent(this, SoketService::class.java), connection, Context.BIND_AUTO_CREATE)
    }


    override fun onDestroy() {
        binder?.removeCallBack("market")
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
            Log.i("zwy", "onServiceConnected")
            binder = service as SoketService.SoketBinder
            isBinder = true
            addCallBack()
            binder?.queryMarket()
        }
    }

    fun addCallBack() {
        binder?.addMarketCallBack("market") {
            NLog.i("market resut")
            mMarketList.addAll(it)
            if (UserBean.isLogin())
                getOptionalFromNet()
            tabs.clear()
            tabs.add("自选")
            pages.clear()
            pages.add(BibiSideOptionalFragment.newInstance(tabs[0]))
            mMarketList.forEach {
                tabs.add(it.money)
                pages.add(BibiSideContentFragment.newInstance(it.money))
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
            val indicatorAdapter = MarketIndicatorAdapter(supportFragmentManager, this@BibiSideActivity, tabs, pages)
            indicatorViewPager.adapter = indicatorAdapter
            indicatorViewPager.setOnIndicatorPageChangeListener { preItem, currentItem ->
                if (currentItem == 0 && !UserBean.isLogin()) {
                    skipActivity(LoginActivity::class.java)
                    indicatorViewPager.setCurrentItem(1, true)
                }
            }
            indicatorViewPager.setCurrentItem(1, true)
            if (pages.size > 2) {
                for (i in 1..pages.size - 1) {
                    (pages[i] as BibiSideContentFragment).refreshData(it[i - 1].list.toMutableList())
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
                        (pages[0] as BibiSideOptionalFragment).refreshData(optionalList)
                        if (pages.size > 2) {
                            for (i in 1..pages.size - 1) {
                                (pages[i] as BibiSideContentFragment).refreshData(mMarketList[i - 1].list.toMutableList())
                            }
                        }
                    }
                }, onError)
    }
}
