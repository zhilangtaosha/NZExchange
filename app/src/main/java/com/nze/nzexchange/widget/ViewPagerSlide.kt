package com.nze.nzexchange.widget

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/8/6
 */
class ViewPagerSlide(context: Context, attrs: AttributeSet?) : ViewPager(context, attrs) {
    var isSlide = false

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (isSlide) {
            return super.onInterceptTouchEvent(ev)
        } else {
            return false
        }
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        if (isSlide) {
            return super.onTouchEvent(ev)
        } else {
            return false
        }
    }
}