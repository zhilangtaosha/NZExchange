package com.nze.nzexchange.extend

import java.text.DecimalFormat

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/12/10
 */
fun Double.formatForCurrency(): String? {
    val df = DecimalFormat("0.########")
    return df.format(this)
}