package com.nze.nzexchange.extend

import android.graphics.drawable.Drawable
import android.os.Build
import android.support.v4.content.ContextCompat
import android.text.Html
import android.widget.TextView

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/4/19
 */

fun TextView.setTxtColor(colorId: Int) {
    setTextColor(ContextCompat.getColor(context, colorId))
}

fun TextView.setTextFromHtml(source: String) {
    if (Build.VERSION.SDK_INT < 24) {
        text = Html.fromHtml(source)
    } else {
        text = Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY)
    }
}

fun TextView.setDrawables(leftId: Int?, topId: Int?, rightId: Int?, bottomId: Int?) {
    var leftDrawable: Drawable? = null
    var topDrawable: Drawable? = null
    var rightDrawable: Drawable? = null
    var bottomDrawable: Drawable? = null
    if (leftId != null) {
        leftDrawable = ContextCompat.getDrawable(context, leftId)
        leftDrawable?.setBounds(0, 0, leftDrawable.intrinsicWidth, leftDrawable.intrinsicHeight)
    }
    if (topId != null) {
        topDrawable = ContextCompat.getDrawable(context, topId)
        topDrawable?.setBounds(0, 0, topDrawable.intrinsicWidth, topDrawable.intrinsicHeight)
    }
    if (rightId != null) {
        rightDrawable = ContextCompat.getDrawable(context, rightId)
        rightDrawable?.setBounds(0, 0, rightDrawable.intrinsicWidth, rightDrawable.intrinsicHeight)
    }
    if (bottomId != null) {
        bottomDrawable = ContextCompat.getDrawable(context, bottomId)
        bottomDrawable?.setBounds(0, 0, bottomDrawable.intrinsicWidth, bottomDrawable.intrinsicHeight)
    }
    setCompoundDrawables(leftDrawable, topDrawable, rightDrawable, bottomDrawable)
}