package com.nze.nzexchange.config

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/3/26
 */
class KLineParam {

    companion object {
        const val MARKET_MYSELF = "kline/exchange"
        const val MARKET_HUOBI = "huobikline/exchange"


        const val TIME_ONE_MIN = "1min"
        const val TIME_FIVE_MIN = "5min"
        const val TIME_FIFTEEN_MIN = "15min"
        const val TIME_THIRTY_MIN = "30min"
        const val TIME_ONE_HOUR = "60min"
        const val TIME_ONE_DAY = "1day"
        const val TIME_ONE_WEEK = "1week"
        const val TIME_ONE_MON = "1mon"

        const val METHOD_GET_K="openKLine"
        const val METHOD_CHANGE_PAIR="updateMarket"
        const val METHOD_CHANGE_TIME="updateInterval"
        const val METHOD_HISTORY="historicalData"

    }
}

class KLineRequestBean(
        var method: String,
        var params: MutableList<String>
) {

}