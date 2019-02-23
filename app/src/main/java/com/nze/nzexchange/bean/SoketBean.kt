package com.nze.nzexchange.bean

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/1/23
 */
data class Soketbean(
        val kLineResult: KLineBean,
        val handicap: Handicap
) {

}

data class KLineBean(
        val start: String,
        val end: String,
        val result: Array<Array<Long>>
) {


}

data class Handicap(
        val asks: Array<Array<Long>>,
        val bids: Array<Array<Long>>
) {

}