package com.nze.nzexchange.controller.main

import android.net.Uri
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.view.View
import android.widget.Toast
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.utils.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.base.NBaseFragment
import com.nze.nzexchange.controller.home.HomeFragment
import com.nze.nzexchange.controller.market.MarketFragment
import com.nze.nzexchange.controller.my.MyFragment
import com.nze.nzexchange.controller.otc.OtcFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : NBaseActivity(), View.OnClickListener,NBaseFragment.OnFragmentInteractionListener {

    val mFragmentManager: FragmentManager by lazy { supportFragmentManager }
    var mCurrentTab = 0
    var mLastTab = -1
     var mLastFragment: NBaseFragment?=null


    override fun isBindNetworkListener(): Boolean = false

    override fun onNetworkConnected(type: NetUtils.NetType) {
    }

    override fun onNetworkDisConnected() {
    }

    override fun getContainerTargetView(): View? = null

    override fun isBindEventBusHere(): Boolean = true

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {
        if (eventCenter.data is String)
            Toast.makeText(this, eventCenter.data as String, Toast.LENGTH_SHORT).show()
    }

    override fun getOverridePendingTransitionMode(): TransitionMode = TransitionMode.RIGHT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun getRootView(): Int = R.layout.activity_main

    override fun initView() {
        tab_home_main.setOnClickListener(this)
        tab_market_main.setOnClickListener(this)
        tab_bibi_main.setOnClickListener(this)
        tab_otc_main.setOnClickListener(this)
        tab_my_main.setOnClickListener(this)
        selectTab(0)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tab_home_main -> mCurrentTab = 0
            R.id.tab_market_main -> mCurrentTab = 1
            R.id.tab_bibi_main -> mCurrentTab = 2
            R.id.tab_otc_main -> mCurrentTab = 3
            R.id.tab_my_main -> mCurrentTab = 4
            else -> {
            }
        }
        if (mLastTab != mCurrentTab) {
            selectTab(mCurrentTab)
        }
    }

    private fun selectTab(position: Int) {
        mLastTab = position
        mCurrentTab = mLastTab
        val childCount = layout_tab_main.getChildCount()
        for (i in 0 until childCount) {
            if (position != i) {
                layout_tab_main.getChildAt(i).setSelected(false)
            } else {
                layout_tab_main.getChildAt(i).setSelected(true)
            }
        }
        switchFragment(position)
    }

    fun switchFragment(id: Int) {
        val tag = id.toString()

        var transaction = mFragmentManager.beginTransaction()
        mLastFragment?.let { transaction.hide(mLastFragment!!) }
        var fragment: NBaseFragment?  = mFragmentManager.findFragmentByTag(tag) as NBaseFragment?
        if (fragment == null) {
            when (id) {
                0 -> fragment = HomeFragment.newInstance()
                1 -> fragment = MarketFragment.newInstance()
                2 -> fragment = BibiFragment.newInstance()
                3 -> fragment = OtcFragment.newInstance()
                4 -> fragment = MyFragment.newInstance()
                else -> {
                    fragment = HomeFragment.newInstance()
                }
            }
             transaction.add(R.id.content_main, fragment, tag)
        } else {
            transaction.show(fragment)
        }
        mLastFragment = fragment as NBaseFragment
        transaction.commitAllowingStateLoss()
    }


    override fun onFragmentInteraction(uri: Uri) {
    }
}
