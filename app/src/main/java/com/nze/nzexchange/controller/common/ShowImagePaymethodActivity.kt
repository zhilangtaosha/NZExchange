package com.nze.nzexchange.controller.common

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.nze.nzexchange.R

/**
 * 显示微信和支付等 图片支付方式
 */
class ShowImagePaymethodActivity : AppCompatActivity() {

    companion object {
        fun skip(context: Context) {
            context.startActivity(Intent(context, ShowImagePaymethodActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_image_paymethod)
    }
}
