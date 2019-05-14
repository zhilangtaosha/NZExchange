package com.nze.nzexchange.controller.my.authentication

import android.content.Intent
import android.view.View
import android.widget.Button
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.RealNameAuthenticationBean
import com.nze.nzexchange.controller.base.NBaseActivity
import kotlinx.android.synthetic.main.activity_authentication_fail.*

/**
 *  审核失败
 */
class AuthenticationFailActivity : NBaseActivity() {
    val againBtn: Button by lazy { btn_again_aaf }
    var realNameAuthenticationBean: RealNameAuthenticationBean? = null
    override fun getRootView(): Int = R.layout.activity_authentication_fail

    override fun initView() {
        realNameAuthenticationBean = intent.getParcelableExtra<RealNameAuthenticationBean>(AuthenticationHomeActivity.INTENT_REAL_NAME_BEAN)
        againBtn.setOnClickListener {
            startActivity(Intent(this, PrimaryAuthenticationActivity::class.java)
                    .putExtra(AuthenticationHomeActivity.INTENT_REAL_NAME_BEAN, realNameAuthenticationBean))
            finish()
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
