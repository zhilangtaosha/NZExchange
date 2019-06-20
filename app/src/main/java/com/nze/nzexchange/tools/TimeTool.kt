package com.nze.nzexchange.tools

import com.nze.nzeframework.tool.NLog
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
        const val PATTERN8 = "HH:mm:ss"
        const val PATTERN9 = "MM-dd HH:mm"
        const val PATTERN10 = "yyyy-MM-dd"
        

        fun date(): Date = Date()

        fun format2(pattern: String, date: Date): String {
            val tz = TimeZone.getTimeZone("Asia/Shanghai")
            TimeZone.setDefault(tz)
            val formatter = SimpleDateFormat(pattern)
            return formatter.format(date)
        }

        fun format(pattern: String, time: Long): String {
            return format2(pattern, Date(time))
        }

        fun format3(pattern: String,time: Long): String {
            val tz = TimeZone.getTimeZone("Asia/Shanghai")
            TimeZone.setDefault(tz)
            val sdf = SimpleDateFormat(pattern)
            val format = sdf.format(Date(time*1000))
            return format;
        }

        fun getLastUpdateTime(): CharSequence = format2(PATTERN2, date())

        //倒计时使用，单位秒
        fun formatTime(t: Long): String = with(t) {
            if (t > 0) {
                "${this / 60}分${this % 60}秒"
            } else {
                "0分0秒"
            }
        }

        //计时
        fun countTime(t: Long) = with(t / 1000) {
            if (t >= 0) {
                var s = this % 60
                var m = this / 60
                var h = this / 360
                "${if (h < 10) "0$h" else "$h"}:${if (m < 10) "0$m" else "$m"}:${if (s < 10) "0$s" else "$s"}"
            } else {
                "00:00:00"
            }
        }


        //时间转时间戳
        fun dateToStamp(date: String, pattern: String): Long {
            val sdf = SimpleDateFormat(pattern, Locale.CHINA)
            try {
                val date = sdf.parse(date)
                return date.time / 1000
            } catch (e: Exception) {
                return 0
            }
        }
    }


}