package com.nze.nzexchange.config

/**
 * 账户类型
 */
class AccountType {
    companion object {
        const val BIBI: Int = 0//币币账户
        const val LEGAL: Int = 1//法币账户
        const val OTC: Int = 2//OTC账户
        const val WITHDRAW: Int = 3//提现账户

        fun getAccountName(type: Int): String = when (type) {
            BIBI -> "币币账户"
            WITHDRAW -> "提现账户"
            OTC -> "OTC账户"
            else -> ""
        }
    }
}