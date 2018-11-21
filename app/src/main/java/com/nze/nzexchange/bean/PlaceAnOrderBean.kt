package com.nze.nzexchange.bean

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/11/21
 */
data class PlaceAnOrderBean(
    val accmoney: Accmoney,
    val poolId: String,
    val suborderAmount: Int,
    val suborderId: String,
    val suborderPrice: Double,
    val userIdBu: String,
    val userIdSell: String
)

