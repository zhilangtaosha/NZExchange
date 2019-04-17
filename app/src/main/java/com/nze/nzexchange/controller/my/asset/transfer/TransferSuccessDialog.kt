package com.nze.nzexchange.controller.my.asset.transfer

import android.app.AlertDialog
import android.content.Context

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/4/17
 */
class TransferSuccessDialog(context: Context) {
    lateinit var dialog: AlertDialog

    init {
        dialog = AlertDialog.Builder(context).create()
        val window = dialog.window
        
    }
}