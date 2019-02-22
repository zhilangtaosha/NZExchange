package com.nze.nzexchange.bean2

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/2/19
 */
data class KLineOrderBean(
        val amount: Int,
        val price: Double
) {
    companion object {
        fun getList() = mutableListOf<KLineOrderBean>().apply {
            for (i in 1..15) {
                add(KLineOrderBean(2313, 2312.12))
            }
        }
    }

}