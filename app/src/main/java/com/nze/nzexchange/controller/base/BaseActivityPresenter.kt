package com.nze.nzeframework.ui

import com.nze.nzexchange.controller.base.NBaseActivity
import java.lang.ref.WeakReference

abstract class BaseActivityPresenter<T>(var activity: NBaseActivity, mIView: T) {
    private val wActivity: WeakReference<NBaseActivity> = WeakReference<NBaseActivity>(activity)




}