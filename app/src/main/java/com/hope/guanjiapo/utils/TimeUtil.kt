package com.hope.guanjiapo.utils

import android.annotation.SuppressLint
import android.text.TextUtils
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


/**
 * @author kazeik.chen
 */
@SuppressLint("SimpleDateFormat")
object TimeUtil {
    val DATE_YYYY = "yyyy"
    val DATE_YMS = "yyyy-MM-dd"
    val DATE_YM = "yyyy-MM"
    val DATE_MS = "MM-dd"
    val DATE_YMD_HMS = "yyyy-MM-dd HH:mm:ss"
    val DATE_M = "MM"
    val DATE_D = "dd"
    val DATE_H = "HH"
    val DATE_MM = "mm"
    val DATE_SS = "ss"
    val DATE_HMS = "HH:mm:ss"
    val DATE_HM = "HH:mm"
    val DATE_Y_M = "yyyyMM"
    val DATE_Y_M_D = "yyyyMMDD"

    fun ymdToYMD(date: String): String {
        val temp = date.split("-")
        var sub = ""
        for (item in temp) {
            sub += item
        }
        return sub
    }

    fun converTime(time: Long): String {
        val currentSeconds = System.currentTimeMillis() / 1000
        val timeGap = currentSeconds - time / 1000// 与现在时间相差秒数
        val timeStr = when {
            timeGap > 3 * 24 * 60 * 60 -> getDayTime(time) + " " + getMinTime(time)
            timeGap > 24 * 2 * 60 * 60 -> // 2天以上就返回标准时间
                "前天 " + getMinTime(time)
            timeGap > 24 * 60 * 60 -> // 1天-2天
                (timeGap / (24 * 60 * 60)).toString() + "昨天 " + getMinTime(time)
            timeGap > 60 * 60 -> // 1小时-24小时
                (timeGap / (60 * 60)).toString() + "今天 " + getMinTime(time)
            timeGap > 60 -> // 1分钟-59分钟
                (timeGap / 60).toString() + "今天 " + getMinTime(time)
            else -> // 1秒钟-59秒钟
                "今天 " + getMinTime(time)
        }
        return timeStr

    }

    fun dateToLong(sDt: String, format: String): Long {
        val sdf = SimpleDateFormat(format)
        var time: Long = 0
        try {
            val dt2 = sdf.parse(sDt)
            time = dt2.time / 1000
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return time
    }

    /**
     * 根据当前日期获得所在周的日期区间（周一和周日日期）
     * @param date Date
     * @return String
     */
    fun getTimeInterval(date: Date): HashMap<String, String> {
        val sdf = SimpleDateFormat(DATE_YMS)
        val cal = Calendar.getInstance()
        cal.time = date
        // 判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了
        val dayWeek = cal.get(Calendar.DAY_OF_WEEK)// 获得当前日期是一个星期的第几天
        if (1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1)
        }
        // 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        cal.firstDayOfWeek = Calendar.MONDAY
        // 获得当前日期是一个星期的第几天
        val day = cal.get(Calendar.DAY_OF_WEEK)
        // 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        cal.add(Calendar.DATE, cal.firstDayOfWeek - day)
        val imptimeBegin = sdf.format(cal.time)
        cal.add(Calendar.DATE, 6)
        val imptimeEnd = sdf.format(cal.time)
        return hashMapOf<String, String>("start" to imptimeBegin, "end" to imptimeEnd)
    }

    fun getChatTime(time: Long): String {
        return getMinTime(time)
    }

    fun getPrefix(time: Long): String {
        val currentSeconds = System.currentTimeMillis()
        val timeGap = currentSeconds - time// 与现在时间差
        var timeStr: String?
        if (timeGap > 24 * 3 * 60 * 60 * 1000) {
            timeStr = getDayTime(time) + " " + getMinTime(time)
        } else if (timeGap > 24 * 2 * 60 * 60 * 1000) {
            timeStr = "前天 " + getMinTime(time)
        } else if (timeGap > 24 * 60 * 60 * 1000) {
            timeStr = "昨天 " + getMinTime(time)
        } else {
            timeStr = "今天 " + getMinTime(time)
        }
        return timeStr

    }

    fun StringToCalendar(time: String, type: String): Calendar {
        val sdf = SimpleDateFormat(type)
        val date = sdf.parse(time)
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar
    }

    fun StringTimeToLong(time: String, type: String): Long {
        return StringToCalendar(time, type).timeInMillis
    }


    fun getDayByType(time: Long, type: String): String {
        val format = SimpleDateFormat(type, Locale.CHINA)
        return format.format(Date(time))
    }

    fun getDayTime(time: Long): String {
        val format = SimpleDateFormat("yy-MM-dd", Locale.CHINA)
        return format.format(Date(time))
    }

    fun getMinTime(time: Long): String {
        val format = SimpleDateFormat("yy-MM-dd HH:mm", Locale.CHINA)
        return format.format(Date(time))
    }

    fun getDayYYYYTime(time: Long): String {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
        return format.format(Date(time))
    }

    fun getYYYYMMDDHHMM(time: Long): String {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)
        return format.format(Date(time))
    }

    fun getYYYYMMDDHHMMSS(time: Long): String {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
        return format.format(Date(time))
    }

    fun getYYYYMMDDHH(time: Long): String {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
        return format.format(Date(time))
    }

    fun getYYYYMMDD(time: Long): String {
        val format = SimpleDateFormat("yyyy/MM/dd", Locale.CHINA)
        return format.format(Date(time))
    }

    fun getHHMMSS(time: Long): String {
        val format = SimpleDateFormat("HH:mm:ss", Locale.CHINA)
        return format.format(Date(time))
    }


    fun getSpecifiedDayBefore(specifiedDay: String, iDay: Int): String {
        if (TextUtils.isEmpty(specifiedDay)) return ""
        val c = Calendar.getInstance()
        var date: Date? = null
        try {
            date = SimpleDateFormat("yy-MM-dd").parse(specifiedDay)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        c.time = date
        val day = c.get(Calendar.DATE)
        c.set(Calendar.DATE, day - iDay)

        return SimpleDateFormat("yyyy-MM-dd").format(c
                .time)
    }

    /**
     * 两个时间相差距离多少天多少小时多少分多少秒
     *
     * @param str1 时间参数 1 格式：1990-01-01 12:00:00
     * @param str2 时间参数 2 格式：2009-01-01 12:00:00
     * @return String 返回值为：xx天xx小时xx分xx秒
     */
    fun getDistanceTimes(str1: String, str2: String): LongArray {
        val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val one: Date
        val two: Date
        var day: Long = 0
        var hour: Long = 0
        var min: Long = 0
        var sec: Long = 0
        try {
            one = df.parse(str1)
            two = df.parse(str2)
            val time1 = one.time
            val time2 = two.time
            val diff: Long
            if (time1 < time2) {
                diff = time2 - time1
            } else {
                diff = time1 - time2
            }
            day = diff / (24 * 60 * 60 * 1000)
            hour = diff / (60 * 60 * 1000) - day * 24
            min = diff / (60 * 1000) - day * 24 * 60 - hour * 60
            sec = diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return longArrayOf(day, hour, min, sec)
    }

    fun getLastWeek(cTime: Long, type: String): String {
        val format = SimpleDateFormat(type)
        val c = Calendar.getInstance()
        c.time = Date(cTime)
        c.add(Calendar.DATE, -7)
        val d = c.getTime()
        return format.format(d)
    }

    fun getDistanceTime(startTime: Long, endTime: Long): LongArray? {
        val diff = endTime - startTime
        var day: Long = 0
        var hour: Long = 0
        var min: Long = 0
        var sec: Long = 0
        if (diff == 0L) {
            return null
        } else {
            val diffTime = LongArray(4)
            day = diff / (24 * 60 * 60 * 1000)
            hour = diff / (60 * 60 * 1000) - day * 24
            min = diff / (60 * 1000) - day * 24 * 60 - hour * 60
            sec = diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60
            diffTime[0] = day
            diffTime[1] = hour
            diffTime[2] = min
            diffTime[3] = sec
            return diffTime
        }
    }

    fun strToCalendar(dateStr: String, format: String): Calendar? {
        val sdf = SimpleDateFormat(format)
        var date: Date? = null
        try {
            date = sdf.parse(dateStr)
            val calendar = Calendar.getInstance()
            calendar.time = date
            return calendar
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return null
    }

    /**
     * 判断当前时间在两个时间段内
     */
    fun checkTimeInTime(currentTime: Long, startTime: Long, endTime: Long): Boolean {
        if (currentTime in (startTime + 1)..(endTime - 1)) {
            return true
        }
        return false
    }

    fun sameDate(d1: Date, d2: Date, type: String): Boolean {
        val fmt = SimpleDateFormat(type);
        return fmt.format(d1).equals(fmt.format(d2));
    }

    fun dateToString(type: String): String {
        val sdf = SimpleDateFormat(type)
        return sdf.format(Date())
    }

    fun stringToDate(str: String, type: String): Date {
        val sdf = SimpleDateFormat(type)
        return sdf.parse(str)
    }

    fun calendarToDate(): Date {
        val calendar = Calendar.getInstance()
        return calendar.time
    }

    fun dateToCalendar(): Calendar {
        val calendar = Calendar.getInstance()
        calendar.time = java.util.Date()
        return calendar
    }

    /**
     * 判断两月份的状态
     */
    fun compare_date(startTime: String, endTime: String, type: String): Int {
        val df = SimpleDateFormat(type)
        try {
            val dt1 = df.parse(startTime)
            val dt2 = df.parse(endTime)
            if (dt1.time > dt2.time) {
                return 1
            } else if (dt1.time < dt2.time) {
                return -1
            } else {
                return 0
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        return 0
    }
}
