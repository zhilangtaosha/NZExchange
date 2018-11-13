package com.nze.nzexchange.controller.home.carousel

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.zhuang.zbannerlibrary.ZBannerAdapter

class CarouselAdapter(var fm: FragmentManager, var imageUrls: MutableList<String>) : ZBannerAdapter(fm) {
    override fun getCount(): Int = imageUrls.size

    override fun getItem(position: Int): Fragment {
        return CarouselFragment.newInstance(imageUrls.get(position), position)
    }

}