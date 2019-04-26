package com.nze.nzexchange.controller.common

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.widget.TextView
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.config.AccountType
import com.nze.nzexchange.config.EventCode
import com.nze.nzexchange.controller.main.MainActivity
import org.greenrobot.eventbus.EventBus

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明: 完善交易资料Dialog
 * @创建时间：2019/4/26
 */
class PerfectInfoDialog(context: Context) {
    lateinit var dialog: AlertDialog
    lateinit var contentTv: TextView
    lateinit var toTv: TextView
    lateinit var cancelTv: TextView
    val context = context
    var content: String? = null
    var clickText: String? = null


    fun show(onClick: (() -> Unit)) {
        dialog = AlertDialog.Builder(context).create()
        dialog.show()
        val window = dialog.window
        window.setContentView(R.layout.dialog_transfer)
        contentTv = window.findViewById(R.id.tv_content_dt)
        toTv = window.findViewById(R.id.tv_to_dt)
        cancelTv = window.findViewById(R.id.tv_cancel_dt)

        contentTv.text = content
        toTv.text = clickText

        cancelTv.setOnClickListener {
            dialog.dismiss()
        }
        toTv.setOnClickListener {
            onClick.invoke()
        }

    }

}