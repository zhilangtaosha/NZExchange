package com.nze.nzexchange.controller.my.asset.legal

import android.view.View
import android.widget.TextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.widget.CommonTopBar
import kotlinx.android.synthetic.main.activity_withdraw_pending.*

/**
 * 提现待处理状态页面
 */
class WithdrawPendingActivity : NBaseActivity() {
    val topBar: CommonTopBar by lazy { ctb_alwe }
    val applyTv: TextView by lazy { tv_apply_alwe }
    val applyTimeTv: TextView by lazy { tv_apply_time_alwe }
    val accountTv: TextView by lazy { tv_account_alwe }
    val accountTimeTv: TextView by lazy { tv_account_time_alwe }
    val historyTv: TextView by lazy { tv_history_alwe }


    override fun getRootView(): Int = R.layout.activity_withdraw_pending

    override fun initView() {
        topBar.setRightClick {
            onBackPressed()
        }

        historyTv.setOnClickListener {
            skipActivity(LegalWithdrawHistoryActivity::class.java)
        }
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
