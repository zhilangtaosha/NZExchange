package com.nze.nzexchange.extend

import android.icu.lang.UCharacter.GraphemeClusterBreak.L
import android.net.http.SslCertificate
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat

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

/**
 * 保留2位小数
 */
fun Double.retain2(): String {
    val df = DecimalFormat("0.##")
    return df.format(this)
}

/**
 * 保留4位小数
 * 不四舍五入
 */
fun Double.retain4(): String {
    val df = DecimalFormat("0.####")
    df.setRoundingMode(RoundingMode.FLOOR);
    return df.format(this)
}

/**
 * 取消科学计数法
 */
fun Double.removeE(): String {
    val nf = NumberFormat.getInstance()
    nf.isGroupingUsed = false
    return nf.format(this)
}

fun String.getValue(): String {
    return this
}

/**
 * 保留两位小数，没有用0替代
 */
fun Double.twoPlace(): String {
    val df = DecimalFormat("0.00")
    return df.format(this)
}