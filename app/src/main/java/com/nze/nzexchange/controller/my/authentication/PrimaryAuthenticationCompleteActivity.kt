package com.nze.nzexchange.controller.my.authentication

import android.view.View
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.config.EventCode
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.main.MainActivity
import kotlinx.android.synthetic.main.activity_primary_authentication_complete.*
import org.greenrobot.eventbus.EventBus

class PrimaryAuthenticationCompleteActivity : NBaseActivity() {


    override fun getRootView(): Int = R.layout.activity_primary_authentication_complete

    override fun initView() {
        btn_goon_apac.setOnCommonClick {
            skipActivity(RealNameAuthenticationActivity::class.java)
        }
        btn_bibi_apac.setOnClickListener {
            EventBus.getDefault().post(EventCenter<Int>(EventCode.CODE_REFRESH_MAIN_ACT, 2))
            skipActivity(MainActivity::class.java)
        }
    }

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {

    }

    override fun getOverridePendingTransitionMode(): TransitionMode = TransitionMode.DEFAULT

    override fun isBindEventBusHere(): Boolean = false

    override fun isBindNetworkListener(): Boolean = false

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
