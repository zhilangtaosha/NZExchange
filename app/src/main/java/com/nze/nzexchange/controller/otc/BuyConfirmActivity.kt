package com.nze.nzexchange.controller.otc

import android.view.View
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.SubOrderInfoBean
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.widget.CommonTopBar
import kotlinx.android.synthetic.main.activity_buy_confirm.*

class BuyConfirmActivity : NBaseActivity() {
    lateinit var subOrderInfoBean: SubOrderInfoBean
    override fun getRootView(): Int = R.layout.activity_sale_confirm

    override fun initView() {
        intent?.let {
            subOrderInfoBean = it.getParcelableExtra(IntentConstant.PARAM_PLACE_AN_ORDER)
        }
        (topbar_abc as CommonTopBar).setRightClick {

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
