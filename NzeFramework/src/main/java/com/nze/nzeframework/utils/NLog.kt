package com.nze.nzeframework.utils

import android.util.Log
import com.nze.nzeframework.config.Config

object NLog {
    fun i(str: String) {
        Log.i(Config.TAG, str)
    }
}
