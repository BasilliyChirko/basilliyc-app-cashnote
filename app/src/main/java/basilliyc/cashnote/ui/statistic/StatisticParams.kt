package basilliyc.cashnote.ui.statistic

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import basilliyc.cashnote.R
import basilliyc.cashnote.backend.manager.FinancialManager
import basilliyc.cashnote.backend.preferences.StatisticPreferences
import basilliyc.cashnote.data.FinancialCurrency
import basilliyc.cashnote.data.StatisticSelectedPeriod
import basilliyc.cashnote.ui.components.BackButton
import basilliyc.cashnote.ui.components.ItemVisibilitySelectable
import basilliyc.cashnote.ui.components.PopupMenuItem
import basilliyc.cashnote.ui.components.SimpleActionBar
import basilliyc.cashnote.ui.components.VerticalGrid
import basilliyc.cashnote.ui.components.VerticalGridCells
import basilliyc.cashnote.ui.components.menu.MenuRowPopup
import basilliyc.cashnote.ui.components.menu.MenuRowText
import basilliyc.cashnote.ui.components.rememberPopupMenuState
import basilliyc.cashnote.ui.stringName
import basilliyc.cashnote.utils.inject

@Composable
fun StatisticParams() {
	
	val preferences by remember { inject<StatisticPreferences>() }
	val financialManager by remember { inject<FinancialManager>() }
	
	val accounts by financialManager.getAccountListAsFlow().collectAsState(emptyList())
	
	Card(
		colors = CardDefaults.cardColors(
			containerColor = MaterialTheme.colorScheme.background
		)
	) {
		SimpleActionBar(
			title = stringResource(R.string.statistic_params),
			navigationIcon = {},
			actions = {
				BackButton(imageVector = Icons.Filled.Close)
			}
		)
		
		MenuRowPopup(
			title = stringResource(R.string.statistic_params_currency_label),
			value = preferences.currency.collectValue().name,
			items = {
				FinancialCurrency.entries.forEach {
					PopupMenuItem(
						text = it.name,
						onClick = { preferences.currency.set(it) }
					)
				}
			}
		)
		
		MenuRowPopup(
			title = stringResource(R.string.statistic_params_month_count_label),
			value = preferences.selectedPeriod.collectValue().stringName,
			items = {
				StatisticSelectedPeriod.entries.forEach {
					PopupMenuItem(
						text = it.stringName,
						onClick = { preferences.selectedPeriod.set(it) }
					)
				}
			}
		)
		
		val selectedAccountIds = preferences.accountIds.collectValue()
		val selectedAccountsCount = selectedAccountIds.size
		val accountsPopupMenuState = rememberPopupMenuState()
		MenuRowText(
			title = stringResource(R.string.statistic_params_account_label),
			value = when (selectedAccountsCount) {
				
				0 -> stringResource(R.string.statistic_params_account_none)
				
				1 -> accounts.find { it.id == selectedAccountIds.first() }?.name
					?: stringResource(R.string.statistic_params_account_one)
				
				accounts.size -> stringResource(R.string.statistic_params_account_all)
				
				else -> stringResource(
					R.string.statistic_params_account_many,
					selectedAccountsCount
				)
			},
			onClick = { accountsPopupMenuState.toggle() }
		)
		if (accountsPopupMenuState.expanded.value) {
			VerticalGrid(
				modifier = Modifier
					.verticalScroll(rememberScrollState())
					.padding(8.dp),
				columns = VerticalGridCells.Fixed(2),
				itemsCount = accounts.size,
				verticalSpace = 8.dp,
				horizontalSpace = 8.dp,
				content = {
					val account = accounts[it]
					ItemVisibilitySelectable(
						title = account.name,
						icon = {
							Text(
								text = account.currency.symbol,
								style = MaterialTheme.typography.displaySmall,
							)
						},
						isSelected = account.id in selectedAccountIds,
						onClick = {
							preferences.accountIds.update {
								it.toMutableList().apply {
									if (account.id in this) {
										remove(account.id)
									} else {
										add(account.id)
									}
								}
							}
						}
					)
				}
			)
		}
		
	}
	
}