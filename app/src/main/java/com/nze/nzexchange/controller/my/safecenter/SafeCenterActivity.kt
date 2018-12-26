package com.nze.nzexchange.controller.my.safecenter

import android.view.View
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.controller.base.NBaseActivity
import kotlinx.android.synthetic.main.activity_safe_center.*

class SafeCenterActivity : NBaseActivity(), View.OnClickListener {


    val bindPhoneTv: TextView by lazy { tv_bind_phone_asc }//手机绑定
    val bindEmailTv: TextView by lazy { tv_bind_email_asc }//邮箱绑定
    val bindGoogleTv: TextView by lazy { tv_bind_google_asc }//谷歌绑定
    val gesturePasswordSwitch: Switch by lazy { switch_gesture_password_asc }//手势密码
    val fingerprintPasswordSwitch: Switch by lazy { switch_fingerprint_password_asc }//指纹密码
    val fundPasswordIv: ImageView by lazy { iv_fund_password_asc }//资金密码
    val modifyPasswordIv: ImageView by lazy { iv_modify_password_asc }//修改登录密码

    override fun getRootView(): Int = R.layout.activity_safe_center

    override fun initView() {
        bindPhoneTv.setOnClickListener(this)
        bindEmailTv.setOnClickListener(this)
        bindGoogleTv.setOnClickListener(this)
        fundPasswordIv.setOnClickListener(this)
        modifyPasswordIv.setOnClickListener(this)
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
            R.id.tv_bind_phone_asc -> {
            }
            R.id.tv_bind_email_asc -> {
                skipActivity(BindEmailActivity::class.java)
            }
            R.id.tv_bind_google_asc -> {
                skipActivity(BindGoogleActivity::class.java)
            }
            R.id.iv_fund_password_asc -> {
            }
            R.id.iv_modify_password_asc -> {
            }
        }
    }
}
