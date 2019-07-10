package com.nze.nzexchange.bean

import android.os.Parcelable
import com.nze.nzexchange.extend.retain2
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/6/12
 */

data class SoketQueryBean(//查询返回
        val error: Any?,
        val result: Any?,
        val id: Int?
)

data class SoketSubscribeBean(//订阅返回
        val method: String,
        val params: Any,
        val id: Int?
)

data class SoketTodayBean(//今日行情
        val deal: Double,
        val high: Double,
        val last: Double,
        val low: Double,
        val open: Double,
        val volume: Double
)

data class SoketDepthBean(//深度
        val asks: Array<Array<Double>>?,
        val bids: Array<Array<Double>>?
)


data class SoketDealBean(//最近成交
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
        val cny: Double,
        val stock_prec: Int,//交易币小数位数
        val money_prec: Int,// 计价币小数位数
        val min_amount: Double,//最小交易数量
        var optional: Int = 0
) {
    var change: Double = 0.0
        get() {
            return field.retain2().toDouble()
        }

    var market: String = ""
        get() {
            return field.replace("-", "/")
        }

    fun getCurrency(): String {
        return market.split("/")[0]
    }

    fun getMainCurrency(): String {
        return market.split("/")[1]
    }
}

data class SoketMarketBean(//交易对
        val money: String,
        val list: List<SoketRankBean>
)


data class SoketOrderBean(//订单：当前委托订单、全部委托订单
        val amount: Double,//挂单总数量
        val ctime: Long,//订单创建时间
        val mtime: Long,//订单更新时间，也就是最新成交时间
        val deal_fee: Double,//成交手续费
        val deal_money: Double,//已成交总价=deal_stock*price
        val deal_stock: Double,//已成交数量
        val ftime: Int,//
        val id: Long,//订单id
        val maker_fee: String,//maker费率
        val taker_fee: String,//taker费率
        val market: String,//交易对
        val price: Double,//挂单价格
        val side: Int,//1表示出售  2 表示购买
        val source: String,
        val type: Int,//1表示限价单   2. 市价订单
        val user: Int,
        val left: Double//剩余数量
)

data class SoketOrderResultBean(//订单返回结果
        val limit: Int,
        val offset: Int,
        val total: Int,
        val records: List<SoketOrderBean>
)

data class SoketSubscribeOrderBean(//订单订阅返回
        val event: Int,//1表示挂单，2 表示更新，3表示订单完全成交和取消
        val order: SoketOrderBean
)