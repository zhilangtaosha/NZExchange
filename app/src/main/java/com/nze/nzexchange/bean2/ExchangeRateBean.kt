package com.nze.nzexchange.bean2

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/12/29
 */
class ExchangeRateBean(
        val date: String,
        val rate: String,
        val average: String
) {
    companion object {
        fun getList() = mutableListOf<ExchangeRateBean>().apply {
            (1..10).forEach {
                add(ExchangeRateBean("2018.12.13","6.8791","0"))
            }
        }
    }
}