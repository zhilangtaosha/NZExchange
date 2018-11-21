package com.nze.nzexchange.controller.my.paymethod

import android.content.Intent
import android.view.View
import android.widget.AdapterView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.PayMethodBean
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.controller.base.NBaseActivity
import kotlinx.android.synthetic.main.activity_set_pay_method.*

class SetPayMethodActivity : NBaseActivity(), AdapterView.OnItemClickListener {


    val adapter: SetPayMethodAdapter by lazy {
        SetPayMethodAdapter(this)
    }

    override fun getRootView(): Int = R.layout.activity_set_pay_method

    override fun initView() {
        lv_aspm.onItemClickListener = this
        lv_aspm.adapter = adapter
        adapter.group = PayMethodBean.getPayList()
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

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (position) {
            0 -> {
                skipActivity(AddBankActivity::class.java)
            }
            1 -> {
                startActivity(Intent(this, AddZhifubaoActivity::class.java).putExtra(IntentConstant.PARAM_PAY, IntentConstant.TYPE_ZHIFUBAO))
            }
            2 -> {
                startActivity(Intent(this, AddZhifubaoActivity::class.java).putExtra(IntentConstant.PARAM_PAY, IntentConstant.TYPE_WECHAT))
            }
        }
    }
}
