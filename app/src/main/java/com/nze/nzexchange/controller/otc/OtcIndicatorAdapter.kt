package com.nze.nzexchange.controller.otc

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.nze.nzexchange.R
import com.nze.nzexchange.controller.base.NBaseFragment
import com.nze.nzexchange.tools.dp2px
import com.nze.nzexchange.widget.indicator.indicator.IndicatorViewPager

open public class OtcIndicatorAdapter(fragmentManager: FragmentManager, val context: Context, var tabs: List<String>, var pages: List<NBaseFragment>) : IndicatorViewPager.IndicatorFragmentPagerAdapter(fragmentManager) {
    val inflate: LayoutInflater = LayoutInflater.from(context)
    override fun getCount(): Int = tabs.size

    override fun getViewForTab(position: Int, convertView: View?, container: ViewGroup?): View {
        var cView = convertView
        if (cView == null) {
            cView = inflate.inflate(R.layout.tab_market,container,false)
        }
        val tv:TextView = cView as TextView
        tv.text = tabs[position%tabs.size]
        val padding = dp2px(15.toFloat(), context)
        tv.setPadding(padding,0,padding,0)
        return cView
    }

    override fun getFragmentForPage(position: Int): Fragment = pages[position]

    override fun getItemPosition(obj: Any?): Int = PagerAdapter.POSITION_NONE

}