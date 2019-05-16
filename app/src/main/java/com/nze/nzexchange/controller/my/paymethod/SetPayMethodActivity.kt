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
import com.nze.nzexchange.config.BusFlowTag
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.common.AuthorityDialog
import com.nze.nzexchange.controller.my.asset.recharge.RechargeCurrencyActivity
import com.nze.nzexchange.controller.my.authentication.AuthenticationHomeActivity
import com.nze.nzexchange.http.CommonRequest
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
    val list = PayMethodBean.getPayList()

    val adapter: SetPayMethodAdapter by lazy {
        SetPayMethodAdapter(this)
    }

    override fun getRootView(): Int = R.layout.activity_set_pay_method

    override fun initView() {
        realNameAuthenticationBean = intent.getParcelableExtra<RealNameAuthenticationBean>(AuthenticationHomeActivity.INTENT_REAL_NAME_BEAN)
        lv_aspm.onItemClickListener = this
        lv_aspm.adapter = adapter
        adapter.group = PayMethodBean.getPayList()

        busCheck()
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
            3 -> {
                AddBpayActivity.skip(this, realNameAuthenticationBean!!)
            }
            4 -> {
                AddOskoActivity.skip(this, realNameAuthenticationBean!!)
            }
        }
    }

    /**
     * 设置收款方式权限判断
     * 有权限，请求接口判断是否已经设置过
     * 没有权限，通知用户，并关闭当前页
     */
    fun busCheck() {
        CommonRequest.busCheck(userBean!!, BusFlowTag.SET_PAY_METHOD)
                .compose(netTfWithDialog())
                .subscribe({
                    if (it.success) {
                        userBean = userBean?.apply {
                            SetPayMethodBean.getPayMethodNet(tokenReqVo.tokenUserId, tokenReqVo.tokenUserKey, tokenReqVo.tokenSystreeId)
                                    .compose(netTfWithDialog())
                                    .subscribe({
                                        if (it.success) {
                                            val methodBean: SetPayMethodBean = it.result
                                            this.payMethod = methodBean
                                            NzeApp.instance.userBean = this
                                            methodBean?.let {
                                                if (!it.accmoneyBankcard.isNullOrEmpty())
                                                    list[0].status = "已设置"
                                                if (!it.accmoneyZfburl.isNullOrEmpty())
                                                    list[1].status = "已设置"
                                                if (!it.accmoneyWeixinurl.isNullOrEmpty())
                                                    list[2].status = "已设置"
                                                if (!it.accmoneyBpaySn.isNullOrEmpty())
                                                    list[3].status = "已设置"
                                                if (!it.accmoneyFrBankcard.isNullOrEmpty())
                                                    list[4].status = "已设置"
                                                adapter.group = list
                                            }
                                        }
                                    }, onError)
                        }
                    } else {
                        if (it.cause != null && it.cause.size > 0) {
                            AuthorityDialog.getInstance(this)
                                    .show("设置收款方式需要完成以下设置，请检查",
                                            it.cause) {
                                        finish()
                                    }
                        }
                    }
                }, onError)
    }
}
