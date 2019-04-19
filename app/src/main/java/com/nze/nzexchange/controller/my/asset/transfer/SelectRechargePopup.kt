package com.nze.nzexchange.controller.my.asset.transfer

import android.app.Activity
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import com.nze.nzeframework.widget.basepopup.BasePopupWindow
import com.nze.nzexchange.R
import com.nze.nzexchange.tools.dp2px

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/4/19
 */
class SelectRechargePopup(context: Activity?) : BasePopupWindow(context) {

    override fun initAnimaView(): View = findViewById(R.id.layout_dsr)


    override fun getClickToDismissView(): View = popupWindowView

    override fun onCreatePopupView(): View = createPopupById(R.layout.dialog_select_recharge)

    override fun initShowAnimation(): Animation = TranslateAnimation(0f, 0f, dp2px(350F, context).toFloat(), 0f).apply {
        duration = 200
    }

    override fun initExitAnimation(): Animation = TranslateAnimation(0f, 0f, 0f, dp2px(350f, context).toFloat()).apply {
        duration = 200
    }
}