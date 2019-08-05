package com.nze.nzexchange.extend

import android.icu.lang.UCharacter.GraphemeClusterBreak.L
import android.net.http.SslCertificate
import com.nze.nzexchange.bean.Result
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:https://blog.csdn.net/bailu666666/article/details/79829902
 * @创建时间：2018/12/10
 */

/**
 * 数字货币保留8位小数
 */
fun Double.formatForCurrency(): String {
//    var b: BigDecimal = BigDecimal(this)
//    return b.setScale(10,BigDecimal.ROUND_HALF_DOWN).toString()
    val df = DecimalFormat("0.########")
    df.roundingMode = RoundingMode.FLOOR
    return df.format(this.toBigDecimal())
}

fun Double.formatForLegal(): String {
    val df = DecimalFormat("0.##")
    return df.format(this)
}

fun Double.formatForLegalByFloor(): String {
    val df = DecimalFormat("0.##")
    df.roundingMode = RoundingMode.FLOOR
    return df.format(this.toBigDecimal())
}

fun Double.formatForLegal2(): Double {
    val df = DecimalFormat("0.##")
    return (df.format(this)).toDouble()
}

fun Double.formatForPrice(): String {
    val df = DecimalFormat("0.############")
//    df.roundingMode = RoundingMode.FLOOR;
    return df.format(this)
}

fun Double.format(rule: String): String {
    val df = DecimalFormat(rule)
    return df.format(this)
}

fun Double.format(num: Int): String {
    val df = DecimalFormat(getRule(num))
    return df.format(this)
}

fun getRule(num: Int): String = when (num) {
    1 -> "0.#"
    2 -> "0.##"
    3 -> "0.###"
    4 -> "0.####"
    5 -> "0.#####"
    6 -> "0.######"
    7 -> "0.#######"
    8 -> "0.########"
    else -> "0.########"
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
 * 直接舍弃O
 */
fun Double.retain4ByFloor(): String {
    val df = DecimalFormat("0.####")
    df.roundingMode = RoundingMode.FLOOR
    return df.format(this.toBigDecimal())
}

/**
 * 保留8位小数
 * 只要后一位非零，都加一
 */
fun Double.retain4ByUp(): String {
    val df = DecimalFormat("0.########")
    df.roundingMode = RoundingMode.UP
    return df.format(this.toBigDecimal())
}

/**
 * 保留8位小数
 * 超过直接舍弃
 */
fun Double.retain8ByFloor(): String {
    val df = DecimalFormat("0.########")
    df.roundingMode = RoundingMode.FLOOR
    return df.format(this.toBigDecimal())
}

/**
 * 只保留整数
 */
fun Double.retainInt(): String {
    val df = DecimalFormat("0")
    df.roundingMode = RoundingMode.FLOOR
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

//加法
fun Double.add(b: Double): Double {
    return (this.toBigDecimal().add(b.toBigDecimal())).toDouble()
}

//减法
fun Double.sub(b: Double): Double {
    return (this.toBigDecimal().subtract(b.toBigDecimal())).toDouble()
}

//乘法
fun Double.mul(b: Double): Double {
    return (this.toBigDecimal().multiply(b.toBigDecimal())).toDouble()
}

/**
 * 除，保留指定位数，四舍五入
 */
fun Double.divByUp(b: Double, len: Int): Double {
    return (this.toBigDecimal().divide(b.toBigDecimal(), len, BigDecimal.ROUND_HALF_UP)).toDouble()
}

/**
 * 除，保留指定位数，不四舍五入，直接舍弃
 */
fun Double.divByFloor(b: Double, len: Int): Double {
    return (this.toBigDecimal().divide(b.toBigDecimal(), len, BigDecimal.ROUND_FLOOR)).toDouble()
}

