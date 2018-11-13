package com.nze.nzexchange.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Adapter
import android.widget.LinearLayout

class LinearLayoutAsListView(context: Context?, attrs: AttributeSet?) : LinearLayout(context, attrs) {
    var adapter: Adapter? = null
        set(value) {
            field = value
            bind()
        }

    fun bind() {
        val count = adapter?.count ?: 0
        this.removeAllViews()
        for (i in 0 until count) {
            val v: View = adapter!!.getView(i, null, this)
            addView(v, i)
        }
    }

}