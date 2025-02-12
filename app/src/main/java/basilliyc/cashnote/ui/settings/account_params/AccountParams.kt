package basilliyc.cashnote.ui.settings.account_params

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import basilliyc.cashnote.R
import basilliyc.cashnote.backend.preferences.AppPreferences
import basilliyc.cashnote.data.FinancialStatisticParams
import basilliyc.cashnote.data.getAllowedCalculations
import basilliyc.cashnote.data.labelText
import basilliyc.cashnote.data.nameText
import basilliyc.cashnote.ui.PreviewValues
import basilliyc.cashnote.ui.components.BoxLoading
import basilliyc.cashnote.ui.components.PopupMenuItem
import basilliyc.cashnote.ui.components.menu.MenuRowPopup
import basilliyc.cashnote.ui.components.menu.MenuRowSwitch
import basilliyc.cashnote.utils.DefaultPreview
import basilliyc.cashnote.utils.rememberInject

@Composable
fun AccountParams() {
	val viewModel = viewModel<AccountParamsViewModel>()
	Page(page = viewModel.stateHolder.page, listener = viewModel)
}

@Composable
private fun Page(page: AccountParamsStateHolder.Page, listener: AccountParamsListener) {
	when (page) {
		is AccountParamsStateHolder.Page.Data -> PageData(page, listener)
		AccountParamsStateHolder.Page.Loading -> BoxLoading()
	}
}

@Composable
@Preview(showBackground = true)
private fun PageDataPreview() = DefaultPreview {
	PageData(
		page = AccountParamsStateHolder.Page.Data(
			statisticParams = PreviewValues.statisticParams,
			allowedCalculations = FinancialStatisticParams.Period.Day.getAllowedCalculations(),
		),
		listener = object : AccountParamsListener {
			override fun onStatisticPeriodChanged(period: FinancialStatisticParams.Period) = Unit
			override fun onStatisticPrimaryValueCalculationChanged(calculation: FinancialStatisticParams.Calculation) =
				Unit
			
			override fun onStatisticSecondaryValueCalculationChanged(calculation: FinancialStatisticParams.Calculation) =
				Unit
			
			override fun onStatisticShowAccountStatisticChanged(showAccountStatistic: Boolean) =
				Unit
			
			override fun onStatisticShowSecondaryValueForCategoryChanged(
				showSecondaryValueForCategory: Boolean,
			) = Unit
			
			override fun onStatisticShowSecondaryValueForAccountChanged(showSecondaryValueForAccount: Boolean) =
				Unit
		}
	)
}

@Composable
private fun PageData(page: AccountParamsStateHolder.Page.Data, listener: AccountParamsListener) {
	Column {
		MenuRowSwitch(
			title = stringResource(R.string.account_params_statistic_show_account_statistic),
			checked = page.statisticParams.showAccountStatistic,
			onCheckedChange = listener::onStatisticShowAccountStatisticChanged,
		)
		
		HorizontalDivider()
		
		MenuRowPopup(
			title = stringResource(R.string.account_params_statistic_period),
			value = page.statisticParams.period.nameText(),
		) {
			FinancialStatisticParams.Period.entries.forEach { period ->
				PopupMenuItem(
					onClick = { listener.onStatisticPeriodChanged(period) },
					text = period.nameText(),
				)
			}
		}
		
		HorizontalDivider()
		
		MenuRowPopup(
			title = stringResource(R.string.account_params_statistic_primary_value_calculation),
			value = page.statisticParams.primaryValueCalculation.labelText(),
		) {
			page.allowedCalculations.forEach { calculation ->
				PopupMenuItem(
					onClick = { listener.onStatisticPrimaryValueCalculationChanged(calculation) },
					text = calculation.nameText(),
				)
			}
		}
		
		HorizontalDivider()
		
		MenuRowPopup(
			title = stringResource(R.string.account_params_statistic_secondary_value_calculation),
			value = page.statisticParams.secondaryValueCalculation.labelText(),
		) {
			page.allowedCalculations.forEach { calculation ->
				PopupMenuItem(
					onClick = { listener.onStatisticSecondaryValueCalculationChanged(calculation) },
					text = calculation.nameText(),
				)
			}
		}
		
		HorizontalDivider()
		
		MenuRowSwitch(
			title = stringResource(R.string.account_params_statistic_show_secondary_value_for_category),
			checked = page.statisticParams.showSecondaryValueForCategory,
			onCheckedChange = listener::onStatisticShowSecondaryValueForCategoryChanged,
		)
		
		HorizontalDivider()
		
		MenuRowSwitch(
			title = stringResource(R.string.account_params_statistic_show_secondary_value_for_account),
			checked = page.statisticParams.showSecondaryValueForAccount,
			onCheckedChange = listener::onStatisticShowSecondaryValueForAccountChanged
		)
		
		HorizontalDivider()
		
		val preferences = rememberInject<AppPreferences>()
		MenuRowSwitch(
			title = stringResource(R.string.account_params_show_accounts_list_in_single_line),
			checked = preferences.accountListSingleLine.flow.collectAsState().value,
			onCheckedChange = {
				preferences.accountListSingleLine.value = it
			}
		)
	}
}