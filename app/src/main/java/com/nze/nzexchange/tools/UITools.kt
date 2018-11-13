package com.nze.nzexchange.tools

import android.content.Context
import android.util.TypedValue
import com.nze.nzexchange.NzeApp


fun dp2px(value: Float,context:Context= NzeApp.instance!!): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, context.getResources().getDisplayMetrics()).toInt()
}