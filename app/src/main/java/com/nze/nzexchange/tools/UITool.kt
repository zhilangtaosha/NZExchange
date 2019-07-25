package com.nze.nzexchange.tools

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.TypedValue
import com.nze.nzexchange.NzeApp


fun dp2px(value: Float, context: Context = NzeApp.instance): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, context.resources.displayMetrics).toInt()
}

fun getNColor(colorId: Int): Int {
    return ContextCompat.getColor(NzeApp.instance, colorId)
}


fun getString( strId: Int): String {
    return NzeApp.instance.getString(strId)
}
