package com.nze.nzexchange

import com.nze.nzeframework.ui.BaseApplication
import com.nze.nzexchange.bean.UserBean
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
    }


}