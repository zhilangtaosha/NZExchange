package com.nze.nzexchange.bean2

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/12/26
 */
data class WithdrawHistoryBean(
        val amount: Double,
        val status: String,
        val date: String
) {

    companion object {
        fun getList() = mutableListOf<WithdrawHistoryBean>().apply {
            (1..10).forEach {
                add(WithdrawHistoryBean(20.00000000, "已完成", "10/21 23:12"))
            }
        }
    }
}