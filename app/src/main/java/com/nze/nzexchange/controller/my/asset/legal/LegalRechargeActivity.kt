package com.nze.nzexchange.controller.my.asset.legal

import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.my.asset.transfer.SelectRechargePopup
import com.nze.nzexchange.extend.setDrawables
import com.nze.nzexchange.widget.CommonButton
import com.nze.nzexchange.widget.CommonTopBar
import kotlinx.android.synthetic.main.activity_legal_recharge.*

class LegalRechargeActivity : NBaseActivity() {
    val topBar: CommonTopBar by lazy { ctb_alr }
    val rechargeMode: TextView by lazy { tv_recharge_mode_alr }
    val amountEt: EditText by lazy { et_amount_alr }
    val nextBtn: CommonButton by lazy { btn_next_alr }
    val selectPopup: SelectRechargePopup by lazy { SelectRechargePopup(this) }


    override fun getRootView(): Int = R.layout.activity_legal_recharge

    override fun initView() {

        rechargeMode.setOnClickListener {
            selectPopup.showPopupWindow()
        }
        refreshLayout()
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

    fun refreshLayout() {
        rechargeMode.setDrawables(R.mipmap.add_icon, null, null, null)
        rechargeMode.text = "请先绑定充值方式"
    }
}
