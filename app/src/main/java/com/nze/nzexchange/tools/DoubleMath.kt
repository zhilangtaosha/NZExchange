package com.nze.nzexchange.tools

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/11/21
 */
class DoubleMath {

    companion object {
        //加
        fun add(a: Double, b: Double): Double {
            return (a.toBigDecimal().add(b.toBigDecimal())).toDouble()
            //  return (a.toBigDecimal() + b.toBigDecimal()).toDouble()
        }

        //减
        fun sub(a: Double, b: Double): Double {
            return (a.toBigDecimal().subtract(b.toBigDecimal())).toDouble()
            // return (a.toBigDecimal() - b.toBigDecimal()).toDouble()
        }

        //乘
        fun mul(a: Double, b: Double, len: Int = 8): Double {
            return (a.toBigDecimal().multiply(b.toBigDecimal())).toDouble()
//            return (a.toBigDecimal() * b.toBigDecimal()).toDouble()
        }

        //除
        fun div(a: Double, b: Double): Double {
            return (a.toBigDecimal() / b.toBigDecimal()).toDouble()
        }

        /**
         * 除，保留指定位数，四舍五入
         */
        fun divByUp(a: Double, b: Double, len: Int): Double {
            return (a.toBigDecimal().divide(b.toBigDecimal(), len, BigDecimal.ROUND_HALF_UP)).toDouble()
        }

        /**
         * 除，保留指定位数，不四舍五入，直接舍弃
         */
        fun divByFloor(a: Double, b: Double, len: Int): Double {
            return (a.toBigDecimal().divide(b.toBigDecimal(), len, BigDecimal.ROUND_FLOOR)).toDouble()
        }


    }
}