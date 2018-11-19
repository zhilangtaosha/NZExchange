package com.nze.nzexchange.controller.my.paymethod

import android.app.Activity
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.Button
import com.nze.nzeframework.widget.basepopup.BasePopupWindow
import com.nze.nzexchange.R
import com.nze.nzexchange.tools.dp2px

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/11/19
 */
class SelectPhotoPopup(context: Activity?) : BasePopupWindow(context), View.OnClickListener {
    var onListener: OnSelectPhotoListener? = null
    val takePictureBtn: Button = (findViewById(R.id.btn_take_picture_popup) as Button).apply {
        setOnClickListener(this@SelectPhotoPopup)
    }
    val albumBtn: Button = (findViewById(R.id.btn_album_popup) as Button).apply {
        setOnClickListener(this@SelectPhotoPopup)
    }

    val cancleBtn: Button = (findViewById(R.id.btn_cancel_popup) as Button).apply {
        setOnClickListener(this@SelectPhotoPopup)
    }


    override fun initShowAnimation(): Animation = TranslateAnimation(0f, 0f, dp2px(350F, context).toFloat(), 0f).apply {
        duration = 450
    }


    override fun initExitAnimation(): Animation = TranslateAnimation(0f, 0f, 0f, dp2px(350f, context).toFloat()).apply {
        duration = 450
    }

    override fun initAnimaView(): View = findViewById(R.id.llyt_anima)

    override fun getClickToDismissView(): View = popupWindowView

    override fun onCreatePopupView(): View = createPopupById(R.layout.popup_select_phone)

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_take_picture_popup -> {
                onListener?.onSelect(SelectType.TAKE_PICTURE)
            }
            R.id.btn_album_popup -> {
                onListener?.onSelect(SelectType.ALBUM)
            }
            R.id.btn_cancel_popup -> {
                onListener?.onSelect(SelectType.CANCEL)
            }
        }
        dismiss()
    }

    enum class SelectType {
        TAKE_PICTURE, ALBUM, CANCEL
    }

    interface OnSelectPhotoListener {
        fun onSelect(select: SelectType)
    }


}