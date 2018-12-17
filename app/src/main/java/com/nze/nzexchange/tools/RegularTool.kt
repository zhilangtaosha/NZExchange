package com.nze.nzexchange.tools

import java.util.regex.Pattern

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:正则工具类
 * @创建时间：2018/12/17
 */
class RegularTool {
    companion object {
        val REGEX_EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$"
        val REGEX_MOBILE = "^((17[0-9])|(14[0-9])|(13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$"

        val ACCOUNT_TYPE_PHONE = 0
        val ACCOUNT_TYPE_EMAIL = 1
        /**
         * 校验手机号
         *
         * @param mobile
         * @return 校验通过返回true，否则返回false
         */
        fun isMobile(mobile: String): Boolean {
            return Pattern.matches(REGEX_MOBILE, mobile)
        }

        /**
         * 校验邮箱
         *
         * @param email
         * @return 校验通过返回true，否则返回false
         */
        fun isEmail(email: String): Boolean {
            return Pattern.matches(REGEX_EMAIL, email)
        }

        fun findAccountType(account: String): Int {
            return if (isEmail(account)) {
                ACCOUNT_TYPE_EMAIL
            } else {
                ACCOUNT_TYPE_PHONE
            }
        }
    }
}