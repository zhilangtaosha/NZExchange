package com.nze.nzexchange.controller.my.asset.legal

import android.text.TextUtils.substring
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.NzeApp
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.SetPayMethodBean
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.bean2.RechargeModeBean
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.my.paymethod.presenter.PayMethodPresenter
import com.nze.nzexchange.controller.my.paymethod.presenter.PayMethodView
import com.nze.nzexchange.extend.setDrawables
import com.nze.nzexchange.widget.CommonButton
import com.nze.nzexchange.widget.CommonTopBar
import kotlinx.android.synthetic.main.activity_legal_recharge.*

class LegalRechargeActivity : NBaseActivity(), PayMethodView {
    val topBar: CommonTopBar by lazy { ctb_alr }
    val rechargeMode: TextView by lazy { tv_recharge_mode_alr }
    val amountEt: EditText by lazy { et_amount_alr }
    val nextBtn: CommonButton by lazy { btn_next_alr }
    val selectPopup: SelectRechargePopup by lazy {
        SelectRechargePopup(this).apply {
            onItemClick = {
                when (it.mode) {
                    RechargeModeBean.BANK -> {
                        if (!payMethodBean?.accmoneyBankcard.isNullOrEmpty()) {
                            val s = payMethodBean?.accmoneyBankcard!!
                            rechargeMode.setDrawables(R.mipmap.bank_icon, null, R.mipmap.right_arrow2, null)
                            rechargeMode.text = "${payMethodBean?.accmoneyBanktype}(${s.substring(s.length - 4)})"
                        }
                    }
                    RechargeModeBean.OSKO -> {
                        if (!payMethodBean?.accmoneyFrBankcard.isNullOrEmpty()) {
                            rechargeMode.setDrawables(R.mipmap.osko_icon, null, R.mipmap.right_arrow2, null)
                            rechargeMode.text = "OSKO"
                        }
                    }
                    RechargeModeBean.BPAY -> {
                        if (!payMethodBean?.accmoneyBpaySn.isNullOrEmpty()) {
                            rechargeMode.setDrawables(R.mipmap.bpay_icon, null, R.mipmap.right_arrow2, null)
                            rechargeMode.text = "BPAY"
                        }
                    }
                }
            }
        }
    }
    val payMehodP by lazy { PayMethodPresenter(this, this) }
    var userBean = UserBean.loadFromApp()
    var payMethodBean: SetPayMethodBean? = null

    override fun getRootView(): Int = R.layout.activity_legal_recharge

    override fun initView() {
        topBar.setRightClick {
            skipActivity(LegalWithdrawHistoryActivity::class.java)
        }
        rechargeMode.setOnClickListener {
            selectPopup.showPopupWindow()
        }

    }

    override fun onResume() {
        super.onResume()
        getAllPayMethod()
    }

    fun getAllPayMethod() {
        payMehodP.getAllPayMethod(userBean!!, {
            if (it.success) {
                payMethodBean = it.result
                userBean!!.payMethod = payMethodBean
                NzeApp.instance.userBean = userBean
                payMethodBean?.let {
                    if (!it.accmoneyBankcard.isNullOrEmpty()) {
                        val s = it.accmoneyBankcard!!
                        rechargeMode.setDrawables(R.mipmap.bank_icon, null, R.mipmap.right_arrow2, null)
                        rechargeMode.text = "${it.accmoneyBanktype}(${s.substring(s.length - 4)})"
                    } else if (!it.accmoneyFrBankcard.isNullOrEmpty()) {
                        rechargeMode.setDrawables(R.mipmap.osko_icon, null, R.mipmap.right_arrow2, null)
                        rechargeMode.text = "OSKO"
                    } else if (!it.accmoneyBpaySn.isNullOrEmpty()) {
                        rechargeMode.setDrawables(R.mipmap.bpay_icon, null, R.mipmap.right_arrow2, null)
                        rechargeMode.text = "BPAY"
                    } else {
                        rechargeMode.setDrawables(R.mipmap.add_icon, null, R.mipmap.right_arrow2, null)
                        rechargeMode.text = "请先绑定充值方式"
                    }
                }
                selectPopup.setRecharge(RechargeModeBean.getPayList(payMethodBean!!))
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

    fun refreshLayout() {
        rechargeMode.setDrawables(R.mipmap.add_icon, null, null, null)
        rechargeMode.text = "请先绑定充值方式"
    }
}
