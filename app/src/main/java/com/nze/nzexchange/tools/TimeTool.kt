package com.nze.nzexchange.tools

import java.text.SimpleDateFormat
import java.util.*

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/11/22
 */
class TimeTool {
    companion object {
        const val PATTERN_DEFAULT = "yyyy-MM-dd HH:mm:ss"
        const val PATTERN2 = "MM/dd HH:mm"
        const val PATTERN3 = "yyyy-MM"
        const val PATTERN4 = "MM-dd"
        const val PATTERN5 = "HH:mm"
        const val PATTERN6 = "yyyy/MM/dd HH:mm:ss"
        const val PATTERN7 = "yyyy/MM/dd"

        fun date(): Date = Date()

        fun format(pattern: String, date: Date): String {
            val formatter = SimpleDateFormat(pattern, Locale.CHINA)
            return formatter.format(date)
        }

        fun format(pattern: String, time: Long): String {
            return format(pattern, Date(time))
        }


        fun getLastUpdateTime(): CharSequence = format(PATTERN2, date())

        //倒计时使用，单位秒
        fun formatTime(t: Long): String = with(t) {
            if (t > 0) {
                "${this / 60}分${this % 60}秒"
            } else {
                "0分0秒"
            }
        }
    }
}