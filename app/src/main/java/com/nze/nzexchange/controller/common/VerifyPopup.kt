package com.nze.nzexchange.controller.common

import android.app.Activity
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.nze.nzeframework.widget.basepopup.BasePopupWindow
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.bean.VerifyBean
import com.nze.nzexchange.extend.getContent
import com.nze.nzexchange.tools.MD5Tool
import com.nze.nzexchange.tools.RegularTool
import com.nze.nzexchange.tools.dp2px
import com.nze.nzexchange.validation.EmptyValidation
import com.nze.nzexchange.widget.CommonButton
import com.nze.nzexchange.widget.VerifyButton
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/4/23
 */
class VerifyPopup(context: Activity) : BasePopupWindow(context) {
    val cancelTv: TextView by lazy { findViewById(R.id.tv_cancel_ppv) as TextView }
    val titleTv: TextView by lazy { findViewById(R.id.tv_title_ppv) as TextView }
    val verifydEt: EditText by lazy { findViewById(R.id.et_verify_ppv) as EditText }
    val sendBtn: VerifyButton by lazy { findViewById(R.id.tv_send_ppv) as VerifyButton }
    val confirmBtn: CommonButton by lazy { findViewById(R.id.btn_confirm_ppv) as CommonButton }

    var onConfirmClick: ((code: String, checkcodeId: String) -> Unit)? = null
    var userBean = UserBean.loadFromApp()
    var account: String? = null
        set(value) {
            field = value
            if (RegularTool.isEmail(value!!)) {
                titleTv.text = "邮箱验证"
                verifydEt.hint = "请输入邮箱验证码"
            } else {
                titleTv.text = "$value"
                verifydEt.hint = "请输入手机验证码"
            }
        }
    var checkcodeId: String? = null


    init {
        cancelTv.setOnClickListener {
            dismiss()
        }
        confirmBtn.initValidator()
                .add(verifydEt, EmptyValidation())
                .executeValidator()
        confirmBtn.setOnCommonClick {
            if (checkcodeId.isNullOrEmpty()) {
                Toast.makeText(context, "请获取验证码", Toast.LENGTH_SHORT).show()
                return@setOnCommonClick
            }
            onConfirmClick?.invoke(verifydEt.getContent(), checkcodeId!!)
            dismiss()
        }

        sendBtn.setOnClickListener {
            VerifyBean.getVerifyCodeNet(account!!, VerifyBean.TYPE_COMMON)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        if (it.success) {
                            sendBtn.startVerify()
                            checkcodeId = it.result.checkcodeId
                            Toast.makeText(context, "验证码已经发送到${account}", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "验证码已经发送失败", Toast.LENGTH_SHORT).show()
                        }
                    }, {})
        }

    }

    override fun dismiss() {
        super.dismiss()
        sendBtn.stopVerify()
    }

    override fun initShowAnimation(): Animation = TranslateAnimation(0f, 0f, dp2px(350F, context).toFloat(), 0f).apply {
        duration = 200
    }

    override fun initExitAnimation(): Animation = TranslateAnimation(0f, 0f, 0f, dp2px(350f, context).toFloat()).apply {
        duration = 200
    }

    override fun getClickToDismissView(): View = popupWindowView

    override fun onCreatePopupView(): View = createPopupById(R.layout.popup_phone_verify)

    override fun initAnimaView(): View = findViewById(R.id.layout_root_pfp)
}