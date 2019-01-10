package com.nze.nzexchange.bean2

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/1/10
 */
data class ConcreteAssetBean(
        val amount: Double,
        val status: String,
        val date: String
) {
    companion object {
        fun getList() = mutableListOf<ConcreteAssetBean>().apply {
            for (i in 1..5) {
                add(ConcreteAssetBean(1.0000, "已完成", "10/21 12:21"))
            }
        }
    }
}