package com.nze.nzexchange.controller.my.asset.legal

import android.view.View
import android.widget.TextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.controller.base.NBaseActivity
import kotlinx.android.synthetic.main.activity_withdraw_success.*

/**
 * 提现已成交状态
 */
class WithdrawSuccessActivity : NBaseActivity() {
    val bankTv: TextView by lazy { tv_bank_aws }
    val amountTv: TextView by lazy { tv_amount_aws }
    val statusTv: TextView by lazy { tv_status_aws }
    val applyTimeTv: TextView by lazy { tv_apply_time_aws }
    val processTimeTv: TextView by lazy { tv_process_time_aws }
    val successTimeTv: TextView by lazy { tv_success_time_aws }
    val changeBankTv: TextView by lazy { tv_change_bank_aws }
    val foundTimeTv: TextView by lazy { tv_found_time_aws }
    val orderNumTv: TextView by lazy { tv_order_num_aws }

    override fun getRootView(): Int = R.layout.activity_withdraw_success

    override fun initView() {
    }

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {
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


}
