package com.nze.nzexchange

import com.nze.nzeframework.ui.BaseApplication
import kotlin.properties.Delegates

class NzeApp:BaseApplication(){

    companion object {
         var instance:NzeApp?=null;

    }
    override fun onCreate() {
        super.onCreate()
        instance=this
    }



}