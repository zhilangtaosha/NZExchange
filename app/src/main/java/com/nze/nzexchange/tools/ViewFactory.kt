package com.nze.nzexchange.tools

import android.view.LayoutInflater
import android.widget.ImageView
import com.nze.nzexchange.NzeApp
import com.nze.nzexchange.R

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/11/21
 */
class ViewFactory {

    companion object {
        fun createLeftPayMethod(iconId: Int): ImageView {
            val iv: ImageView = LayoutInflater.from(NzeApp.instance).inflate(R.layout.image_pay, null) as ImageView
            iv.setImageResource(iconId)
            iv.setPadding(0, 0, dp2px(5F), 0)
            return iv
        }

        fun createRightPayMethod(iconId: Int): ImageView {
            val iv: ImageView = LayoutInflater.from(NzeApp.instance).inflate(R.layout.image_pay, null) as ImageView
            iv.setImageResource(iconId)
            iv.setPadding(dp2px(5F), 0, 0, 0)
            return iv
        }

    }
}