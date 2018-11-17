package com.nze.nzexchange.extend

import android.os.Build
import android.support.v4.content.ContextCompat
import android.text.Html
import android.view.View
import android.widget.TextView

fun View.setBg(drawableId: Int) {
    if (Build.VERSION.SDK_INT > 15) {
        background = ContextCompat.getDrawable(context, drawableId)
    } else {
        setBackgroundDrawable(ContextCompat.getDrawable(context, drawableId))
    }
}

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

