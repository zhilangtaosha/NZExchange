package com.nze.nzexchange.bean2

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/1/24
 */
data class ShenDubean(
        val amount: String,
        val price: String
) {

    companion object {
        fun getList() = mutableListOf<ShenDubean>().apply {
            for (i in 1..15) {
                add(ShenDubean("2313", "2312.26"))
            }
        }
    }
}