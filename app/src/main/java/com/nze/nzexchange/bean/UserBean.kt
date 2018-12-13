package com.nze.nzexchange.bean

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/12/12
 */
data class UserBean(
        val userEmail: String?,
        val userId: String?,
        val userName: String?,
        val userNick: String?,
        val userNo: String?,
        val userPhone: String?,
        val userShow: String?,
        val userTag: String?,
        val tokenReqVo: TokenReqVo?
) {


}