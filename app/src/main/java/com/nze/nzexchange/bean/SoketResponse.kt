package com.nze.nzexchange.bean

import com.nze.nzexchange.extend.retain2
import java.math.BigDecimal

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
        val params: Any,
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
        val id: String,
        val price: Double,
        val time: Double,
        val type: String
)

data class SoketRankBean(//首页涨幅榜
        val deal: Double,
        val high: Double,
        val last: Double,
        val low: Double,

        val open: Double,
        val volume: Double,
        val cny: Double
) {
    var change: Double = 0.0
        get() {
            return field.retain2().toDouble()
        }

    var market: String = ""
        get() {
            return field.replace("-", "/")
        }
}