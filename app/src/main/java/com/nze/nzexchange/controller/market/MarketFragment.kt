package com.nze.nzexchange.controller.market


import android.support.v4.view.ViewPager
import android.view.View
import android.widget.ImageView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.TransactionPairsBean
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.controller.base.NBaseFragment
import com.nze.nzexchange.controller.login.LoginActivity
import com.nze.nzexchange.tools.dp2px
import com.nze.nzexchange.tools.getNColor
import com.nze.nzexchange.widget.indicator.indicator.IndicatorViewPager
import com.nze.nzexchange.widget.indicator.indicator.ScrollIndicatorView
import com.nze.nzexchange.widget.indicator.indicator.slidebar.ColorBar
import com.nze.nzexchange.widget.indicator.indicator.transition.OnTransitionTextListener
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


        getTransactionPair()
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
    }


    override fun isBindEventBusHere(): Boolean = false

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
}
