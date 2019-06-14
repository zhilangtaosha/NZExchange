package com.nze.nzexchange.bean

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/6/12
 */

data class SoketQueryBean(
        val error: Any?,
        val result: Any?,
        val id: Int?
)

data class SoketSubscribeBean(
        val method: String,
        val params: Array<Any>,
        val id: Int?
)

data class SoketTodayBean(
        val deal: Double,
        val high: Double,
        val last: Double,
        val low: Double,
        val open: Double,
        val volume: Double
)

data class SoketDepthBean(
        val asks: Array<Array<Double>>?,
        val bids: Array<Array<Double>>?
)

data class SoketDealBean(
        val amount: Double,
        val id: Int,
        val price: Double,
        val time: Double,
        val type: String
)