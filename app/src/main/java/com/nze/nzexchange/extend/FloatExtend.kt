package com.nze.nzexchange.extend

import java.math.RoundingMode
import java.text.DecimalFormat

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/5/24
 */


/**
 * 保留12位小数
 * 不四舍五入
 * 直接舍弃O
 */
fun Float.formatForPrice(): String {
    val df = DecimalFormat("0.############")
    df.setRoundingMode(RoundingMode.FLOOR);
    return df.format(this)
}


fun Float.formatForCurrency(): String {
//    var b: BigDecimal = BigDecimal(this)
//    return b.setScale(10,BigDecimal.ROUND_HALF_DOWN).toString()
    val df = DecimalFormat("0.########")
    df.roundingMode = RoundingMode.FLOOR
    return df.format(this.toBigDecimal())
}