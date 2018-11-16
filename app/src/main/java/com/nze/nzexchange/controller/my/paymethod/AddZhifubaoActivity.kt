package com.nze.nzexchange.controller.my.paymethod

import android.view.View
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.utils.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.controller.base.NBaseActivity
import kotlinx.android.synthetic.main.activity_add_zhifubao.*

class AddZhifubaoActivity : NBaseActivity() {
    var type: Int = IntentConstant.TYPE_ZHIFUBAO

    override fun getRootView(): Int = R.layout.activity_add_zhifubao

    override fun initView() {
        intent?.let {
            type = it.getIntExtra(IntentConstant.PARAM_TYPE, IntentConstant.TYPE_ZHIFUBAO)
        }

        if (type == IntentConstant.TYPE_ZHIFUBAO) {
            ctb_aaz.setTitle("添加支付宝")
            et_cardno_value_aaz.hint = "请输入支付宝账号"
        } else {
            ctb_aaz.setTitle("添加微信")
            et_cardno_value_aaz.hint = "请输入微信账号"
        }
        layout_add_aaz.setOnClickListener {

        }
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


}
