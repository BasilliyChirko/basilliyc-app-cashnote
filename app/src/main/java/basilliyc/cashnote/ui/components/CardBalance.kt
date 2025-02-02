package basilliyc.cashnote.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import basilliyc.cashnote.data.AccountColor
import basilliyc.cashnote.data.AccountCurrency
import basilliyc.cashnote.data.color
import basilliyc.cashnote.data.symbol
import basilliyc.cashnote.utils.DefaultPreview
import basilliyc.cashnote.utils.toPriceString

@Composable
fun CardBalance(
	modifier: Modifier = Modifier,
	onClick: () -> Unit,
	title: String,
	primaryValue: Double,
	secondaryValue: Double?,
	currency: AccountCurrency?,
	color: AccountColor?,
) {
	Card(
		modifier = modifier,
		onClick = onClick,
		colors = CardDefaults.cardColors(
			containerColor = color?.color ?: Color.Unspecified
		)
	) {
		Column(
			modifier = Modifier.padding(8.dp)
		) {
			Text(
				modifier = Modifier,
				text = title,
				style = MaterialTheme.typography.titleMedium,
			)
			
			Row(
				verticalAlignment = Alignment.CenterVertically,
			) {
				if (currency != null) {
					Text(
						modifier = Modifier,
						text = currency.symbol,
						style = MaterialTheme.typography.displaySmall,
					)
				}
				
				Spacer(modifier = Modifier.weight(1F))
				
				Column(
					horizontalAlignment = Alignment.End,
					verticalArrangement = Arrangement.Center,
				) {
					
					Text(
						text = primaryValue.toPriceString(showPlus = false),
						modifier = Modifier,
						style = MaterialTheme.typography.titleLarge,
					)
					
					if (secondaryValue != null) {
						Text(
							modifier = Modifier,
							text = secondaryValue.toPriceString(showPlus = true),
							style = MaterialTheme.typography.bodyLarge,
						)
					}
					
				}
				
			}
			
			
		}
	}
}

@Composable
@Preview(showBackground = true)
private fun CardBalancePreview() = DefaultPreview {
	CardBalance(
		modifier = Modifier
			.width(180.dp),
		onClick = {},
		title = "Card",
		primaryValue = 100.0,
		secondaryValue = 50.0,
		currency = AccountCurrency.EUR,
		color = AccountColor.Green,
	)
}