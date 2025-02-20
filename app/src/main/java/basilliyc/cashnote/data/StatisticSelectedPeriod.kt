package basilliyc.cashnote.data

enum class StatisticSelectedPeriod(val monthCount: Int) {
	OneMonth(1),
	ThreeMonths(3),
	SixMonths(6),
	OneYear(12),
	AllTime(0),
}

