package basilliyc.cashnote.data

import java.util.Calendar

enum class StatisticSelectedPeriod(val monthCount: Int) {
	OneMonth(1),
	ThreeMonths(3),
	SixMonths(6),
	OneYear(12),
}

//enum class StatisticMonth {
//	January,
//	February,
//	March,
//	April,
//	May,
//	June,
//	July,
//	August,
//	September,
//	October,
//	November,
//	December,
//}

data class StatisticMonth(
	val month: Int,
	val year: Int,
) : Comparable<StatisticMonth> {
	override fun compareTo(other: StatisticMonth): Int {
		if (year != other.year) return year - other.year
		return month - other.month
	}
}

fun StatisticMonth(calender: Calendar) = StatisticMonth(
	month = calender.get(Calendar.MONTH),
	year = calender.get(Calendar.YEAR),
)

//fun StatisticMonth(number: Int) = StatisticMonth.entries[number]
//fun StatisticMonth(calendar: Calendar) = StatisticMonth.entries[calendar.get(Calendar.MONTH)]