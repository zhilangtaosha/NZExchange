package com.nze.nzexchange.extend

import java.math.BigDecimal
import java.text.DecimalFormat

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/12/10
 */
fun Double.formatForCurrency(): String? {
//    var b: BigDecimal = BigDecimal(this)
//    return b.setScale(10,BigDecimal.ROUND_HALF_DOWN).toString()
    val df = DecimalFormat("0.#############")
    return df.format(this)
}


fun String.getValue(): String {
    return this
}

fun Double.twoPlace():String{
    val df = DecimalFormat("0.00")
    return df.format(this)
}