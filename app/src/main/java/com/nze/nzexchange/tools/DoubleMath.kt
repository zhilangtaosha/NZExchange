package com.nze.nzexchange.tools

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
            return (a.toBigDecimal() + b.toBigDecimal()).toDouble()
        }

        //减
        fun sub(a: Double, b: Double): Double {
            return (a.toBigDecimal() - b.toBigDecimal()).toDouble()
        }

        //乘
        fun mul(a: Double, b: Double): Double {
            return (a.toBigDecimal() * b.toBigDecimal()).toDouble()
        }

        //除
        fun div(a: Double, b: Double): Double {
            return (a.toBigDecimal() / b.toBigDecimal()).toDouble()
        }
    }
}