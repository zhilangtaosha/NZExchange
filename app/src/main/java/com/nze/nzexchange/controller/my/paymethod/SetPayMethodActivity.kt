package com.nze.nzexchange.controller.my.paymethod

import android.content.Intent
import android.view.View
import android.widget.AdapterView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.NzeApp
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.PayMethodBean
import com.nze.nzexchange.bean.RealNameAuthenticationBean
import com.nze.nzexchange.bean.SetPayMethodBean
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.my.authentication.AuthenticationHomeActivity
import kotlinx.android.synthetic.main.activity_set_pay_method.*

/**
 * 设置支付方式主页
 * 设置支付方式的前提条件
 * 1，必须已经实名认证
 * 2，必须已经设置资金密码
 */
class SetPayMethodActivity : NBaseActivity(), AdapterView.OnItemClickListener {

    var userBean: UserBean? = UserBean.loadFromApp()
    var realNameAuthenticationBean: RealNameAuthenticationBean? = null

    val adapter: SetPayMethodAdapter by lazy {
        SetPayMethodAdapter(this)
    }

    override fun getRootView(): Int = R.layout.activity_set_pay_method

    override fun initView() {
        realNameAuthenticationBean = intent.getParcelableExtra<RealNameAuthenticationBean>(AuthenticationHomeActivity.INTENT_REAL_NAME_BEAN)
        lv_aspm.onItemClickListener = this
        lv_aspm.adapter = adapter
        adapter.group = PayMethodBean.getPayList()

        userBean = userBean?.apply {
            SetPayMethodBean.getPayMethodNet(tokenReqVo.tokenUserId, tokenReqVo.tokenUserKey, tokenReqVo.tokenSystreeId)
                    .compose(netTfWithDialog())
                    .subscribe({
                        if (it.success) {
                            this.payMethod = it.result
                            NzeApp.instance.userBean = this
                        }
                    }, onError)
        }
    }


    override fun <T> onEventComming(eventCenter: EventCenter<T>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (position) {
            0 -> {
                startActivity(Intent(this, AddBankActivity::class.java)
                        .putExtra(IntentConstant.INTENT_REAL_NAME_BEAN, realNameAuthenticationBean))
            }
            1 -> {
                startActivity(Intent(this, AddZhifubaoActivity::class.java)
                        .putExtra(IntentConstant.PARAM_PAY, IntentConstant.TYPE_ZHIFUBAO)
                        .putExtra(IntentConstant.INTENT_REAL_NAME_BEAN, realNameAuthenticationBean))
            }
            2 -> {
                startActivity(Intent(this, AddZhifubaoActivity::class.java)
                        .putExtra(IntentConstant.PARAM_PAY, IntentConstant.TYPE_WECHAT)
                        .putExtra(IntentConstant.INTENT_REAL_NAME_BEAN, realNameAuthenticationBean))
            }
        }
    }
}
