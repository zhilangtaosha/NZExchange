package com.nze.nzexchange.controller.bibi


import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.View
import android.widget.ListView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.widget.pulltorefresh.internal.PullToRefreshBase
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.TransactionPairsBean
import com.nze.nzexchange.controller.base.NBaseFragment
import com.nze.nzexchange.controller.otc.OtcIndicatorAdapter
import com.nze.nzexchange.tools.dp2px
import com.nze.nzexchange.tools.getNColor
import com.nze.nzexchange.widget.indicator.indicator.IndicatorViewPager
import com.nze.nzexchange.widget.indicator.indicator.ScrollIndicatorView
import com.nze.nzexchange.widget.indicator.indicator.slidebar.ColorBar
import com.nze.nzexchange.widget.indicator.indicator.transition.OnTransitionTextListener
import kotlinx.android.synthetic.main.fragment_bib_side.view.*


/**
 * A simple [Fragment] subclass.
 *
 */
class BibiSideFragment : NBaseFragment(), PullToRefreshBase.OnRefreshListener<ListView> {

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

    companion object {
        @JvmStatic
        fun newInstance() = BibiSideFragment()
    }

    override fun getRootView(): Int = R.layout.fragment_bib_side

    override fun initView(rootView: View) {
        viewPager = rootView.vp_fbs
        scrollIndicatorView = rootView.siv_fbs

        scrollIndicatorView.onTransitionListener = OnTransitionTextListener().setColor(getNColor(R.color.color_FF6D87A8), getNColor(R.color.color_FFA3AAB9))

        val colorBar = ColorBar(activity!!, getNColor(R.color.color_FF6D87A8), 3)
        colorBar.setWidth(dp2px(41F))
        scrollIndicatorView.setScrollBar(colorBar)

        viewPager.offscreenPageLimit = 2
        indicatorViewPager = IndicatorViewPager(scrollIndicatorView, viewPager)
        onFirstRequest()
    }

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {

    }

    override fun isBindEventBusHere(): Boolean = false

    override fun isBindNetworkListener(): Boolean = false

    override fun onNetworkConnected(type: NetUtils.NetType) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onNetworkDisConnected() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getContainerTargetView(): View? = viewPager

    override fun onPullDownToRefresh(refreshView: PullToRefreshBase<ListView>?) {
    }

    override fun onPullUpToRefresh(refreshView: PullToRefreshBase<ListView>?) {
    }

    override fun onFirstRequest() {
        TransactionPairsBean.getAllTransactionPairs()
                .compose(netTf())
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
                        val indicatorAdapter = OtcIndicatorAdapter(childFragmentManager, activity!!, tabs, pages)
                        indicatorViewPager.adapter = indicatorAdapter
                        indicatorViewPager.setOnIndicatorPageChangeListener { preItem, currentItem ->
                            //            pages[currentItem].getDataFromNet()
                        }
                    }
                }, onError)

    }


}
