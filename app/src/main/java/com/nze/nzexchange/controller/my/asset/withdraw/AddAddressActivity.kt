package com.nze.nzexchange.controller.my.asset.withdraw

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.controller.base.NBaseActivity

/**
 * 添加新的提币地址
 * 添加完成后在列表展示
 */
class AddAddressActivity : NBaseActivity() {


    override fun getRootView(): Int = R.layout.activity_add_address

    override fun initView() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getOverridePendingTransitionMode(): TransitionMode {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isBindEventBusHere(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isBindNetworkListener(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onNetworkConnected(type: NetUtils.NetType) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onNetworkDisConnected() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getContainerTargetView(): View? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
