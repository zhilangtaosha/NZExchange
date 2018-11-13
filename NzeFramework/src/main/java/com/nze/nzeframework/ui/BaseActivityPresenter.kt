package com.nze.nzeframework.ui

import java.lang.ref.WeakReference

abstract class BaseActivityPresenter<T>(var mWeakActivity:WeakReference<BaseActivity>,mIView:T)