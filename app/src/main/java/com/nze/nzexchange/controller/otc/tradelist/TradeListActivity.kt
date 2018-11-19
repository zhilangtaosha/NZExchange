package com.nze.nzexchange.controller.otc.tradelist

import android.net.Uri
import android.support.v4.view.ViewPager
import android.view.View
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.base.NBaseFragment
import com.nze.nzexchange.controller.common.CommonIndicatorAdapter
import com.nze.nzexchange.tools.dp2px
import com.nze.nzexchange.tools.getNColor
import com.nze.nzexchange.widget.indicator.indicator.IndicatorViewPager
import com.nze.nzexchange.widget.indicator.indicator.ScrollIndicatorView
import com.nze.nzexchange.widget.indicator.indicator.slidebar.ColorBar
import com.nze.nzexchange.widget.indicator.indicator.transition.OnTransitionTextListener
import kotlinx.android.synthetic.main.activity_trade_list.*

class TradeListActivity : NBaseActivity(), NBaseFragment.OnFragmentInteractionListener {


    lateinit var indicatorViewPager: IndicatorViewPager
    lateinit var viewPager: ViewPager
    lateinit var scrollIndicatorView: ScrollIndicatorView
    val tabs = listOf<String>("未完成", "已完成", "已取消")
    val pages = listOf<NBaseFragment>(TradeCommonFragment.newInstance(TradeCommonFragment.TYPE_NO_COMPLETE),
            TradeCommonFragment.newInstance(TradeCommonFragment.TYPE_COMPLETED),
            TradeCommonFragment.newInstance(TradeCommonFragment.TYPE_CANCEL))

    override fun getRootView(): Int = R.layout.activity_trade_list

    override fun initView() {
        siv_atl.onTransitionListener = OnTransitionTextListener().setColor(getNColor(R.color.color_FF6D87A8), getNColor(R.color.color_FFA3AAB9))

        val colorBar = ColorBar(this, getNColor(R.color.color_FF6D87A8), 3)
        colorBar.setWidth(dp2px(50F))
        siv_atl.setScrollBar(colorBar)

        vp_atl.offscreenPageLimit = 2
        indicatorViewPager = IndicatorViewPager(siv_atl, vp_atl)
        val indicatorAdapter = CommonIndicatorAdapter(supportFragmentManager!!, this, tabs, pages)
        indicatorViewPager.adapter = indicatorAdapter
    }

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getOverridePendingTransitionMode(): TransitionMode = TransitionMode.DEFAULT

    override fun isBindEventBusHere(): Boolean = false

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
}
