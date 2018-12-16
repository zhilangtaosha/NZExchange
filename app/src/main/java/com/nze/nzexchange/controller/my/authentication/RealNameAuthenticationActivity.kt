package com.nze.nzexchange.controller.my.authentication

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.widget.CommonButton
import kotlinx.android.synthetic.main.activity_real_name_authentication.*

class RealNameAuthenticationActivity : NBaseActivity(), View.OnClickListener {


    val frontIv: TextView by lazy { iv_front_arna }
    val backIv: TextView by lazy { iv_back_arna }
    val holdIv: TextView by lazy { iv_hold_arna }
    val submitBtn: CommonButton by lazy { btn_submit_arna }

    override fun getRootView(): Int = R.layout.activity_real_name_authentication

    override fun initView() {

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

    override fun getContainerTargetView(): View? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_front_arna -> {
            }
            R.id.iv_back_arna -> {
            }
            R.id.iv_hold_arna -> {
            }
            R.id.btn_submit_arna -> {
            }
        }
    }


}
