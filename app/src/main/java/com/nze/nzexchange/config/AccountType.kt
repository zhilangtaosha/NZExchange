package com.nze.nzexchange.config

/**
 * 账户类型
 */
class AccountType {
    companion object {
        const val BIBI: Int = 0//币币账户
        const val WITHDRAW: Int = 1//提现账户
        const val OTC: Int = 3//OTC账户
        const val LEGAL: Int = 2//法币账户

        fun getAccountName(type: Int): String = when (type) {
            BIBI -> "币币账户"
            WITHDRAW -> "提现账户"
            OTC -> "OTC账户"
            else -> ""
        }
    }
}