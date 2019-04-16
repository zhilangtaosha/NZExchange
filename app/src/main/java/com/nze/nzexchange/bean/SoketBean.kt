package com.nze.nzexchange.bean

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:  K线页面Socket实体
 * @创建时间：2019/1/23
 */
data class Soketbean(
        val lineK: LineKBean?,
        val handicap: Handicap?,
        val latestDeal: List<NewDealBean>?,
        val quotes: Array<String>?,
        val depth:Depth?
) {

    companion object {

    }
}

/**
 *K线数据
 */
data class LineKBean(
        val result: Array<Array<String>>?,
        val type: String
)

/**
 * k线盘口
 */
data class Handicap(
        val asks: Array<Array<String>>?,//买入
        val bids: Array<Array<String>>?//卖出
) {

}

/**
 * k线实时成交
 */
data class NewDealBean(
        val amount: String,
        val id: String,
        val price: String,
        val time: Double,
        val type: Int//0购买，1出售
)

data class Depth(
        val bids: Array<Array<Double>>,
        val asks: Array<Array<Double>>
)