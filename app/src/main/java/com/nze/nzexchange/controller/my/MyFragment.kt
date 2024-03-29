package com.nze.nzexchange.controller.my


import android.content.Intent
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.NzeApp
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.LegalAccountBean
import com.nze.nzexchange.bean.RealNameAuthenticationBean
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.config.EventCode
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.config.LegalConfig
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.base.NBaseFragment
import com.nze.nzexchange.controller.bibi.BibiAllOrderActivity
import com.nze.nzexchange.controller.common.CheckPermission
import com.nze.nzexchange.controller.login.LoginActivity
import com.nze.nzexchange.controller.my.asset.MyAssetActivity
import com.nze.nzexchange.controller.my.asset.legal.LegalAssetDetailActivity
import com.nze.nzexchange.controller.my.asset.legal.LegalRechargeActivity
import com.nze.nzexchange.controller.my.asset.legal.LegalWithdrawActivity
import com.nze.nzexchange.controller.my.asset.presenter.LegalP
import com.nze.nzexchange.controller.my.authentication.AuthenticationFailActivity
import com.nze.nzexchange.controller.my.authentication.AuthenticationHomeActivity
import com.nze.nzexchange.controller.my.authentication.PrimaryAuthenticationActivity
import com.nze.nzexchange.controller.my.authentication.RealNameAuthenticationActivity
import com.nze.nzexchange.controller.my.paymethod.SetPayMethodActivity
import com.nze.nzexchange.controller.my.safecenter.SafeCenterActivity
import com.nze.nzexchange.controller.my.setting.SettingActivity
import kotlinx.android.synthetic.main.fragment_my.view.*


class MyFragment : NBaseFragment(), View.OnClickListener {
    val legalP: LegalP by lazy { LegalP(activity as NBaseActivity) }
    lateinit var rootView: View
    private val userNameTV: TextView by lazy { rootView.tv_user_name_my }
    private val legalAccountLayout: RelativeLayout by lazy { rootView.layout_legal_account_my }
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
    var realNameAuthenticationBean: RealNameAuthenticationBean? = null
    lateinit var accountBean: LegalAccountBean

    companion object {
        @JvmStatic
        fun newInstance() = MyFragment()
    }

    override fun getRootView(): Int = R.layout.fragment_my

    override fun initView(rootView: View) {
        this.rootView = rootView
        userNameTV.setOnClickListener(this)
        rechargeTv.setOnClickListener(this)
        withdrawTv.setOnClickListener(this)
        assetTv.setOnClickListener(this)
        orderManageTv.setOnClickListener(this)
        payMethodTv.setOnClickListener(this)
        safeCenterTv.setOnClickListener(this)
        authenticationTv.setOnClickListener(this)
        settingTv.setOnClickListener(this)
        legalAccountLayout.setOnClickListener(this)

        unitTv.text = "${LegalConfig.NAME}"
        changeForLogin()

    }

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {
        if (eventCenter.eventCode == EventCode.CODE_LOGIN_SUCCUSS || eventCenter.eventCode == EventCode.CODE_LOGOUT_SUCCESS) {
            userBean = NzeApp.instance.userBean
            changeForLogin()
        }


    }

    override fun onResume() {
        super.onResume()
        if (userBean != null) {
            getReanNameAuthentication()
            legalP.getLegalAccountInfo(userBean!!, {
                if (it.success) {
                    accountBean = it.result
                    moneyTv.text = "${accountBean.accAbleAmount}"
                }
            }, onError)
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
        if (!UserBean.isLogin()) {
            skipActivity(LoginActivity::class.java)
            return
        }
        when (v?.id) {
            R.id.tv_user_name_my -> {
                skipActivity(LoginActivity::class.java)
            }
            R.id.layout_legal_account_my -> {
                LegalAssetDetailActivity.skip(activity!!, realNameAuthenticationBean)
            }
            R.id.tv_recharge_my -> {
                CheckPermission.getInstance()
                        .commonCheck(activity as NBaseActivity, CheckPermission.ACC_FILL, "法币充值需要完成以下设置，请检查", onPass = {
                            skipActivity(LegalRechargeActivity::class.java)
                        })
            }
            R.id.tv_withdraw_my -> {
                CheckPermission.getInstance()
                        .commonCheck(activity as NBaseActivity, CheckPermission.ACC_PICKFUND, "法币提现需要完成以下设置，请检查", onPass = {
                            LegalWithdrawActivity.skip(activity!!, realNameAuthenticationBean!!, accountBean)
                        })

            }
            R.id.tv_asset_my -> {
                skipActivity(MyAssetActivity::class.java)
            }
            R.id.tv_order_manage_my -> {
                BibiAllOrderActivity.toAllOrderActivity(mBaseActivity, BibiAllOrderActivity.FROM_MY, "*")
            }
            R.id.tv_pay_method_my -> {
                startActivity(Intent(activity, SetPayMethodActivity::class.java)
                        .putExtra(IntentConstant.INTENT_REAL_NAME_BEAN, realNameAuthenticationBean))
            }
            R.id.tv_safe_center_my -> {
                skipActivity(SafeCenterActivity::class.java)
            }
            R.id.tv_authentication_my -> {
                when (realNameAuthenticationBean?.mereallyStatus) {
                    100 -> {
                        if (realNameAuthenticationBean?.membName.isNullOrEmpty()) {
                            activity!!.startActivity(Intent(activity, PrimaryAuthenticationActivity::class.java)
                                    .putExtra(AuthenticationHomeActivity.INTENT_REAL_NAME_BEAN, realNameAuthenticationBean))
                        } else if (realNameAuthenticationBean?.mereallyStep1Fileurl.isNullOrEmpty()) {
                            activity!!.startActivity(Intent(activity, RealNameAuthenticationActivity::class.java)
                                    .putExtra(AuthenticationHomeActivity.INTENT_REAL_NAME_BEAN, realNameAuthenticationBean))
                        }
                    }
                    101, 990 -> {
                        val intent = Intent(activity, AuthenticationHomeActivity::class.java)
                        intent.putExtra(AuthenticationHomeActivity.INTENT_REAL_NAME_BEAN, realNameAuthenticationBean)
                        startActivity(intent)
                    }
                    1011 -> {
                        activity!!.startActivity(Intent(activity, AuthenticationFailActivity::class.java)
                                .putExtra(AuthenticationHomeActivity.INTENT_REAL_NAME_BEAN, realNameAuthenticationBean))
                    }
                }
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
            userNameTV.text = getString(R.string.login)
        }
    }

    fun getReanNameAuthentication() {
        RealNameAuthenticationBean.getReanNameAuthentication(userBean?.tokenReqVo?.tokenUserId!!, userBean?.tokenReqVo?.tokenUserKey!!, userBean?.tokenReqVo?.tokenSystreeId!!)
                .compose(netTfWithDialog())
                .subscribe({
                    realNameAuthenticationBean = it.result

                }, onError)
    }
}
