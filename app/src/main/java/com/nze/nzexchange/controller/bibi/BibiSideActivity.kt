package com.nze.nzexchange.controller.bibi

import android.net.Uri
import android.support.v4.view.ViewPager
import android.view.View
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.TransactionPairsBean
import com.nze.nzexchange.config.EventCode
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.base.NBaseFragment
import com.nze.nzexchange.controller.otc.OtcIndicatorAdapter
import com.nze.nzexchange.tools.dp2px
import com.nze.nzexchange.tools.getNColor
import com.nze.nzexchange.widget.indicator.indicator.IndicatorViewPager
import com.nze.nzexchange.widget.indicator.indicator.ScrollIndicatorView
import com.nze.nzexchange.widget.indicator.indicator.slidebar.ColorBar
import com.nze.nzexchange.widget.indicator.indicator.transition.OnTransitionTextListener
import kotlinx.android.synthetic.main.activity_bibi_side.*

class BibiSideActivity : NBaseActivity(), NBaseFragment.OnFragmentInteractionListener {


    private lateinit var viewPager: ViewPager
    private lateinit var scrollIndicatorView: ScrollIndicatorView
    private lateinit var indicatorViewPager: IndicatorViewPager
    private val mainCurrencyList: MutableList<TransactionPairsBean> = mutableListOf()

    private val tabs by lazy {
        mutableListOf<String>().apply {
            add("BCH")
            add("BTC")
            add("ETH")
//            add("USDT")
//            add("EOS")
//            add("HT")
        }
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

        getTransactionPair()
    }

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {
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

    fun getTransactionPair() {
        TransactionPairsBean.getAllTransactionPairs()
                .compose(netTfWithDialog())
                .subscribe({
                    if (it.success) {
                        mainCurrencyList.addAll(it.result)
                        tabs.clear()
                        tabs.add("自选")
                        pages.clear()
                        pages.add(BibiSideContentFragment.newInstance(tabs[0]))
                        mainCurrencyList.forEach {
                            tabs.add(it.mainCurrency)
                            pages.add(BibiSideContentFragment.newInstance(it.mainCurrency))
                        }
                        viewPager.offscreenPageLimit = tabs.size
                        val indicatorAdapter = OtcIndicatorAdapter(supportFragmentManager, this, tabs, pages)
                        indicatorViewPager.adapter = indicatorAdapter
                        indicatorViewPager.setOnIndicatorPageChangeListener { preItem, currentItem ->
                            //            pages[currentItem].getDataFromNet()
                        }
                    }
                }, onError)
    }
}
