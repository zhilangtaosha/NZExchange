package com.nze.nzexchange.bean2

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/12/25
 */
data class RechargeHistoryBean(
        val title: String = "",
        val month: String = "",
        val type: String = "USDT充值",
        val rechargeAmount: String = "+1334.21USDT",
        val totalAmount: String = "余额 12345.23USDT",
        val isTitle: Boolean = false
) {

    companion object {

        fun getList() = mutableListOf<RechargeHistoryBean>().apply {
            add(RechargeHistoryBean("本月", isTitle = true))
            add(RechargeHistoryBean(month = "12-21"))
            add(RechargeHistoryBean(month = "12-18"))
            add(RechargeHistoryBean(month = "12-02"))

            add(RechargeHistoryBean("2018-11", isTitle = true))
            add(RechargeHistoryBean(month = "11-27"))
            add(RechargeHistoryBean(month = "11-13"))
            add(RechargeHistoryBean(month = "11-06"))
        }
    }
}