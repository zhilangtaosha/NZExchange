package com.nze.nzexchange.config

import com.nze.nzexchange.config.KLineParam.Companion.socketId

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明: 关联类
 * SoketRequestBean
 * WebSoketP
 * @创建时间：2019/3/26
 */
class KLineParam {

    companion object {
        const val MARKET_MYSELF = "ws://192.168.1.107:443"
        const val MARKET_MYSELF2 = "ws://3.15.16.7:443"
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

        //张总新k线
        const val METHOD_QUERY_PRICE = "price.query"//查询价格
        const val METHOD_SUBSCRIBE_PRICE = "price.subscribe"//订阅价格
        const val METHOD_QUERY_STATE = "state.query"//查询市场行情
        const val METHOD_SUBSCRIBE_STATE = "state.subscribe"//订阅市场行情
        const val METHOD_QUERY_TODAY = "today.query"//查询今日行情
        const val METHOD_SUBSCRIBE_TODAY = "today.subscribe"//订阅今日行情
        const val METHOD_QUERY_DEALS = "deals.query"//查询最近成交列表
        const val METHOD_SUBSCRIBE_DEALS = "deals.subscribe"//订阅最近成交列表
        const val METHOD_QUERY_DEPTH = "depth.query"//查询深度
        const val METHOD_SUBSCRIBE_DEPTH = "depth.subscribe"//订阅深度
        const val METHOD_QUERY_KLINE = "kline.query"//查询K线
        const val METHOD_SUBSCRIBE_KLINE = "kline.subscribe"//订阅K线

        const val STATUS_MA = "MA"
        const val STATUS_BOLL = "BOLL"
        const val STATUS_MACD = "MACD"
        const val STATUS_KDJ = "KDJ"
        const val STATUS_RSI = "RSI"
        const val STATUS_WR = "WR"
        const val STATUS_MAIN_EMPTY = "mainEmpty"
        const val STATUS_SUB_EMPTY = "subEmpty"

        //订阅返回
        const val SUBSCRIBE_KLINE = "kline.update"//k线数据
        const val SUBSCRIBE_TODAY = "today.update"//今日行情
        const val SUBSCRIBE_DEALS = "deals.update"//最近成交列表
        const val SUBSCRIBE_DEPTH = "depth.update"//深度

        //返回数据类型
        const val DATA_KLINE_QUERY = 0
        const val DATA_KLINE_SUBSCRIBE = 1
        const val DATA_TODAY_SUBSCRIBE = 2
        const val DATA_DEALS_SUBSCRIBE = 3
        const val DATA_DEPTH_SUBSCRIBE = 4

        const val DEPTH_4 = "0.0001"
        const val DEPTH_8 = "0.00000001"


        var socketId: Int = 0
        val ID_KLINE: Int = 100001
    }
}


class KLineRequestBean(
        var method: String,
        var params: MutableList<String>
) {
    companion object {
        fun create(method: String): KLineRequestBean {
            return KLineRequestBean(method, mutableListOf<String>())
        }
    }
}