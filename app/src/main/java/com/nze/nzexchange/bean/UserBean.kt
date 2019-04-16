package com.nze.nzexchange.bean

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import com.nze.nzexchange.NzeApp
import com.nze.nzexchange.controller.login.LoginActivity

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/12/12
 */
data class UserBean(
        var userEmail: String?,
        val userId: String,
        val userName: String?,
        val userNick: String?,
        val userNo: String?,
        var userPhone: String?,
        val userShow: String?,
        val userTag: String?,
        val tokenReqVo: TokenReqVo,
        var payMethod: SetPayMethodBean?
) {


    companion object {

        fun loadFromApp() = NzeApp.instance.userBean

        fun isLogin(): Boolean = loadFromApp() != null

        fun logout() {
            NzeApp.instance.userBean = null
        }

        fun isLogin(context: Context): Boolean {
            var userBean = loadFromApp()
            var isLogin = true
            if (userBean == null) {
                isLogin = false
                startActivity(context, Intent(context, LoginActivity::class.java), null)
            }
            return isLogin
        }
    }
}