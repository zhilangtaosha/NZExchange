package com.nze.nzeframework.ui

import com.nze.nzexchange.bean.Result
import com.nze.nzexchange.bean.SetPayMethodBean
import com.nze.nzexchange.controller.base.NBaseActivity
import java.lang.ref.WeakReference


abstract class BaseActivityP(var activity: NBaseActivity) {
    private val wActivity: WeakReference<NBaseActivity> = WeakReference<NBaseActivity>(activity)

    open fun getAct(): NBaseActivity {
        return wActivity.get()!!
    }


}