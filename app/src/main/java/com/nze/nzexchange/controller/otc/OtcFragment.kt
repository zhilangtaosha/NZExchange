package com.nze.nzexchange.controller.otc


import android.graphics.drawable.ColorDrawable
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.utils.EventCenter
import com.nze.nzexchange.Extend.setTextFromHtml

import com.nze.nzexchange.R
import com.nze.nzexchange.controller.base.NBaseFragment
import com.nze.nzexchange.controller.otc.tradelist.TradeListActivity
import com.nze.nzexchange.tools.dp2px
import com.nze.nzexchange.tools.getNColor
import com.nze.nzexchange.widget.indicator.indicator.IndicatorViewPager
import com.nze.nzexchange.widget.indicator.indicator.ScrollIndicatorView
import com.nze.nzexchange.widget.indicator.indicator.slidebar.ColorBar
import com.nze.nzexchange.widget.indicator.indicator.transition.OnTransitionTextListener
import kotlinx.android.synthetic.main.fragment_otc.view.*


class OtcFragment : NBaseFragment(), View.OnClickListener {
    lateinit var drawerLayout: DrawerLayout
    lateinit var moreTv: TextView
    lateinit var leftLayout: LinearLayout
    lateinit var availableTv: TextView
    lateinit var frozenTv: TextView
    lateinit var availableLayout: RelativeLayout

    lateinit var indicatorViewPager: IndicatorViewPager
    lateinit var viewPager: ViewPager
    lateinit var scrollIndicatorView: ScrollIndicatorView
    val tabs = listOf<String>("购买", "出售", "广告")
    val pages = listOf<NBaseFragment>(OtcContentFragment.newInstance(OtcContentFragment.TYPE_BUY),
            OtcContentFragment.newInstance(OtcContentFragment.TYPE_SALE),
            OtcAdFragment.newInstance())

    var currentItem: Int = 0

    val sideData = mutableListOf<String>("BTC", "USDT", "EOS", "ETH", "LTC", "BCH", "DASH")

    companion object {
        @JvmStatic
        fun newInstance() = OtcFragment()
    }

    override fun getRootView(): Int = R.layout.fragment_otc

    override fun initView(rootView: View) {
        //title
        moreTv = rootView.more_market
        moreTv.setOnClickListener(this)
        rootView.iv_trade_list_market.setOnClickListener(this)
        //侧边栏
        drawerLayout = rootView.layout_drawer_market
        leftLayout = rootView.layout_left_market
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
//        drawerLayout.setDrawerShadow(ColorDrawable(getNColor(R.color.black)), Gravity.RIGHT)
        val sideAdapter:OtcSideAdapter=OtcSideAdapter(activity!!)
        rootView.lv_side_otc.adapter = sideAdapter
        sideAdapter.group = sideData
        //主页
        // rootView.available_home.text = Html.fromHtml("可用<font color=\"#E05760\">0.1983</font>UCC")
        availableLayout = rootView.layout_available_otc
        availableTv = rootView.tv_available_otc
        frozenTv = rootView.tv_frozen_otc
        availableLayout.setOnClickListener(this)

        scrollIndicatorView = rootView.siv_otc
        viewPager = rootView.vp_otc
        scrollIndicatorView.onTransitionListener = OnTransitionTextListener().setColor(getNColor(R.color.color_FF6D87A8), getNColor(R.color.color_FFA3AAB9))

        val colorBar = ColorBar(activity!!, getNColor(R.color.color_FF6D87A8), 3)
        colorBar.setWidth(dp2px(41F))
        scrollIndicatorView.setScrollBar(colorBar)

        viewPager.offscreenPageLimit = 2
        indicatorViewPager = IndicatorViewPager(scrollIndicatorView, viewPager)
        val indicatorAdapter = OtcIndicatorAdapter(fragmentManager!!, activity!!, tabs, pages)
        indicatorViewPager.adapter = indicatorAdapter

        indicatorViewPager.setOnIndicatorPageChangeListener { preItem: Int, currentItem: Int ->
            this.currentItem = currentItem
            changeAva(currentItem)
        }

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
                if (drawerLayout.isDrawerOpen(leftLayout)) {
                    drawerLayout.openDrawer(leftLayout)
                } else {
                    drawerLayout.openDrawer(leftLayout)
                }
            }
            R.id.layout_available_otc -> {
                when (currentItem) {
                    0, 1 -> {

                    }
                    2 -> {
                        skipActivity(PublishActivity::class.java)
                    }
                    else -> {
                    }
                }
            }
            R.id.iv_trade_list_market -> {
                skipActivity(TradeListActivity::class.java)
            }
            else -> {
            }
        }
    }

    fun changeAva(current: Int) {
        when (current) {
            0, 1 -> {
                availableTv.setTextFromHtml("可用<font color=\"#E05760\">0.1983</font>UCC")
                frozenTv.text = "冻结0.000UCC"
            }
            2 -> {
                availableTv.text = "发布广告"
                frozenTv.text = "零手续费"
            }
            else -> {

            }
        }
    }
}
