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
    fun format(){
        val p:Double = 1.881111111111111
        val d=20.4
        println("add>>${DoubleMath.add(p,d)}")
        println("sub>>${DoubleMath.sub(p,d)}")
        val m = DoubleMath.mul(p,d)
        println("mul>>$m ${m.retain8ByFloor()}")
        println("div>>${DoubleMath.divByFloor(p,d,2)}")
    }
}