package com.nze.nzexchange.tools

import android.os.Build
import android.text.Html
import android.text.Spanned


class TextTool {

    companion object {
        fun fromHtml(source: String): Spanned {
            return if (Build.VERSION.SDK_INT < 24) {
                Html.fromHtml(source)
            } else {
                Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY)
            }
        }
    }
}
