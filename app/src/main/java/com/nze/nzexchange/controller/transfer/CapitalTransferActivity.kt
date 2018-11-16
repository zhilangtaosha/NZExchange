package com.nze.nzexchange.controller.transfer

import android.app.Activity
import android.content.Intent
import android.view.View
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.ui.BaseActivity
import com.nze.nzeframework.utils.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.config.AccountType
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.controller.base.NBaseActivity
import kotlinx.android.synthetic.main.activity_capital_transfer.*

/**
 * 资金划转
 */
class CapitalTransferActivity : NBaseActivity(), View.OnClickListener {
    val REQUEST_CODE_OUT: Int = 0;
    val REQUEST_CODE_IN: Int = 1;

    override fun getRootView(): Int = R.layout.activity_capital_transfer

    override fun initView() {
        tv_out_account_act.setOnClickListener(this)
        tv_in_account_act.setOnClickListener(this)
    }

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getOverridePendingTransitionMode(): BaseActivity.TransitionMode = BaseActivity.TransitionMode.DEFAULT

    override fun isBindEventBusHere(): Boolean = false

    override fun isBindNetworkListener(): Boolean = false

    override fun onNetworkConnected(type: NetUtils.NetType) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onNetworkDisConnected() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getContainerTargetView(): View? = null

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_out_account_act -> {
                startActivityForResult(Intent(this, SelectAccountActivity::class.java), REQUEST_CODE_OUT)

            }
            R.id.tv_in_account_act -> {
                startActivityForResult(Intent(this, SelectAccountActivity::class.java), REQUEST_CODE_IN)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val type: Int = data?.getIntExtra(IntentConstant.PARAM_ACCOUNT, AccountType.BIBI)!!
            if (requestCode == REQUEST_CODE_OUT) {
                tv_out_account_act.text = AccountType.getAccountName(type)
            } else if (requestCode == REQUEST_CODE_IN) {
                tv_in_account_act.text = AccountType.getAccountName(type)
            }
        }
    }


}
