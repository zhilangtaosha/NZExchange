package com.nze.nzexchange.controller.my.authentication

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.RealNameAuthenticationBean
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.controller.base.NBaseActivity
import kotlinx.android.synthetic.main.activity_authentication_home.*

class AuthenticationHomeActivity : NBaseActivity(), View.OnClickListener {
    private val primaryLayout: RelativeLayout by lazy { layout_primary_aah }
    private val primaryStatusTv: TextView by lazy { status_primary_aah }
    private val primaryIv: ImageView by lazy { iv_primary_aah }
    private val primaryDetailLayout: LinearLayout by lazy { layout_detail_primary_aah }
    private val userNameTv: TextView by lazy { user_name_aah }
    private val accountTv: TextView by lazy { account_aah }
    private val countryTv: TextView by lazy { country_aah }
    private val certificateTv: TextView by lazy { certificate_aah }
    private val realNameLayout: RelativeLayout by lazy { layout_real_name_aah }
    private val realNameStatusTv: TextView by lazy { status_real_name_aah }
    private val realNameIv: ImageView by lazy { iv_real_name_aah }
    private val vedioLayout: RelativeLayout by lazy { layout_vedio_aah }
    private val vedioStatusTv: TextView by lazy { status_vedio_aah }
    private val vedioIv: ImageView by lazy { iv_vedio_aah }
    var realNameAuthenticationBean: RealNameAuthenticationBean? = null
    var userBean = UserBean.loadFromApp()

    companion object {
        val INTENT_REAL_NAME_BEAN = "realName"
    }

    override fun getRootView(): Int = R.layout.activity_authentication_home
    override fun initView() {
        realNameAuthenticationBean = intent.getParcelableExtra<RealNameAuthenticationBean>(INTENT_REAL_NAME_BEAN)

        realNameAuthenticationBean?.let {
            userNameTv.text = it.membName
            certificateTv.text = it.membIdentitycard
        }

        if (!userBean?.userPhone.isNullOrEmpty()) {
            accountTv.text = userBean?.userPhone
        } else {
            accountTv.text = userBean?.userEmail
        }

        primaryLayout.setOnClickListener(this)
        realNameLayout.setOnClickListener(this)
        vedioLayout.setOnClickListener(this)
    }

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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

    override fun getContainerTargetView(): View? = null


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.layout_primary_aah -> {
                skipActivity(PrimaryAuthenticationActivity::class.java)
            }
            R.id.layout_real_name_aah -> {
                skipActivity(RealNameAuthenticationActivity::class.java)
            }
            R.id.layout_vedio_aah -> {
                skipActivity(VideoAuthenticationActivity::class.java)
            }
        }
    }
}
