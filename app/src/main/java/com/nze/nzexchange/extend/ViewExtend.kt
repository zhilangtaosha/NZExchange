package com.nze.nzexchange.extend

import android.graphics.drawable.Drawable
import android.os.Build
import android.support.v4.content.ContextCompat
import android.text.Html
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.jakewharton.rxbinding2.view.RxView
import java.util.concurrent.TimeUnit

fun View.setBg(drawableId: Int) {
    if (Build.VERSION.SDK_INT > 15) {
        background = ContextCompat.getDrawable(context, drawableId)
    } else {
        setBackgroundDrawable(ContextCompat.getDrawable(context, drawableId))
    }
}


fun Button.setBgByDrawable(drawable: Drawable) {
    if (Build.VERSION.SDK_INT > 16) {
        background = drawable
    } else {
        setBackgroundDrawable(drawable)
    }
}


fun Button.setShakeClickListener(click: View.OnClickListener) {
    RxView.clicks(this)
            .throttleFirst(2, TimeUnit.SECONDS)
            .subscribe {
                click.onClick(this)
            }
}




