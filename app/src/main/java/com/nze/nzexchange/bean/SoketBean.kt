package com.nze.nzexchange.bean

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/1/23
 */
data class Soketbean(
        val lineK: LineKBean?,
        val handicap: Handicap?
) {

    companion object {

    }
}

/**
 *
 */
data class LineKBean(
        val result: Array<Array<Long>>?,
        val type: String
)


data class Handicap(
        val asks: Array<Array<String>>?,//买入
        val bids: Array<Array<String>>?//卖出
) {

}