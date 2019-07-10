package com.nze.nzexchange.config

import com.nze.nzexchange.BuildConfig

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明: 关联类
 * SoketRequestBean
 * WebSoketImpl
 * @创建时间：2019/3/26
 */
class KLineParam {

    companion object {
        fun getMarketMyself(): String {
            return if (BuildConfig.DEBUG) {
                MARKET_MYSELF_IN
            } else {
                MARKET_MYSELF_OUT
            }
        }

        const val MARKET_MYSELF_IN = "ws://192.168.1.107:443"
        const val MARKET_MYSELF_OUT = "ws://zhongyingying.qicp.io:1443"
        const val MARKET_MYSELF2 = "ws://3.15.16.7:443"
        const val MARKET_HUOBI = "ws://3.16.25.221:443"


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
        const val METHOD_QUERY_RANK = "price.ranking"//查询涨幅榜
        const val METHOD_MARKET_RANK = "market.ranking"//查询行情列表
        const val METHOD_AUTH = "server.auth"//身份验证
        const val METHOD_QUERY_ORDER = "order.query"//查询当前订单
        const val METHOD_SUBSCRIBE_ORDER = "order.subscribe"//订单订阅/通知
        const val METHOD_LIMIT_DEAL = "order.limit"//挂限价单
        const val METHOD_MARKET_DEAL = "order.market"//挂限价单

        //副图类型
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
        const val DATA_RANK_QUERY = 5//涨幅榜
        const val DATA_MARKET_QUERY = 6//查询行情
        const val DATA_AUTH = 7//身份认证
        const val DATA_CURRENT_ORDER_QUERY = 8//查询当前订单
        const val DATA_ORDER_SUBSCRIBE = 9//订阅订单
        const val DATA_LIMIT_DEAL = 10//挂限价单
        const val DATA_MARKET_DEAL = 11//挂市价单

        //最近成交列表类型
        const val DEALS_SELL = "sell"
        const val DEALS_BUY = "buy"

        //k线类型
        const val KLINE_TYPE_ONE_MIN = 60
        const val KLINE_TYPE_FIVE_MIN = 300
        const val KLINE_TYPE_FIFTEEN_MIN = 900
        const val KLINE_TYPE_THIRTY_MIN = 1800
        const val KLINE_TYPE_ONE_HOUR = 3600
        const val KLINE_TYPE_FOUR_HOUR = 14400
        const val KLINE_TYPE_ONE_DAY = 86400
        const val KLINE_TYPE_ONE_WEEK = 604800
        const val KLINE_TYPE_ONE_MON = 2592000

        //深度
        const val DEPTH_1 = "0.1"
        const val DEPTH_2 = "0.01"
        const val DEPTH_3 = "0.001"
        const val DEPTH_4 = "0.0001"
        const val DEPTH_5 = "0.00001"
        const val DEPTH_6 = "0.000001"
        const val DEPTH_7 = "0.0000001"
        const val DEPTH_8 = "0.00000001"

        //查询、订阅k线数据返回数量
        const val AMOUNT_KLINE = 100
        //查询、订阅深度数据返回数量
        const val AMOUNT_DEPTH_10 = 10
        const val AMOUNT_DEPTH_5 = 5
        const val AMOUNT_DEPTH_100 = 100


        var socketId: Int = 0
        val ID_KLINE: Int = 100001//K线查询
        val ID_RANK: Int = 100002//涨幅榜
        val ID_MARKET: Int = 100003//行情
        val ID_AUTH: Int = 100004//身份认证
        val ID_ORDER: Int = 100005//当前订单
        val ID_LIMIT_DEAL: Int = 100006//挂限价单
        val ID_MARKET_DEAL: Int = 100007//挂限价单


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