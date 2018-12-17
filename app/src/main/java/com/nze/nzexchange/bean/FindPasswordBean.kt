package com.nze.nzexchange.bean

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/12/17
 */
data class FindPasswordBean(
        val treeauthId: String?,
        var userCreateTime: Long = 0,
        val userCreateUser: String?,
        val userEmail: String?,
        val userId: String?,
        val userImage: String?,
        val userName: String?,
        val userNick: String?,
        val userNo: String?,
        var userOrd: Int = 0,
        val userPassword: String?,
        val userPhone: String?,
        var userStatus: Int = 0,
        var userUpdateTime: Long = 0,
        val userUpdateUser: String?
) {

}