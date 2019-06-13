package com.nze.nzexchange.controller.my.asset.transfer

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
 * @类 说 明:
 * @创建时间：2019/4/17
 */
class TransferSuccessDialog(context: Context) {
    lateinit var dialog: AlertDialog
    lateinit var contentTv: TextView
    lateinit var toTv: TextView
    lateinit var cancelTv: TextView
    var type: Int = AccountType.BIBI
    val context = context

    fun show(type: Int) {
        dialog = AlertDialog.Builder(context).create()
        dialog.show()
        val window = dialog.window
        window.setContentView(R.layout.dialog_transfer)
        contentTv = window.findViewById(R.id.tv_content_dt)
        toTv = window.findViewById(R.id.tv_to_dt)
        when (type) {
            AccountType.OTC -> {
                contentTv.text = "资金已成功划转至OTC账户，马上进行OTC交易"
                toTv.text = "OTC交易"
            }
            AccountType.BIBI -> {
                contentTv.text = "资金已成功划转至币币账户，马上进行币币交易"
                toTv.text = "币币交易"
            }
            AccountType.LEGAL -> {
                contentTv.text = "资金已成功划转至币币账户，马上进行法币交易"
                toTv.text = "法币交易"
            }
        }

        cancelTv = window.findViewById(R.id.tv_cancel_dt)
        cancelTv.setOnClickListener {
            dialog.dismiss()
        }
        toTv.setOnClickListener {
            if (type == AccountType.BIBI) {
                EventBus.getDefault().post(EventCenter<Int>(EventCode.CODE_REFRESH_MAIN_ACT, 3))
            } else {
                EventBus.getDefault().post(EventCenter<Int>(EventCode.CODE_REFRESH_MAIN_ACT, 2))
                EventBus.getDefault().post(EventCenter<Int>(EventCode.CODE_TRADE_BIBI, 1))
            }
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }

    }

}