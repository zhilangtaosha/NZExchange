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

        fun date(): Date = Date()

        fun format(pattern: String, date: Date): String {
            val formatter = SimpleDateFormat(pattern, Locale.CHINA)
            return formatter.format(date)
        }

        fun format(pattern: String, time: Long): String {
            return format(pattern, Date(time))
        }


        fun getLastUpdateTime(): CharSequence = format(PATTERN2, date())

        //倒计时使用
        fun formatTime(t: Long): String = with(t) {
            "${this / 60}分${this % 60}秒"
        }
    }
}