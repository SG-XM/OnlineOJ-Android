package com.twt.zq.commons.util

import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by SGXM on 2018/10/26.
 */
object TimeUtil {

    const val DAY_MILL = 86400000L
    val formaterHms = SimpleDateFormat("HH:mm:ss", Locale.CHINA)
    val formater = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
    val formaterHMS = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
    //@return 2018-01-01 -> 2018-1-1
    fun formartMarkDate(time: String): String {
        return time.split("-").joinToString(separator = "-") {
            if (it.first() == '0') it.substring(1) else it
        }
    }

    fun formartDate2DoubleWidth(time: String): String {
        return time.split("-").joinToString(separator = "-") {
            if (it.length < 2 || it[1] == ' ') "0$it" else it
        }
    }

    fun second2String(sec: Int): String {
        val hour = sec / 3600
        val minute = sec / 60 % 60
        val second = sec % 60
        val hh = if (hour.toString().length == 1) "0" + hour.toString() else hour.toString()
        val mm = if (minute.toString().length == 1) "0" + minute.toString() else minute.toString()
        val ss = if (second.toString().length == 1) "0" + second.toString() else second.toString()
        return hh + ":" + mm + ":" + ss
    }


    //Date.time => 1567785600000 -> 2019-09-07
    fun string2Date(time: String): Date = formaterHMS.parse(formartDate2DoubleWidth(time))

    fun string2DateyMd(time: String): Date = formater.parse(formartDate2DoubleWidth(time))

    fun string2Calender(time: String): Calendar = Date2Cal(string2Date(time))

    fun Date2Cal(date: Date): Calendar = Calendar.getInstance().also { it.time = date }
    //@return Now String
    fun getToNowStringHms() = formaterHMS.format(Date(System.currentTimeMillis()))

    //@return Now String
    fun getToDayString() = formaterHMS.format(Date(System.currentTimeMillis()))

    //@return Today String
    fun getToDayStringYMD() = formater.format(Date(System.currentTimeMillis()))

    //@return Yesterday String
    fun getYesdayStringYMD() = formater.format(Date(System.currentTimeMillis() - DAY_MILL))

    //@return day berfore String
    fun getDayBeforeStringYMD() = formater.format(Date(System.currentTimeMillis() - DAY_MILL - DAY_MILL))

    //@return any day before String
    fun getDaysStringYMD(dayback: Int) = formater.format(Date(System.currentTimeMillis() - DAY_MILL * dayback))

    //@return Tomorrow Noon String
    fun getNextDayString() =
        formater.format(Calendar.getInstance().also { it.add(Calendar.DAY_OF_YEAR, 1) }.timeInMillis) + " 12:00"

    //@return 10year later
    fun getNextTenYearString() = (Calendar.getInstance().get(Calendar.YEAR) + 10).toString() + "-12-31 00:00"

    fun getInterval(str: String): String {
        val ms = System.currentTimeMillis() - string2Date(str).time
        val ss = 1000
        val mi = ss * 60
        val hh = mi * 60
        val dd = hh * 24

        val day = ms / dd
        val hour = (ms - day * dd) / hh
        val minute = (ms - day * dd - hour * hh) / mi
        val second = (ms - day * dd - hour * hh - minute * mi) / ss
        val milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss
        return "${day}天${hour}时"
    }

    fun Long2Interval(ms: Long): String {
        val ss = 1000
        val mi = ss * 60
        val hh = mi * 60
        val dd = hh * 24

        val day = ms / dd
        val hour = (ms - day * dd) / hh
        val minute = (ms - day * dd - hour * hh) / mi
        val second = (ms - day * dd - hour * hh - minute * mi) / ss
        val milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss
        return "${day}天${hour}时"
    }
    fun stamp2Date(s: String) = formater.format(Date(s.toLong()))
    fun stamp2Date(s: Long) = formater.format(Date(s))
    fun stamp2DateHMs(s: Long) = formaterHMS.format(Date(s))
    fun countTwoDateByDay(startDate: Date, endDate: Date): Int {
        val start = startDate
        val end = endDate
        val cal = Calendar.getInstance()
        cal.time = start
        val time1 = cal.timeInMillis
        cal.time = end
        val time2 = cal.timeInMillis
        val between_days = (time2 - time1) / (1000 * 3600 * 24)
        return between_days.toInt()
    }

    fun countTwoDateByHr(startDate: Date, endDate: Date): Double {
        val start = startDate
        val end = endDate
        val cal = Calendar.getInstance()
        cal.time = start
        val time1 = cal.timeInMillis
        cal.time = end
        val time2 = cal.timeInMillis
        val between_hrs = (time2 - time1) / (1000 * 3600.0)
        return between_hrs
    }

}