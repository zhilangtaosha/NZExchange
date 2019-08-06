package com.nze.nzexchange.controller.main

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.IBinder
import android.support.v4.app.FragmentManager
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.View
import android.view.ViewParent
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.tool.NLog
import com.nze.nzexchange.R
import com.nze.nzexchange.config.EventCode
import com.nze.nzexchange.config.KLineParam
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.base.NBaseFragment
import com.nze.nzexchange.controller.bibi.BibiFragment
import com.nze.nzexchange.controller.bibi.SoketService
import com.nze.nzexchange.controller.home.HomeFragment
import com.nze.nzexchange.controller.market.MarketFragment
import com.nze.nzexchange.controller.my.MyFragment
import com.nze.nzexchange.controller.otc.OtcFragment
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.view.*

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
    var mCurrentTab = 0
    var mLastTab = 0
    override fun getRootView(): Int = R.layout.activity_home

    override fun initView() {
        tab_home_home.setOnClickListener(this)
        tab_market_home.setOnClickListener(this)
        tab_bibi_home.setOnClickListener(this)
        tab_otc_home.setOnClickListener(this)
        tab_my_home.setOnClickListener(this)
        viewPager.setOnTouchListener { v, event ->
            false
        }
        viewPager.adapter = fragmentAdapter
        viewPager.offscreenPageLimit = 5
        viewPager.currentItem = 0

    }

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {
        if (eventCenter.eventCode == EventCode.CODE_REFRESH_MAIN_ACT) {
            val i = eventCenter.data as Int
            mLastTab = mCurrentTab
            mCurrentTab = i
            viewPager.currentItem = i
        }
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
        when (v?.id) {
            R.id.tab_home_home -> {
                viewPager.currentItem = 0
            }
            R.id.tab_market_home -> {
                viewPager.currentItem = 1
            }
            R.id.tab_bibi_home -> {
                viewPager.currentItem = 2
            }
            R.id.tab_otc_home -> {
                viewPager.currentItem = 3
            }
            R.id.tab_my_home -> {
                viewPager.currentItem = 4
            }
        }

    }

    var binder: SoketService.SoketBinder? = null
    val connection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            Log.i("zwy", "start onServiceDisconnected")
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.i("zwy", "start onServiceConnected")
            binder = service as SoketService.SoketBinder
            binder?.initSocket("start", KLineParam.getMarketMyself(), {
                NLog.i("start open")
            }, {})
        }
    }

    override fun onResume() {
        super.onResume()
        bindService(Intent(this, SoketService::class.java), connection, Context.BIND_AUTO_CREATE)
    }

    override fun onPause() {
        super.onPause()
        unbindService(connection)
    }

}
