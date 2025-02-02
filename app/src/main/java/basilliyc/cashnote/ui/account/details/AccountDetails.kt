package basilliyc.cashnote.ui.account.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import basilliyc.cashnote.R
import basilliyc.cashnote.data.AccountCurrency
import basilliyc.cashnote.data.symbol
import basilliyc.cashnote.ui.PreviewValues
import basilliyc.cashnote.ui.components.BoxLoading
import basilliyc.cashnote.ui.components.CardBalance
import basilliyc.cashnote.ui.components.CardBalanceLeadingIcon
import basilliyc.cashnote.ui.components.SimpleActionBar
import basilliyc.cashnote.ui.components.VerticalGrid
import basilliyc.cashnote.ui.components.VerticalGridCells
import basilliyc.cashnote.utils.DefaultPreview
import basilliyc.cashnote.utils.ScaffoldBox
import basilliyc.cashnote.utils.log
import basilliyc.cashnote.utils.toPriceString

//--------------------------------------------------------------------------------------------------
//  ROOT
//--------------------------------------------------------------------------------------------------


@Composable
fun AccountDetails() {

}


@Composable
@Preview(showBackground = true)
private fun AccountDetailsPreview() = DefaultPreview {
	PageData(
		page = AccountDetailsState.Page.Data(
			account = PreviewValues.accountFilled,
			showBalanceValue = true,
			showBalanceProfit = true,
			balanceSpend = 1234.56,
			balanceReceive = 1238.56,
			categories = PreviewValues.categories.map {
				AccountDetailsState.CategoryWithBalance(
					category = it,
					balance = 500.0,
					deviation = -40.0,
				)
			}
		)
	)
}

//--------------------------------------------------------------------------------------------------
//  PAGE
//--------------------------------------------------------------------------------------------------

@Composable
private fun Page(
	state: AccountDetailsState,
) {
	when (val content = state.page) {
		is AccountDetailsState.Page.Data -> PageData(content)
		AccountDetailsState.Page.Loading -> PageLoading()
	}
}

@Composable
private fun PageLoading() {
	ScaffoldBox(
		topBar = { SimpleActionBar() },
		content = { BoxLoading() }
	)
}

//--------------------------------------------------------------------------------------------------
//  PAGE.DATA
//--------------------------------------------------------------------------------------------------

@Composable
private fun PageData(
	page: AccountDetailsState.Page.Data,
) {
	ScaffoldBox(
		topBar = {
			SimpleActionBar(
				title = page.account.name
			)
		},
		content = {
			Column(
				modifier = Modifier.verticalScroll(rememberScrollState())
			) {
				PageDataBalance(page)
				PageDataCategories(page)
			}
		}
	)
}

@Composable
private fun ColumnScope.PageDataBalance(
	page: AccountDetailsState.Page.Data,
) {
	if (!page.showBalanceValue) return
	
	Card(
		modifier = Modifier
			.padding(horizontal = 16.dp)
	) {
		BalanceRow(
			modifier = Modifier.padding(vertical = 8.dp),
			title = stringResource(R.string.account_balance),
			value = page.account.balance,
			currency = page.account.currency,
			isLarge = true,
		)
		if (
			page.showBalanceProfit &&
			page.balanceSpend != null &&
			page.balanceReceive != null
		) {
			HorizontalDivider()
			
			Column(
				modifier = Modifier.padding(vertical = 8.dp)
			) {
				BalanceRow(
					title = stringResource(R.string.account_balance_receive),
					value = page.balanceReceive,
				)
				BalanceRow(
					title = stringResource(R.string.account_balance_spend),
					value = page.balanceSpend,
				)
				BalanceRow(
					title = stringResource(R.string.account_balance_profit),
					value = page.balanceReceive - page.balanceSpend,
					showPlus = true,
				)
			}
			
		}
	}
}

@Composable
private fun BalanceRow(
	modifier: Modifier = Modifier,
	isLarge: Boolean = false,
	title: String,
	value: Double,
	currency: AccountCurrency? = null,
	showPlus: Boolean = false,
) {
	Row(
		modifier = modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		Text(
			text = title,
			modifier = Modifier.weight(1f),
			maxLines = 1,
			style = if (isLarge) {
				MaterialTheme.typography.titleLarge
			} else {
				MaterialTheme.typography.titleMedium
			},
		)
		Text(
			text = buildString {
				if (currency != null) {
					append(currency.symbol)
					append(" ")
				}
				append(value.toPriceString(showPlus))
			},
			maxLines = 1,
			style = if (isLarge) {
				MaterialTheme.typography.titleLarge
			} else {
				MaterialTheme.typography.titleMedium
			},
		)
	}
}

@Composable
private fun ColumnScope.PageDataCategories(
	page: AccountDetailsState.Page.Data,
) {
	val categories = page.categories
	log("categories.size",categories.size)
	VerticalGrid(
		modifier = Modifier
			.padding(16.dp),
		columns = VerticalGridCells.Adaptive(140.dp),
		itemsCount = categories.size,
		verticalSpace = 8.dp,
		horizontalSpace = 8.dp,
	) { index ->
		val category = categories[index]
		
		CardBalance(
			modifier = Modifier,
			onClick = {
				//TODO
			},
			title = category.category.name,
			primaryValue = category.balance,
			secondaryValue = category.deviation,
			leadingIcon = CardBalanceLeadingIcon(category.category.icon?.imageVector),
			color = null,
		)
	}
}

