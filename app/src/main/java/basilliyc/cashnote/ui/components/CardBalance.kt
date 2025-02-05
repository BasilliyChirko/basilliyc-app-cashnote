package basilliyc.cashnote.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import basilliyc.cashnote.data.AccountColor
import basilliyc.cashnote.data.AccountCurrency
import basilliyc.cashnote.data.color
import basilliyc.cashnote.data.symbol
import basilliyc.cashnote.utils.DefaultPreview
import basilliyc.cashnote.utils.toPriceString

sealed interface CardBalanceLeadingIcon {
	data class Vector(val imageVector: ImageVector?) : CardBalanceLeadingIcon
	data class Currency(val currency: AccountCurrency) : CardBalanceLeadingIcon
}

fun CardBalanceLeadingIcon(currency: AccountCurrency) = CardBalanceLeadingIcon.Currency(currency)

fun CardBalanceLeadingIcon(imageVector: ImageVector?) = CardBalanceLeadingIcon.Vector(imageVector)

@Composable
fun CardBalance(
	modifier: Modifier = Modifier,
	onClick: () -> Unit,
	title: String,
	primaryValue: Double,
	secondaryValue: Double?,
	leadingIcon: CardBalanceLeadingIcon?,
	color: AccountColor?,
) {
	OutlinedCard(
		modifier = modifier,
		onClick = onClick,
		colors = CardDefaults.outlinedCardColors(
			containerColor = color?.color ?: Color.Unspecified
		),
		shape = MaterialTheme.shapes.small,
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
				
				when (val icon = leadingIcon) {
					is CardBalanceLeadingIcon.Currency -> {
						Text(
							text = icon.currency.symbol,
							style = MaterialTheme.typography.displaySmall,
						)
					}
					is CardBalanceLeadingIcon.Vector -> {
						if (icon.imageVector != null) {
							Icon(
								imageVector = icon.imageVector,
								contentDescription = title,
							)
						}
					}
					null -> Unit
				}
				
				Spacer(
					modifier = Modifier.weight(1F)
						.height(48.dp)
				)
				
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
		leadingIcon = CardBalanceLeadingIcon(Icons.Default.Home),
		color = AccountColor.Green,
	)
}

@Composable
@Preview(showBackground = true)
private fun CardBalancePreview2() = DefaultPreview {
	CardBalance(
		modifier = Modifier
			.width(180.dp),
		onClick = {},
		title = "Card",
		primaryValue = 100.0,
		secondaryValue = 50.0,
		leadingIcon = CardBalanceLeadingIcon(AccountCurrency.EUR),
		color = AccountColor.Green,
	)
}

@Composable
@Preview(showBackground = true)
private fun CardBalancePreview3() = DefaultPreview {
	CardBalance(
		modifier = Modifier
			.width(180.dp),
		onClick = {},
		title = "Card",
		primaryValue = 100.0,
		secondaryValue = 50.0,
		leadingIcon = null,
		color = AccountColor.Green,
	)
}