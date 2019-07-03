package com.nze.nzexchange.controller.common

import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.ErrorBean
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.config.EventCode
import com.nze.nzexchange.tools.ViewFactory
import org.greenrobot.eventbus.EventBus

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/5/13
 */
class AuthorityDialog(context: Context) {
    lateinit var dialog: AlertDialog
    lateinit var nameTv: TextView
    lateinit var confirmTv: TextView
    lateinit var contentLayout: LinearLayout
    val context = context
    var content: String? = null
    var clickText: String? = null

    companion object {
        fun getInstance(context: Context): AuthorityDialog {
            return AuthorityDialog(context)
        }
    }

    fun isShow(): Boolean {
        return dialog.isShowing
    }

    fun show(name: String, error: List<ErrorBean>, onClick: (() -> Unit)) {
        dialog = AlertDialog.Builder(context).create()
        dialog.show()
        val window = dialog.window
        window.setContentView(R.layout.dialog_authority)
        nameTv = window.findViewById(R.id.tv_name_da)
        contentLayout = window.findViewById(R.id.layout_content_da)
        confirmTv = window.findViewById(R.id.tv_confirm_da)

        nameTv.visibility = View.VISIBLE
        nameTv.text = name
        var isExit = false
        error.forEachIndexed { index, errorBean ->
            var s = ""
            when (errorBean.errorCode) {
                "me_memb_username_empty" -> {//实名认证实名为空
                    s = "${index + 1}.完成实名认证"
                }
                "me_memb_really_fileAuditFail" -> {//实名认证资料未审核通过
                    s = "${index + 1}.实名认证通过审核"
                }
                "me_memb_accmoney_noset" -> {//会员收款方式未设置
                    s = "${index + 1}.设置收款方式"
                }
                "me_memb_buspw_nodata" -> {//未设置资金密码
                    s = "${index + 1}.设置资金密码"
                }
                "token_lost" -> {//登录身份失效
                    s = "${index + 1}.${errorBean.errorMsg}"
                    isExit = true
                }
            }
            val tv = ViewFactory.createAuthorityTv(s)
            tv.text = s
            contentLayout.addView(tv)
        }

        confirmTv.setOnClickListener {
            dialog.dismiss()
            onClick.invoke()
            if (isExit) {
                UserBean.logout()
                EventBus.getDefault().post(EventCenter<Int>(EventCode.CODE_REFRESH_MAIN_ACT, 0))
                EventBus.getDefault().post(EventCenter<String>(EventCode.CODE_LOGOUT_SUCCESS))
            }
        }


    }
}