package basilliyc.cashnote.utils

import java.util.Calendar

fun CalendarInstance(timeInMillis: Long) = Calendar.getInstance().apply { this.timeInMillis = timeInMillis }
