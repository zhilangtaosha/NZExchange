package com.nze.nzexchange.config

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/3/26
 */
class KLineParam {

    companion object {
        const val MARKET_MYSELF = "ws://192.168.1.130:443/ws"
        const val MARKET_HUOBI = "ws://18.224.228.208:443/huobi"


        const val TIME_ONE_MIN = "1min"
        const val TIME_FIVE_MIN = "5min"
        const val TIME_FIFTEEN_MIN = "15min"
        const val TIME_THIRTY_MIN = "30min"
        const val TIME_ONE_HOUR = "60min"
        const val TIME_ONE_DAY = "1day"
        const val TIME_ONE_WEEK = "1week"
        const val TIME_ONE_MON = "1mon"

        const val METHOD_GET_K = "openKLine"//获取k线初始数据
        const val METHOD_CHANGE_PAIR = "updateMarket"//切换交易对
        const val METHOD_CHANGE_TIME = "updateInterval"//切换k线时间间隔
        const val METHOD_HISTORY = "historicalData"
        const val METHOD_QUERYDATASOURCE = "queryDataSource"//获取平台数据

    }
}

class KLineRequestBean(
        var method: String,
        var params: MutableList<String>
) {

}