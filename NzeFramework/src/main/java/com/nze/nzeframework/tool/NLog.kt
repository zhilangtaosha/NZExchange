package com.nze.nzeframework.tool

import android.util.Log

class NLog {

    companion object {
        const val TAG: String = "nz"
        fun i(str: String) {
            Log.i(TAG, str)
        }
    }

}
