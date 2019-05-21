package com.nze.nzexchange.controller.my.paymethod

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.NzeApp
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.RealNameAuthenticationBean
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.common.FundPasswordPopup
import com.nze.nzexchange.controller.my.paymethod.presenter.PayMethodPresenter
import com.nze.nzexchange.controller.my.paymethod.presenter.PayMethodView
import com.nze.nzexchange.extend.getContent
import com.nze.nzexchange.extend.getValue
import kotlinx.android.synthetic.main.activity_add_bpay.*

class AddBpayActivity : NBaseActivity(), PayMethodView {
    val pmp by lazy { PayMethodPresenter(this, this) }
    val nameTv: TextView by lazy { tv_name_value_aab }
    val accountEt: EditText by lazy { et_account_aab }
    val saveBtn: Button by lazy { btn_save_aac }
    var authenticationBean: RealNameAuthenticationBean? = null
    var userBean = UserBean.loadFromApp()
    val fundPopup: FundPasswordPopup by lazy {
        FundPasswordPopup(this).apply {
            onPasswordClick = {
                pmp.addPayMethod(userBean!!,
                        {
                            if (it.success) {
                                val payMethodBean = it.result
                                userBean?.payMethod?.accmoneyBpaySn = payMethodBean.accmoneyBpaySn

                                NzeApp.instance.userBean = userBean
                                this@AddBpayActivity.finish()
                            } else {
                                showToast(it.message)
                            }
                        }, onError, accmoneyBpaySn = accountEt.getContent(), curBuspwUcode = it)
            }
        }
    }

    companion object {
        fun skip(context: Context, authenticationBean: RealNameAuthenticationBean) {
            context.startActivity(Intent(context, AddBpayActivity::class.java)
                    .putExtra(IntentConstant.INTENT_REAL_NAME_BEAN, authenticationBean))
        }
    }

    override fun getRootView(): Int = R.layout.activity_add_bpay

    override fun initView() {
        authenticationBean = intent.getParcelableExtra<RealNameAuthenticationBean>(IntentConstant.INTENT_REAL_NAME_BEAN)
        authenticationBean?.let {
            nameTv.text = it.membName
        }

        userBean?.payMethod?.let {
            accountEt.setText(it.accmoneyBpaySn?.getValue()?:"")
        }

        saveBtn.setOnClickListener {
            fundPopup.showPopupWindow()
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
