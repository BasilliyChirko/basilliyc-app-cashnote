package basilliyc.cashnote.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

@Composable
fun BalanceText(
	modifier: Modifier = Modifier,
	text: String,
	style: TextStyle = TextStyle.Default,
	color: Color = Color.Unspecified,
	coinsTextStyle: TextStyle = style,
	coinsTextColor: Color = color,
) {
	
	val priceString = text.split('.')
	Row(modifier = modifier, verticalAlignment = Alignment.Bottom) {
		Text(
			text = priceString[0],
			style = style,
			color = color,
		)
		Text(
			text = ".${priceString[1]}",
			style = coinsTextStyle,
			color = coinsTextColor,
		)
	}
	
}