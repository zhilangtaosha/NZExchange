package com.nze.nzexchange.controller.otc.tradelist

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.utils.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.tools.getNColor
import kotlinx.android.synthetic.main.activity_trade_complete_detail.*

class TradeCommonDetailActivity : NBaseActivity() {
    var type: Int = TradeCommonFragment.TYPE_COMPLETED
    override fun getRootView(): Int = R.layout.activity_trade_complete_detail

    override fun initView() {
        intent?.let {
            type = it.getIntExtra(TradeCommonFragment.PARAM_TYPE, TradeCommonFragment.TYPE_COMPLETED)
        }

        if (type == TradeCommonFragment.TYPE_COMPLETED) {
            tv_status_atcd.setTextColor(getNColor(R.color.color_FF6D87A8))
            layout_complete_atcd.visibility = View.VISIBLE
        } else {
            tv_status_atcd.setTextColor(getNColor(R.color.color_FF7F8592))
            layout_complete_atcd.visibility = View.GONE

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
