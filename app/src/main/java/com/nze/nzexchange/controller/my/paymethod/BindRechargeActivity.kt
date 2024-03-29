package com.nze.nzexchange.controller.my.paymethod

import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.NzeApp
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.PayMethodBean
import com.nze.nzexchange.bean.RealNameAuthenticationBean
import com.nze.nzexchange.bean.SetPayMethodBean
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.common.CheckPermission
import com.nze.nzexchange.controller.my.authentication.presenter.AuthenticationP
import com.nze.nzexchange.controller.my.authentication.presenter.AuthenticationView
import com.nze.nzexchange.controller.my.paymethod.presenter.PayMethodPresenter
import com.nze.nzexchange.controller.my.paymethod.presenter.PayMethodView
import kotlinx.android.synthetic.main.activity_bind_recharge.*

/**
 * 绑定充值方式
 */
class BindRechargeActivity : NBaseActivity(), AuthenticationView, AdapterView.OnItemClickListener {

    val lv: ListView by lazy { lv_abr }
    val list = PayMethodBean.getLegalPayList()

    val adapter: SetPayMethodAdapter by lazy {
        SetPayMethodAdapter(this)
    }
    val authenticationP by lazy { AuthenticationP(this, this) }
    val payMehodP by lazy { PayMethodPresenter(this) }


    var userBean = UserBean.loadFromApp()
    var authenticationBean: RealNameAuthenticationBean? = null

    override fun getRootView(): Int = R.layout.activity_bind_recharge

    override fun initView() {

        authenticationP.getAuthentication(userBean!!, {
            if (it.success) {
                authenticationBean = it.result
            }
        }, onError)


        lv.onItemClickListener = this
        lv.adapter = adapter
        adapter.group = list

        CheckPermission.getInstance()
                .commonCheck(this, CheckPermission.SET_PAY_METHOD, "设置收款方式需要完成以下设置，请检查", onReject = {
                    finish()
                })

    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (position) {
            0 -> {
                AddBankActivity.skip(this, authenticationBean!!)
            }
            1 -> {
                AddBpayActivity.skip(this, authenticationBean!!)
            }
            2 -> {
                AddOskoActivity.skip(this, authenticationBean!!)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getAllPayMethod()
    }

    fun getAllPayMethod() {
        payMehodP.getAllPayMethod(userBean!!, {
            val methodBean: SetPayMethodBean = it.result
            userBean!!.payMethod = methodBean
            NzeApp.instance.userBean = userBean
            methodBean?.let {
                if (!it.accmoneyBankcard.isNullOrEmpty())
                    list[0].status = "已设置"
                if (!it.accmoneyBpaySn.isNullOrEmpty())
                    list[1].status = "已设置"
                if (!it.accmoneyFrBankcard.isNullOrEmpty())
                    list[2].status = "已设置"
                adapter.group = list
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
