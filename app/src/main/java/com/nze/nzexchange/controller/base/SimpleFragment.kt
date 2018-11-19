package com.nze.nzexchange.controller.base

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class SimpleFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(getRootView(), container, false)
        initView(rootView)
        return rootView
    }

    abstract fun getRootView(): Int
    abstract fun initView(rootView: View)

}