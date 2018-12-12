package com.nze.nzexchange.controller.my


import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.NzeApp
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.config.EventCode
import com.nze.nzexchange.controller.base.NBaseFragment
import com.nze.nzexchange.controller.login.LoginActivity
import com.nze.nzexchange.controller.my.paymethod.SetPayMethodActivity
import com.nze.nzexchange.controller.my.setting.SettingActivity
import kotlinx.android.synthetic.main.fragment_my.view.*


class MyFragment : NBaseFragment(), View.OnClickListener {
    lateinit var rootView: View
    private val userNameTV: TextView by lazy { rootView.tv_user_name_my }
    private val moneyTv: TextView by lazy { rootView.tv_money_my }
    private val unitTv: TextView by lazy { rootView.tv_unit_my }
    private val moneyLayout: LinearLayout by lazy { rootView.layout_money_my }
    private val rechargeTv: TextView by lazy { rootView.tv_recharge_my }
    private val withdrawTv: TextView by lazy { rootView.tv_withdraw_my }
    private val assetTv: TextView by lazy { rootView.tv_asset_my }
    private val orderManageTv: TextView by lazy { rootView.tv_order_manage_my }
    private val payMethodTv: TextView by lazy { rootView.tv_pay_method_my }
    private val safeCenterTv: TextView by lazy { rootView.tv_safe_center_my }
    private val authenticationTv: TextView by lazy { rootView.tv_authentication_my }
    private val settingTv: TextView by lazy { rootView.tv_setting_my }

    var userBean: UserBean? = NzeApp.instance.userBean

    companion object {
        @JvmStatic
        fun newInstance() = MyFragment()
    }

    override fun getRootView(): Int = R.layout.fragment_my

    override fun initView(rootView: View) {
        this.rootView = rootView
        userNameTV.setOnClickListener(this)
        moneyLayout.setOnClickListener(this)
        rechargeTv.setOnClickListener(this)
        withdrawTv.setOnClickListener(this)
        assetTv.setOnClickListener(this)
        orderManageTv.setOnClickListener(this)
        payMethodTv.setOnClickListener(this)
        safeCenterTv.setOnClickListener(this)
        authenticationTv.setOnClickListener(this)
        settingTv.setOnClickListener(this)

        changeForLogin()
    }

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {
        if (eventCenter.eventCode == EventCode.CODE_LOGIN_SUCCUSS) {
            userBean = NzeApp.instance.userBean
            changeForLogin()
        }
    }


    override fun isBindEventBusHere(): Boolean = true

    override fun isBindNetworkListener(): Boolean = false

    override fun onNetworkConnected(type: NetUtils.NetType) {
    }

    override fun onNetworkDisConnected() {
    }

    override fun getContainerTargetView(): View? = null

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_user_name_my -> {
                skipActivity(LoginActivity::class.java)
            }
            R.id.layout_money_my -> {
            }
            R.id.tv_recharge_my -> {
            }
            R.id.tv_withdraw_my -> {
            }
            R.id.tv_asset_my -> {
            }
            R.id.tv_order_manage_my -> {
            }
            R.id.tv_pay_method_my -> {
                skipActivity(SetPayMethodActivity::class.java)
            }
            R.id.tv_safe_center_my -> {
            }
            R.id.tv_authentication_my -> {
            }
            R.id.tv_setting_my -> {
                skipActivity(SettingActivity::class.java)
            }
        }
    }


    fun changeForLogin() {
        if (userBean != null) {
            userNameTV.isClickable = false
            userNameTV.text = userBean?.userTag
        } else {
            userNameTV.isClickable = true
            userNameTV.text = "登录"
        }
    }
}
