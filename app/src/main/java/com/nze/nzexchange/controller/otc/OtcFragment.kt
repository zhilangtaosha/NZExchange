package com.nze.nzexchange.controller.otc


import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.text.Html
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.utils.EventCenter

import com.nze.nzexchange.R
import com.nze.nzexchange.controller.base.NBaseFragment
import com.nze.nzexchange.controller.market.MarketContentFragment
import com.nze.nzexchange.controller.market.MarketIndicatorAdapter
import com.nze.nzexchange.tools.dp2px
import com.nze.nzexchange.widget.indicator.indicator.IndicatorViewPager
import com.nze.nzexchange.widget.indicator.indicator.ScrollIndicatorView
import com.nze.nzexchange.widget.indicator.indicator.slidebar.ColorBar
import com.nze.nzexchange.widget.indicator.indicator.transition.OnTransitionTextListener
import kotlinx.android.synthetic.main.fragment_market.view.*
import kotlinx.android.synthetic.main.fragment_otc.*
import kotlinx.android.synthetic.main.fragment_otc.view.*


class OtcFragment : NBaseFragment(), View.OnClickListener {
    lateinit var drawer: DrawerLayout
    lateinit var moreTv: TextView
    lateinit var leftLayout: LinearLayout

    lateinit var indicatorViewPager: IndicatorViewPager
    lateinit var viewPager: ViewPager
    lateinit var scrollIndicatorView: ScrollIndicatorView
    val tabs = listOf<String>("购买", "出售")
    val pages = listOf<NBaseFragment>(OtcContentFragment.newInstance(OtcContentFragment.TYPE_BUY), OtcContentFragment.newInstance(OtcContentFragment.TYPE_SALE))

    companion object {
        @JvmStatic
        fun newInstance() = OtcFragment()
    }

    override fun getRootView(): Int = R.layout.fragment_otc

    override fun initView(rootView: View) {
        //title
        moreTv = rootView.more_market
        moreTv.setOnClickListener(this)
        //侧边栏
        drawer = rootView.layout_drawer_market
        leftLayout = rootView.layout_left_market
        rootView.layout_drawer_market.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        //主页
        rootView.available_home.text = Html.fromHtml("可用<font color=\"#E05760\">0.1983</font>UCC")

        scrollIndicatorView = rootView.siv_otc
        viewPager = rootView.vp_otc
        scrollIndicatorView.onTransitionListener = OnTransitionTextListener().setColor(-0x927858, -0x5c5547)

        val colorBar = ColorBar(activity!!, -0x927858, 3)
        colorBar.setWidth(dp2px(41F))
        scrollIndicatorView.setScrollBar(colorBar)

        viewPager.offscreenPageLimit = 2
        indicatorViewPager = IndicatorViewPager(scrollIndicatorView, viewPager)
        val indicatorAdapter = OtcIndicatorAdapter(fragmentManager!!, activity!!, tabs, pages)
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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.more_market -> {
                if (drawer.isDrawerOpen(leftLayout)) {
                    drawer.openDrawer(leftLayout)
                } else {
                    drawer.openDrawer(leftLayout)
                }
            }
            else -> {
            }
        }
    }

}
