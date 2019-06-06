package com.nze.nzexchange

import com.nze.nzexchange.extend.add
import com.nze.nzexchange.extend.formatForPrice
import com.nze.nzexchange.extend.retain8ByFloor
import com.nze.nzexchange.tools.DoubleMath
import com.nze.nzexchange.tools.TimeTool
import org.junit.Test

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/4/18
 */
class TimeToolTest {

    @Test
    fun format() {
        val give = "12.1"
        var rate = 0.0
        if (!give.isNullOrEmpty()) {
            if (give.contains(".")) {
                val decimal = give.substring(give.indexOf(".") + 1, give.length)
                var s = "0."
                val len = decimal.length
                if (len > 1) {
                    for (i in 0 until len - 1) {
                        s = "${s}0"
                    }
                }
                s = "${s}1"
                rate = s.toDouble()
            } else {
                rate = 1.0
            }
        }
        println(rate)
    }
}