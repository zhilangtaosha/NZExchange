package com.nze.nzexchange.widget

import android.content.Context
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatButton
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import com.nze.nzexchange.R

class CommonButton(context: Context, attrs: AttributeSet?) : Button(context, attrs) {

    init {
        initView(context, attrs)
    }

    fun initView(context: Context, attrs: AttributeSet?) {
        if (Build.VERSION.SDK_INT > 15) {
            background = ContextCompat.getDrawable(getContext(), R.drawable.selector_common_btn_bg)
        } else {
            setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.selector_common_btn_bg))
        }

        setTextColor(ContextCompat.getColor(context, R.color.selector_common_btn_text))
    }
}