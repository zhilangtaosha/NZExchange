package com.nze.nzexchange.bean

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/12/4
 */
data class HandicapBean(var index: Int, var cost: Double, var amount: Double, var realAmount: Double) {

    companion object {
        fun getList() = mutableListOf<HandicapBean>().apply {
            for (i in 1..5) {
                add(HandicapBean(i, 3568.25, 2.65, 1.65))
            }
        }
    }
}