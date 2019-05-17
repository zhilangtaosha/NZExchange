package com.nze.nzexchange.controller.my.asset.legal

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.bean2.RechargeModeBean
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.controller.base.NBaseActivity
import kotlinx.android.synthetic.main.activity_recharge_confirm.*

/**
 * 法币充值确认页面
 */
class RechargeConfirmActivity : NBaseActivity() {
    val moneyTv: TextView by lazy { tv_money_arc }
    val bankNameLayout: LinearLayout by lazy { layout_bank_name_arc }
    val bankNameTv: TextView by lazy { tv_bank_name_arc }
    val accountKeyTv: TextView by lazy { tv_account_key_arc }
    val accountValueTv: TextView by lazy { tv_account_value_arc }
    val payeeTv: TextView by lazy { tv_payee_arc }
    val codeTv: TextView by lazy { tv_code_arc }
    val cancelTv: TextView by lazy { tv_cancel_arc }
    val confirmTv: TextView by lazy { tv_confirm_arc }
    var type: Int = RechargeModeBean.BANK
    var code: Int = 0
    var amount: String? = null

    companion object {
        fun skip(context: Context, type: Int, code: String, amount: String) {
            context.startActivity(Intent(context, RechargeConfirmActivity::class.java)
                    .putExtra(IntentConstant.PARAM_TYPE, type)
                    .putExtra(IntentConstant.PARAM_CODE, code)
                    .putExtra(IntentConstant.PARAM_ACCOUNT, amount))
        }
    }

    override fun getRootView(): Int = R.layout.activity_recharge_confirm
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
