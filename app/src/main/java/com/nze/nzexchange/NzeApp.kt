package com.nze.nzexchange

import com.nze.nzeframework.ui.BaseApplication
import kotlin.properties.Delegates

class NzeApp : BaseApplication() {

    companion object {
        lateinit var instance: NzeApp;

    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }


}