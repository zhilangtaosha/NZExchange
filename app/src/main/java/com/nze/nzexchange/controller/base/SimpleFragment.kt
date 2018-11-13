package com.nze.nzexchange.controller.base

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nze.nzeframework.netstatus.NetChangeObserver
import com.nze.nzeframework.netstatus.NetStateReceiver
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.ui.BaseActivity
import com.nze.nzeframework.ui.BaseFragment
import com.nze.nzeframework.utils.EventCenter
import org.greenrobot.eventbus.EventBus

abstract class SimpleFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(getRootView(), container, false)
        initView(rootView)
        return rootView
    }

    abstract fun getRootView(): Int
    abstract fun initView(rootView: View)

}