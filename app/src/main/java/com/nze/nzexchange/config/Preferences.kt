package com.nze.nzexchange.config

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/3/22
 */
class Preferences {
    companion object {
        val COME_BACK_PWD = "comeBackPwd"
        val BACK_PWD_GESTURE = 0//手势解锁
        val BACK_PWD_FINGERPRINT = 1//指纹解锁
        val BACK_PWD_NO = -1//不需要解锁

        val LOGIN_USER_NAME="userName"
    }

}