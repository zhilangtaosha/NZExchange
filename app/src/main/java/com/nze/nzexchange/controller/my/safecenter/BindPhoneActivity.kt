package com.nze.nzexchange.controller.my.safecenter

import android.view.View
import android.widget.EditText
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.tool.NLog
import com.nze.nzexchange.NzeApp
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.bean.VerifyBean
import com.nze.nzexchange.config.EventCode
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.extend.getContent
import com.nze.nzexchange.http.CRetrofit
import com.nze.nzexchange.validation.EmptyValidation
import com.nze.nzexchange.widget.CommonButton
import com.nze.nzexchange.widget.VerifyButton
import kotlinx.android.synthetic.main.activity_bind_phone_activity.*
import org.greenrobot.eventbus.EventBus

class BindPhoneActivity : NBaseActivity(), View.OnClickListener {


    private val phoneEt: EditText by lazy { et_phone_aba }
    private val verifyEt: EditText by lazy { et_verify_aba }
    private val verifyBtn: VerifyButton by lazy { btn_verify_aba }
    private val confirmBtn: CommonButton by lazy { btn_confirm_aba }
    private var checkcodeId: String? = null
    private var userBean = UserBean.loadFromApp()

    override fun getRootView(): Int = R.layout.activity_bind_phone_activity

    override fun initView() {

        verifyBtn.setVerifyClick(this)
        confirmBtn.setOnCommonClick(this)
        confirmBtn.initValidator()
                .add(phoneEt, EmptyValidation())
                .add(verifyEt, EmptyValidation())
                .executeValidator()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_verify_aba -> {
                var phone = phoneEt.getContent()
                if (phone.isNullOrEmpty()) {
                    showToast("请输入手机号码")
                    return
                }
                VerifyBean.getVerifyCodeNet(phone, VerifyBean.TYPE_COMMON)
                        .compose(netTfWithDialog())
                        .subscribe({
                            if (it.success) {
                                verifyBtn.startVerify()
                                checkcodeId = it.result.checkcodeId
                                showToast("验证码已经发送到$phone")
                            }
                        }, onError)
            }
            R.id.btn_confirm_aba -> {
                if (checkcodeId.isNullOrEmpty()) {
                    showToast("请先获取验证码")
                    return
                }
                if (confirmBtn.validate()) {
                    CRetrofit.instance
                            .userService()
                            .bindPhone(userBean!!.tokenReqVo.tokenUserId, userBean!!.tokenReqVo.tokenUserKey, checkcodeId!!, verifyEt.getContent(), phoneEt.getContent())
                            .compose(netTfWithDialog())
                            .subscribe({
                                if (it.success) {
                                    NzeApp.instance.userBean?.userPhone = phoneEt.getContent()
                                    EventBus.getDefault().post(EventCenter<String>(EventCode.CODE_REFRESH_USERBEAN))
                                    this.finish()
                                } else {
                                    showToast(it.message)
                                }
                            }, onError)
                }
            }
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

    override fun getContainerTargetView(): View? = null

}
