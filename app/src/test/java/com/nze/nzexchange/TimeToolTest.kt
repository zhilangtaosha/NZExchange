package com.nze.nzexchange

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
    fun format(){
        val t = "1555541760"
        val s = TimeTool.format3(t.toLong())
        println(s)
    }
}