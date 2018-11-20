package com.nze.nzeframework.ui

import com.nze.nzexchange.controller.base.NBaseFragment
import java.lang.ref.WeakReference

abstract class BaseFragmentPresenter<T>(var mFragment: NBaseFragment, mIView: T)