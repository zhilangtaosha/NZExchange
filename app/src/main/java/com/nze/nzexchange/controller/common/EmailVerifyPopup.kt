package com.nze.nzexchange.controller.common

import android.app.Activity
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.EditText
import android.widget.TextView
import com.nze.nzeframework.widget.basepopup.BasePopupWindow
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.extend.getContent
import com.nze.nzexchange.tools.MD5Tool
import com.nze.nzexchange.tools.dp2px
import com.nze.nzexchange.validation.EmptyValidation
import com.nze.nzexchange.widget.CommonButton

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/4/23
 */
class EmailVerifyPopup(context: Activity) : BasePopupWindow(context) {
    val cancelTv: TextView by lazy { findViewById(R.id.tv_cancel_pev) as TextView }
    val emailTv: TextView by lazy { findViewById(R.id.tv_email_pev) as TextView }
    val verifydEt: EditText by lazy { findViewById(R.id.et_password_pev) as EditText }

    val sendTv: TextView by lazy { findViewById(R.id.tv_send_pev) as TextView }
    val confirmBtn: CommonButton by lazy { findViewById(R.id.btn_confirm_pev) as CommonButton }

    var onConfirmClick: ((code: String) -> Unit)? = null
    var userBean = UserBean.loadFromApp()

    init {
        cancelTv.setOnClickListener {
            dismiss()
        }
        confirmBtn.initValidator()
                .add(verifydEt, EmptyValidation())
                .executeValidator()
        confirmBtn.setOnCommonClick {
            onConfirmClick?.invoke(verifydEt.getContent())
            dismiss()
        }
        emailTv.text = userBean?.userEmail


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