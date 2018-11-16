package com.nze.nzexchange.controller.transfer

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.utils.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.config.AccountType
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.controller.base.NBaseActivity
import kotlinx.android.synthetic.main.activity_select_account.*

class SelectAccountActivity : NBaseActivity(), View.OnClickListener {


    override fun getRootView(): Int = R.layout.activity_select_account

    override fun initView() {
        layout_bibi_asa.setOnClickListener(this)
        layout_withdraw_asa.setOnClickListener(this)
        layout_otc_asa.setOnClickListener(this)
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

    override fun getContainerTargetView(): View? = null

    override fun onClick(v: View?) {
        val intent: Intent = Intent()
        when (v?.id) {
            R.id.layout_bibi_asa -> {
                intent.putExtra(IntentConstant.PARAM_ACCOUNT, AccountType.BIBI)
            }
            R.id.layout_withdraw_asa -> {
                intent.putExtra(IntentConstant.PARAM_ACCOUNT, AccountType.WITHDRAW)
            }
            R.id.layout_otc_asa -> {
                intent.putExtra(IntentConstant.PARAM_ACCOUNT, AccountType.OTC)
            }
        }
        SelectAccountActivity@ this.setResult(Activity.RESULT_OK, intent)
        SelectAccountActivity@ this.finish()
    }
}
