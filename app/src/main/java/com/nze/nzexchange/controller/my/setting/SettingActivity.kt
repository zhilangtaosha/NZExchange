package com.nze.nzexchange.controller.my.setting

import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.ui.BaseActivity
import com.nze.nzexchange.NzeApp
import com.nze.nzexchange.R
import com.nze.nzexchange.R.id.*
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.config.EventCode
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.login.LoginActivity
import com.nze.nzexchange.controller.main.MainActivity
import com.nze.nzexchange.http.CRetrofit
import com.nze.nzexchange.widget.CommonButton
import com.nze.nzexchange.widget.CommonTopBar
import kotlinx.android.synthetic.main.activity_setting.*
import org.greenrobot.eventbus.EventBus

class SettingActivity : NBaseActivity(), View.OnClickListener {


    val topBar: CommonTopBar by lazy { ctb_set }
    val moneyTypeLayout: RelativeLayout by lazy { layout_money_type_set }
    val moneyTypeTv: TextView by lazy { tv_money_type_set }
    val exchangeRateTv: TextView by lazy { tv_exchange_rate_set }
    val serviceChargeTv: TextView by lazy { tv_service_charge_set }
    val aboutUsTv: TextView by lazy { tv_about_us_set }
    val nightModeTv: TextView by lazy { tv_night_mode_set }
    val colorModeLayout: RelativeLayout by lazy { layout_color_mode_set }
    val colorModeIv: ImageView by lazy { iv_color_mode_set }
    val styleModeLayout: RelativeLayout by lazy { layout_style_mode_set }
    val styleModeIv: ImageView by lazy { iv_style_mode_set }
    val editionLayout: RelativeLayout by lazy { layout_edition_set }
    val editionTv: TextView by lazy { tv_edition_set }
    val logoutBtn: CommonButton by lazy {
        btn_logout_set.apply {
            text = if (userBean != null) "退出登录" else "登录"
        }
    }
    var userBean: UserBean? = NzeApp.instance.userBean

    override fun getRootView(): Int = R.layout.activity_setting

    override fun initView() {
        moneyTypeLayout.setOnClickListener(this)
        exchangeRateTv.setOnClickListener(this)
        serviceChargeTv.setOnClickListener(this)
        aboutUsTv.setOnClickListener(this)
        nightModeTv.setOnClickListener(this)
        colorModeLayout.setOnClickListener(this)
        styleModeLayout.setOnClickListener(this)
        editionLayout.setOnClickListener(this)
        logoutBtn.setOnCommonClick(this)


    }

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {
        if (eventCenter.eventCode == EventCode.CODE_LOGIN_SUCCUSS) {
            userBean = NzeApp.instance.userBean
            logoutBtn.text = "退出登录"
        }
    }

    override fun getOverridePendingTransitionMode(): BaseActivity.TransitionMode = BaseActivity.TransitionMode.DEFAULT

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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.layout_money_type_set -> {
                skipActivity(MoneyTypeSetActivity::class.java)
            }
            R.id.tv_exchange_rate_set -> {
                skipActivity(ExchangeRateSetActivity::class.java)
            }
            R.id.tv_service_charge_set -> {
            }
            R.id.tv_about_us_set -> {
            }
            R.id.tv_night_mode_set -> {
            }
            R.id.layout_color_mode_set -> {
                skipActivity(ColorModeSetActivity::class.java)
            }
            R.id.layout_style_mode_set -> {
            }
            R.id.layout_edition_set -> {
            }
            R.id.btn_logout_set -> {
                if (userBean != null) {
                    val tokenReqVo = userBean?.tokenReqVo
                    tokenReqVo?.run {
                        CRetrofit.instance
                                .userService()
                                .logout(tokenUserId, tokenUserKey, tokenSystreeId)
                                .compose(netTfWithDialog())
                                .subscribe({
                                    if (it.success) {
                                        NzeApp.instance.userBean = null
                                        userBean = null
//                                        logoutBtn.text = "登录"
//                                        EventBus.getDefault().post(EventCenter<Boolean>(EventCode.CODE_LOGOUT_SUCCESS, true))
                                        EventBus.getDefault().post(EventCenter<Int>(EventCode.CODE_REFRESH_MAIN_ACT, 0))
                                        skipActivity(MainActivity::class.java)
                                    }
                                }, onError)
                    }

                } else {
                    skipActivity(LoginActivity::class.java)
                }
            }
        }
    }
}
