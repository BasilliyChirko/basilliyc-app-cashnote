package basilliyc.cashnote.utils

import android.icu.util.Calendar.*
import java.util.Calendar

fun CalendarInstance(timeInMillis: Long = System.currentTimeMillis()) = Calendar.getInstance().apply { this.timeInMillis = timeInMillis }

fun Calendar.moveToFirstDayOfMonth(): Calendar {
	set(DAY_OF_MONTH, 1)
	set(HOUR_OF_DAY, 0)
	set(MINUTE, 0)
	set(SECOND, 0)
	set(MILLISECOND, 0)
	return this
}
