package com.nze.nzexchange.controller.my.asset.legal

import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.widget.CommonTopBar
import kotlinx.android.synthetic.main.activity_legal_withdraw.*

class LegalWithdrawActivity : NBaseActivity() {
    val topBar: CommonTopBar by lazy { ctb_alw }
    val bankCardTv: TextView by lazy { tv_bank_card_alw }
    val limitTv: TextView by lazy { tv_limit_alw }
    val moneyEt: EditText by lazy { et_money_alw }
    val availableTv: TextView by lazy { tv_available_alw }
    val feeTv: TextView by lazy { tv_fee_alw }
    val nextBtn: Button by lazy { btn_next }

    override fun getRootView(): Int = R.layout.activity_legal_withdraw

    override fun initView() {
        topBar.setRightClick {
            skipActivity(LegalWithdrawHistoryActivity::class.java)
        }
        nextBtn.setOnClickListener {
            skipActivity(WithdrawPendingActivity::class.java)
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