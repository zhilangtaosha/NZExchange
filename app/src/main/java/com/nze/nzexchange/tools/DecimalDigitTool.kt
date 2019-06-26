package com.nze.nzexchange.tools

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/6/26
 */
class DecimalDigitTool {

    companion object {
        val DIGIT_1 = "0.#"
        val DIGIT_2 = "0.##"
        val DIGIT_3 = "0.###"
        val DIGIT_4 = "0.####"
        val DIGIT_5 = "0.#####"
        val DIGIT_6 = "0.######"
        val DIGIT_7 = "0.#######"
        val DIGIT_8 = "0.########"

        fun getDigit(num: Int): String = when (num) {
            1 -> DIGIT_1
            2 -> DIGIT_2
            3 -> DIGIT_3
            4 -> DIGIT_4
            5 -> DIGIT_5
            6 -> DIGIT_6
            7 -> DIGIT_7
            8 -> DIGIT_8
            else -> DIGIT_8
        }

    }
}