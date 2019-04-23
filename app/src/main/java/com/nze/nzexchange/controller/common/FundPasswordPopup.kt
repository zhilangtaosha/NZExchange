package com.nze.nzexchange.controller.common

import android.app.Activity
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.EditText
import android.widget.TextView
import com.nze.nzeframework.widget.basepopup.BasePopupWindow
import com.nze.nzexchange.R
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
class FundPasswordPopup(context: Activity) : BasePopupWindow(context) {
    val cancelTv: TextView by lazy { findViewById(R.id.tv_cancel_pfp) as TextView }
    val passwordEt: EditText by lazy { findViewById(R.id.et_password_pfp) as EditText }
    val confirmBtn: CommonButton by lazy { findViewById(R.id.btn_confirm_pfp) as CommonButton }

    var onPasswordClick: ((pwd: String) -> Unit)? = null

    init {
        cancelTv.setOnClickListener {
            dismiss()
        }
        confirmBtn.initValidator()
                .add(passwordEt, EmptyValidation())
                .executeValidator()
        confirmBtn.setOnCommonClick {
            val pwd = MD5Tool.getMd5_32(passwordEt.getContent())
            onPasswordClick?.invoke(pwd)
            dismiss()
        }
    }

    override fun initShowAnimation(): Animation = TranslateAnimation(0f, 0f, dp2px(350F, context).toFloat(), 0f).apply {
        duration = 200
    }

    override fun initExitAnimation(): Animation = TranslateAnimation(0f, 0f, 0f, dp2px(350f, context).toFloat()).apply {
        duration = 200
    }

    override fun getClickToDismissView(): View = popupWindowView

    override fun onCreatePopupView(): View = createPopupById(R.layout.popup_fund_password)

    override fun initAnimaView(): View = findViewById(R.id.layout_root_pfp)
}