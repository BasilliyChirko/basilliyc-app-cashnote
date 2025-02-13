package basilliyc.cashnote.ui.theme

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import basilliyc.cashnote.data.FinancialColor
import basilliyc.cashnote.data.color

val AccountColorDayRed = Color(0xFFFDB1B1)
val AccountColorDayOrange = Color(0xFFFDCFB1)
val AccountColorDayGreen = Color(0xFFADFDD3)
val AccountColorDayBlue = Color(0xFFA5D7FF)
val AccountColorDayYellow = Color(0xFFFDEFC1)
val AccountColorDayPurple = Color(0xFFDFC9FF)
val AccountColorDayTurquoise = Color(0xFFBEEDFF)

val AccountColorNightRed = Color(0xFF790000)
val AccountColorNightOrange = Color(0xFF794D00)
val AccountColorNightGreen = Color(0xFF00622A)
val AccountColorNightBlue = Color(0xFF004873)
val AccountColorNightYellow = Color(0xFF605E00)
val AccountColorNightPurple = Color(0xFF280049)
val AccountColorNightTurquoise = Color(0xFF003A49)

val colorGrey99 = Color(0xFF999999)

val colorGreen500 = Color(0xFF4CAF50)
val colorRed500 = Color(0xFFF44336)

@Composable
fun FinancialColor.backgroundCardGradient(): Brush {
	return Brush.horizontalGradient(
		listOf(
			color,
			color.copy(alpha = 0.4F),
			Color.Unspecified,
		)
	)
}

@Composable
fun Modifier.backgroundCardGradient(
	color: FinancialColor?,
	shape: Shape = RectangleShape,
): Modifier {
	if (color == null) return this
	return this.background(brush = color.backgroundCardGradient(), shape = shape)
}

@Composable
fun FinancialColor.backgroundPageGradient(): Brush {
	return Brush.verticalGradient(
		listOf(
			color,
//			color.copy(alpha = 0.8F),
//			color.copy(alpha = 0.4F),
			color.copy(alpha = 0.4F),
			Color.Unspecified,
		)
	)
}

@Composable
fun Modifier.backgroundPageGradient(
	color: FinancialColor?,
	shape: Shape = RectangleShape,
): Modifier {
	if (color == null) return this
	return this.background(brush = color.backgroundPageGradient(), shape = shape)
}