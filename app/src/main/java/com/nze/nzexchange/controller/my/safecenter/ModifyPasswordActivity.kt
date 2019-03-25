package com.nze.nzexchange.controller.my.safecenter

import android.view.View
import android.widget.EditText
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.extend.getContent
import com.nze.nzexchange.http.CRetrofit
import com.nze.nzexchange.tools.MD5Tool
import com.nze.nzexchange.validation.EmptyValidation
import com.nze.nzexchange.widget.CommonButton
import kotlinx.android.synthetic.main.activity_modify_password.*

class ModifyPasswordActivity : NBaseActivity() {
    val oldPwdEt: EditText by lazy { et_old_password_amp }
    val newPwdEt: EditText by lazy { et_new_password_amp }
    val newPwdConfirmEt: EditText by lazy { et_new_password_confirm_amp }
    val confirmBtn: CommonButton by lazy { btn_confirm_amp }
    var userBean = UserBean.loadFromApp()

    override fun getRootView(): Int = R.layout.activity_modify_password

    override fun initView() {
        confirmBtn.initValidator()
                .add(oldPwdEt, EmptyValidation())
                .add(newPwdEt, EmptyValidation())
                .add(newPwdConfirmEt, EmptyValidation())
                .executeValidator()
                .setOnCommonClick {
                    setPwd()
                }
    }

    fun setPwd() {
        if (confirmBtn.validate()) {
            var newPwd: String = newPwdEt.getContent()
            val newPwdConfirm: String = newPwdConfirmEt.getContent()
            if (newPwd != newPwdConfirm) {
                showToast("两次密码输入不一致")
                return
            }
            var oldPwd = MD5Tool.getMd5_32(oldPwdEt.getContent())
            newPwd = MD5Tool.getMd5_32(newPwd)
            userBean?.tokenReqVo?.let {
                CRetrofit.instance
                        .userService()
                        .modifyPassword(it.tokenUserId, it.tokenUserKey, it.tokenSystreeId, oldPwd, newPwd)
                        .compose(netTfWithDialog())
                        .subscribe({
                            showToast(it.message)
                        }, onError)
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
