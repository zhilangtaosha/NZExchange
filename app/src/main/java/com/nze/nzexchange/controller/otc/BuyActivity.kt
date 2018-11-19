package com.nze.nzexchange.controller.otc

import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.tools.dp2px
import kotlinx.android.synthetic.main.activity_buy.*

class BuyActivity : NBaseActivity(), View.OnClickListener {

    var type = OtcContentFragment.TYPE_BUY
    override fun getRootView(): Int = R.layout.activity_buy

    override fun initView() {
        type = intent?.getIntExtra(OtcContentFragment.PARAM_TYPE, OtcContentFragment.TYPE_BUY)!!
        if (type == OtcContentFragment.TYPE_BUY) {
            ctb_ab.setTitle("买入UCC")
        } else {
            ctb_ab.setTitle("买出UCC")
        }

        btn_confirm_ab.setOnClickListener(this)

        layout_pay_ab.addView(createPayMethod(R.mipmap.wechat_icon))
        layout_pay_ab.addView(createPayMethod(R.mipmap.zhifubao_icon))
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
                if (type == OtcContentFragment.TYPE_BUY) {
                    skipActivity(BuyConfirmActivity::class.java)
                    Toast.makeText(this, "buy...", Toast.LENGTH_SHORT).show()
                } else {
                    skipActivity(SaleConfirmActivity::class.java)
                    Toast.makeText(this, "sale...", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
            }
        }
    }

    fun createPayMethod(iconId:Int): ImageView {
        val iv: ImageView = LayoutInflater.from(this).inflate(R.layout.image_pay, null) as ImageView
        iv.setImageResource(iconId)
        iv.setPadding(dp2px(5F, this),0,0,0)
        return iv
    }

}
