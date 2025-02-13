package basilliyc.cashnote.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import basilliyc.cashnote.data.FinancialColor
import basilliyc.cashnote.data.FinancialCurrency
import basilliyc.cashnote.data.color
import basilliyc.cashnote.ui.symbol
import basilliyc.cashnote.ui.theme.backgroundCardGradient
import basilliyc.cashnote.utils.DefaultPreview
import basilliyc.cashnote.utils.toPriceColor
import basilliyc.cashnote.utils.toPriceString

sealed interface CardBalanceLeadingIcon {
	data class Vector(val imageVector: ImageVector?) : CardBalanceLeadingIcon
	data class Currency(val currency: FinancialCurrency) : CardBalanceLeadingIcon
}

fun CardBalanceLeadingIcon(currency: FinancialCurrency) = CardBalanceLeadingIcon.Currency(currency)

fun CardBalanceLeadingIcon(imageVector: ImageVector?) = CardBalanceLeadingIcon.Vector(imageVector)

@Composable
fun CardBalance(
	modifier: Modifier = Modifier,
	onClick: () -> Unit,
	title: String,
	primaryValue: Double,
	secondaryValue: Double?,
	leadingIcon: CardBalanceLeadingIcon?,
	color: FinancialColor?,
	isWide: Boolean = false,
) {
	OutlinedCard(
		modifier = modifier,
		onClick = onClick,
		shape = MaterialTheme.shapes.small,
		border = color?.color?.let { BorderStroke(1.dp, it) }
			?: CardDefaults.outlinedCardBorder(),
	) {
		Box(
			modifier = Modifier.backgroundCardGradient(color)
		) {
			
			if (isWide) {
				Row(
					modifier = Modifier
						.padding(8.dp),
					content = {
						Column {
							Title(title = title)
							Icon(icon = leadingIcon, title = title)
						}
						Spacer(
							modifier = Modifier
								.weight(1F)
								.height(48.dp)
						)
						Values(primaryValue = primaryValue, secondaryValue = secondaryValue)
					}
				)
			} else {
				Column(
					modifier = Modifier
						.padding(8.dp),
					content = {
						Title(title = title)
						Row(
							verticalAlignment = Alignment.CenterVertically,
						) {
							Icon(icon = leadingIcon, title = title)
							Spacer(
								modifier = Modifier
									.weight(1F)
									.height(48.dp)
							)
							Values(primaryValue = primaryValue, secondaryValue = secondaryValue)
						}
					}
				)
			}
			
		}
	}
}

@Composable
private fun Title(
	title: String,
) {
	Text(
		modifier = Modifier,
		text = title,
		style = MaterialTheme.typography.titleMedium,
	)
}

@Composable
private fun Icon(
	icon: CardBalanceLeadingIcon?,
	title: String,
) {
	when (icon) {
		is CardBalanceLeadingIcon.Currency -> {
			Text(
				text = icon.currency.symbol,
				style = MaterialTheme.typography.headlineMedium,
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
}

@Composable
private fun Values(
	primaryValue: Double,
	secondaryValue: Double?,
) {
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
				color = secondaryValue.toPriceColor(),
			)
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
		color = FinancialColor.Green,
		isWide = true,
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
		leadingIcon = CardBalanceLeadingIcon(FinancialCurrency.EUR),
		color = FinancialColor.Green,
		isWide = true,
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
		color = FinancialColor.Green,
	)
}