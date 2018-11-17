package com.nze.nzeframework.ui

import java.lang.ref.WeakReference

abstract class BaseActivityPresenter<T>(var activity: BaseActivity, mIView: T) {
    val wActivity: WeakReference<BaseActivity> = WeakReference<BaseActivity>(activity)
}