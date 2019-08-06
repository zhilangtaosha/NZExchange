package com.nze.nzexchange.controller.main

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.nze.nzexchange.controller.base.NBaseFragment

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/8/6
 */
class MainFragmentAdapter(fm: FragmentManager?, var list: List<NBaseFragment>) : FragmentPagerAdapter(fm) {
    override fun getItem(p0: Int): Fragment = list[p0]

    override fun getCount(): Int = list.size
}