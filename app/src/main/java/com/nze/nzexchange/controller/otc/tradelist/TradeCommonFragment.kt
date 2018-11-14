package com.nze.nzexchange.controller.otc.tradelist


import android.view.View
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.utils.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.controller.base.NBaseFragment
import com.nze.nzexchange.controller.otc.OtcFragment


/**
 * 交易单已取消、已完成
 *
 */
class TradeCommonFragment : NBaseFragment() {


    companion object {
        @JvmStatic
        fun newInstance() = TradeCommonFragment()
    }

    override fun getRootView(): Int = R.layout.common_ptrlv


    override fun initView(rootView: View) {
    }

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

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
