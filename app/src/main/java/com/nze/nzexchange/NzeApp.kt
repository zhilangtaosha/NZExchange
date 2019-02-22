package com.nze.nzexchange

import android.content.Context
import android.support.multidex.MultiDex
import com.nze.nzeframework.ui.BaseApplication
import com.nze.nzexchange.bean.UserBean
import com.uuzuche.lib_zxing.activity.ZXingLibrary
import kotlin.properties.Delegates

class NzeApp : BaseApplication() {
    var userId = "007"
    var userBean: UserBean? = null

    companion object {
        lateinit var instance: NzeApp;

    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        ZXingLibrary.initDisplayOpinion(this)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}