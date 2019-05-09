package com.nze.nzexchange.tools

import android.content.ClipboardManager
import android.content.Context
import com.nze.nzexchange.NzeApp

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/5/8
 */
class CopyTool {

    companion object {
        fun copy(text: String): Boolean {
            try {
                val clip: ClipboardManager by lazy { NzeApp.instance.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager }
                clip.text = text
                return true
            } catch (e: Exception) {
                return false
            }
        }
    }
}