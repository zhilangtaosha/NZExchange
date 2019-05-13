package com.nze.nzexchange.tools

import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import com.nze.nzexchange.NzeApp
import com.nze.nzexchange.R
import com.nze.nzexchange.extend.setDrawables

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

        fun createPayMehod(layout: Int): TextView {
            val tv: TextView = LayoutInflater.from(NzeApp.instance).inflate(layout, null) as TextView
            return tv
        }

        fun createAuthorityTv(content: String): TextView {
            val tv: TextView = LayoutInflater.from(NzeApp.instance).inflate(R.layout.tv_authority, null) as TextView
            tv.text = content
            return tv
        }

    }
}