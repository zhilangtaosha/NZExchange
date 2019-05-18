package com.nze.nzexchange.controller.my.asset.legal

import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.common.TipDialog
import com.nze.nzexchange.controller.common.VerifyPopup
import com.nze.nzexchange.controller.my.paymethod.presenter.PayMethodPresenter
import com.nze.nzexchange.controller.my.paymethod.presenter.PayMethodView
import com.nze.nzexchange.widget.CommonTopBar
import kotlinx.android.synthetic.main.activity_legal_withdraw.*

class LegalWithdrawActivity : NBaseActivity(), PayMethodView {
    val topBar: CommonTopBar by lazy { ctb_alw }
    val bankCardTv: TextView by lazy { tv_bank_card_alw }
    val limitTv: TextView by lazy { tv_limit_alw }
    val moneyEt: EditText by lazy { et_money_alw }
    val availableTv: TextView by lazy { tv_available_alw }
    val feeTv: TextView by lazy { tv_fee_alw }
    val nextBtn: Button by lazy { btn_next }
    var userBean = UserBean.loadFromApp()
    val pmp: PayMethodPresenter by lazy { PayMethodPresenter(this, this) }
    val verifyPopup by lazy { VerifyPopup(this).apply {
        account="13603041983"
    } }

    override fun getRootView(): Int = R.layout.activity_legal_withdraw

    override fun initView() {

        topBar.setRightClick {
            skipActivity(LegalWithdrawHistoryActivity::class.java)
        }
        nextBtn.setOnClickListener {
            verifyPopup.showPopupWindow()
//            skipActivity(WithdrawPendingActivity::class.java)
        }


    }

    override fun onResume() {
        super.onResume()
        pmp.getAllPayMethod(userBean!!, {
            if (it.success) {
                val payMethodBean = it.result
                if (!pmp.isAddBank(payMethodBean)) {
                    TipDialog.getInstance(this)
                            .show("提现必须设置银行收款方式", false, {
                                finish()
                            })
                } else {
                    val s = payMethodBean.accmoneyBankcard!!
                    bankCardTv.text = "${payMethodBean.accmoneyBanktype}(${s.substring(s.length - 4)})"
                }
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


}
