package com.nze.nzexchange.controller.main

import android.net.Uri
import android.support.v4.app.FragmentManager
import android.support.v4.view.ViewPager
import android.view.View
import android.view.ViewParent
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.base.NBaseFragment
import com.nze.nzexchange.controller.bibi.BibiFragment
import com.nze.nzexchange.controller.home.HomeFragment
import com.nze.nzexchange.controller.market.MarketFragment
import com.nze.nzexchange.controller.my.MyFragment
import com.nze.nzexchange.controller.otc.OtcFragment
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_main.*

class HomeActivity : NBaseActivity(), NBaseFragment.OnFragmentInteractionListener, View.OnClickListener {


    val viewPager: ViewPager by lazy { vp_home }
    val mFragmentManager: FragmentManager by lazy { supportFragmentManager }
    val list: MutableList<NBaseFragment> by lazy {
        mutableListOf<NBaseFragment>(
                HomeFragment.newInstance(),
                MarketFragment.newInstance(),
                BibiFragment.newInstance(),
                OtcFragment.newInstance(),
                MyFragment.newInstance()
        )
    }
    val fragmentAdapter: MainFragmentAdapter by lazy { MainFragmentAdapter(mFragmentManager, list) }

    override fun getRootView(): Int = R.layout.activity_home

    override fun initView() {
        tab_home_home.setOnClickListener(this)
        tab_market_home.setOnClickListener(this)
        tab_bibi_home.setOnClickListener(this)
        tab_otc_home.setOnClickListener(this)
        tab_my_home.setOnClickListener(this)
        viewPager.adapter = fragmentAdapter
        viewPager.currentItem = 0
    }

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {
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

    override fun onClick(v: View?) {
    }
}
