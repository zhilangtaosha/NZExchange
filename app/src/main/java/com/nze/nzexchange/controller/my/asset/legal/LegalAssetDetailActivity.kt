package com.nze.nzexchange.controller.my.asset.legal

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.FinancialRecordBean
import com.nze.nzexchange.bean.LegalAccountBean
import com.nze.nzexchange.bean.RealNameAuthenticationBean
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.config.LegalConfig
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.common.CheckPermission
import com.nze.nzexchange.controller.my.asset.ConcreteAssetAdapter
import com.nze.nzexchange.controller.my.asset.presenter.LegalP
import com.nze.nzexchange.extend.formatForLegal
import kotlinx.android.synthetic.main.activity_legal_asset_detail.*

class LegalAssetDetailActivity : NBaseActivity(), View.OnClickListener {

    val legalP: LegalP by lazy { LegalP(this) }
    private val totalAmountTv: TextView by lazy { tv_total_amount_alad }
    private val rechargeBtn: Button by lazy { btn_recharge_alad }
    private val withdrawBtn: Button by lazy { btn_withdraw_alad }
    private val transferBtn: Button by lazy { btn_transfer_alad }
    private val listView: ListView by lazy { listView_alad }
    private var userBean = UserBean.loadFromApp()
    var realNameAuthenticationBean: RealNameAuthenticationBean? = null
    lateinit var accountBean: LegalAccountBean
    val concreteAdapter: ConcreteAssetAdapter by lazy { ConcreteAssetAdapter(this, FinancialRecordBean.ACCOUNT_LEGAL) }

    companion object {
        fun skip(context: Context, realNameAuthenticationBean: RealNameAuthenticationBean?) {
            context.startActivity(Intent(context, LegalAssetDetailActivity::class.java)
                    .putExtra(IntentConstant.PARAM_AUTHENTICATION, realNameAuthenticationBean))
        }
    }

    override fun getRootView(): Int = R.layout.activity_legal_asset_detail

    override fun initView() {
        intent?.let {
            realNameAuthenticationBean = it.getParcelableExtra(IntentConstant.PARAM_AUTHENTICATION)
        }
        rechargeBtn.setOnClickListener(this)
        withdrawBtn.setOnClickListener(this)
        transferBtn.setOnClickListener(this)
        listView.adapter = concreteAdapter

        getFinancialRecord()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_recharge_alad -> {
                CheckPermission.getInstance()
                        .commonCheck(this, CheckPermission.ACC_FILL, "法币充值需要完成以下设置，请检查", onPass = {
                            skipActivity(LegalRechargeActivity::class.java)
                        })
            }
            R.id.btn_withdraw_alad -> {
                CheckPermission.getInstance()
                        .commonCheck(this, CheckPermission.ACC_PICKFUND, "法币提现需要完成以下设置，请检查", onPass = {
                            LegalWithdrawActivity.skip(this, realNameAuthenticationBean!!, accountBean)
                        })
            }
            R.id.btn_transfer_alad -> {
                CheckPermission.getInstance()
                        .commonCheck(this, CheckPermission.ACC_MOVE, "法币划转需要完成以下设置，请检查", onPass = {
                            LegalTransferActivity.skip(this, accountBean)
                        })
            }

        }
    }

    override fun onResume() {
        super.onResume()
        legalP.getLegalAccountInfo(userBean!!, {
            if (it.success) {
                accountBean = it.result
                totalAmountTv.text = accountBean.accAbleAmount.formatForLegal()
            }
        }, onError)
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

    //获取最近财务记录
    fun getFinancialRecord() {
        FinancialRecordBean.getFinancialRecord(userBean!!.userId, null, FinancialRecordBean.ACCOUNT_LEGAL)
                .compose(netTfWithDialog())
                .subscribe({
                    if (it.success) {
                        concreteAdapter.group = it.result
                    }
                }, onError)
    }
}
