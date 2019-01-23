package com.nze.nzexchange.bean

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/1/23
 */
data class KLineBean(
        val start: String,
        val end: String,
        val result: Array<Array<Long>>
) {


}