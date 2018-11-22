package com.nze.nzexchange.controller.market


import android.support.v4.view.ViewPager
import android.view.View
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter

import com.nze.nzexchange.R
import com.nze.nzexchange.controller.base.NBaseFragment
import com.nze.nzexchange.tools.dp2px
import com.nze.nzexchange.tools.getNColor
import com.nze.nzexchange.widget.indicator.indicator.IndicatorViewPager
import com.nze.nzexchange.widget.indicator.indicator.ScrollIndicatorView
import com.nze.nzexchange.widget.indicator.indicator.slidebar.ColorBar
import com.nze.nzexchange.widget.indicator.indicator.transition.OnTransitionTextListener
import kotlinx.android.synthetic.main.fragment_market.view.*


class MarketFragment : NBaseFragment() {
    lateinit var indicatorViewPager: IndicatorViewPager
    lateinit var viewPager: ViewPager
    lateinit var scrollIndicatorView: ScrollIndicatorView
    val tabs = listOf<String>("自选", "USDT", "BTC", "ETH", "EOS", "NZB")
    val pages = listOf<NBaseFragment>(MarketContentFragment(),
            MarketContentFragment(), MarketContentFragment(),
            MarketContentFragment(), MarketContentFragment(),
            MarketContentFragment())


    companion object {
        @JvmStatic
        fun newInstance() = MarketFragment()
    }

    override fun getRootView(): Int = R.layout.fragment_market

    override fun initView(rootView: View) {
        scrollIndicatorView = rootView.siv_market
        viewPager = rootView.vp_market

        scrollIndicatorView.onTransitionListener = OnTransitionTextListener().setColor(-0x927858, -0x5c5547)

        val colorBar = ColorBar(activity!!, getNColor(R.color.color_FF6D87A8), 3)
        colorBar.setWidth(dp2px(60F))
        scrollIndicatorView.setScrollBar(colorBar)

        viewPager.offscreenPageLimit = 2
        indicatorViewPager = IndicatorViewPager(scrollIndicatorView, viewPager)
        val indicatorAdapter = MarketIndicatorAdapter(fragmentManager!!, activity!!, tabs, pages)
        indicatorViewPager.adapter = indicatorAdapter
    }

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {
    }


    override fun isBindEventBusHere(): Boolean = false

    override fun isBindNetworkListener(): Boolean = false

    override fun onNetworkConnected(type: NetUtils.NetType) {
    }

    override fun onNetworkDisConnected() {
    }

    override fun getContainerTargetView(): View? = null


}
