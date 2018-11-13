package com.nze.nzexchange.controller.otc.buy

import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.utils.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.controller.base.NBaseActivity
import kotlinx.android.synthetic.main.activity_buy.*

class BuyActivity : NBaseActivity(), View.OnClickListener {


    override fun getRootView(): Int = R.layout.activity_buy

    override fun initView() {
        btn_confirm_ab.setOnClickListener(this)
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
            R.id.btn_confirm_ab -> {
                skipActivity(BuyConfirmActivity::class.java)
            }
            else -> {
            }
        }
    }

    fun createPayMethod():ImageView {
        val iv: ImageView = LayoutInflater.from(this).inflate(R.layout.image_pay, null) as ImageView
        iv.setImageResource(R.mipmap.wechat_icon)
        return iv
    }

}
