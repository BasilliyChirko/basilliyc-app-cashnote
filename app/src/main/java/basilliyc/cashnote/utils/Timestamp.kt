package basilliyc.cashnote.utils

import android.annotation.SuppressLint
import android.content.Context
import android.text.format.DateUtils.FORMAT_NO_YEAR
import android.text.format.DateUtils.FORMAT_NUMERIC_DATE
import android.text.format.DateUtils.FORMAT_SHOW_DATE
import android.text.format.DateUtils.formatDateTime
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date


object UtilsTimestamp {
	
	fun format(context: Context?, timestamp: Long, style: TimestampStyle): String {
		val date = Date(timestamp)
		
		return when (style) {
			TimestampStyle.YearMonthDay ->
				DateFormat.getDateInstance(DateFormat.SHORT).format(date)
			
			TimestampStyle.MonthDay -> {
				val flags = FORMAT_SHOW_DATE or FORMAT_NO_YEAR or FORMAT_NUMERIC_DATE
				formatDateTime(context, timestamp, flags)
			}
			
			TimestampStyle.MonthDayHourMinute -> format(context,
				timestamp,
				TimestampStyle.MonthDay) + " " + format(context,
				timestamp,
				TimestampStyle.HourMinute)
			
			TimestampStyle.HourMinute ->
				DateFormat.getTimeInstance(DateFormat.SHORT).format(date)
			
			TimestampStyle.HourMinuteSeconds ->
				DateFormat.getTimeInstance(DateFormat.MEDIUM).format(date)
			
			TimestampStyle.YearMonthDayHourMinute ->
				DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(date)
			
			TimestampStyle.YearMonthDayHourMinuteSeconds ->
				DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(date)
			
			TimestampStyle.TodayTime ->
				if (isToday(timestamp)) format(context, timestamp, TimestampStyle.HourMinute)
				else format(context, timestamp, TimestampStyle.MonthDayHourMinute)
		}
	}
	
	fun isToday(timestamp: Long): Boolean {
		val nowCalendar = Calendar.getInstance()
		val timeCalendar = Calendar.getInstance().apply {
			timeInMillis = timestamp
		}
		return timeCalendar[Calendar.DAY_OF_MONTH] == nowCalendar[Calendar.DAY_OF_MONTH] &&
				timeCalendar[Calendar.MONTH] == nowCalendar[Calendar.MONTH] &&
				timeCalendar[Calendar.YEAR] == nowCalendar[Calendar.YEAR]
	}
	
}

fun Long.format(context: Context?, style: TimestampStyle) =
	UtilsTimestamp.format(context, this, style)

fun Long.format(style: TimestampStyle) = UtilsTimestamp.format(null, this, style)

enum class TimestampStyle {
	YearMonthDay,
	MonthDay,
	MonthDayHourMinute,
	HourMinute,
	HourMinuteSeconds,
	YearMonthDayHourMinute,
	YearMonthDayHourMinuteSeconds,
	TodayTime,
}

@SuppressLint("SimpleDateFormat")
fun Long.formatTime(pattern: String): String {
	return SimpleDateFormat(pattern).format(Date(this))
}

@SuppressLint("SimpleDateFormat")
fun String.formatTime(pattern: String): Long {
	return SimpleDateFormat(pattern).parse(this)?.time
		?: throw Throwable("Can`t parse string '$this' by pattern '$pattern'")
}

fun Long.toCalendar(): Calendar {
	val calendar = Calendar.getInstance()
	calendar.timeInMillis = this
	return calendar
}

const val secInMillis = 1000L
const val minInMillis = 1000L * 60L
const val hourInMillis = 1000L * 60L * 60L
const val dayInMillis = 1000L * 60L * 60L * 24L
const val monthInMillis = 1000L * 60L * 60L * 24L * 30L
const val yearInMillis = 1000L * 60L * 60L * 24L * 30L * 12L