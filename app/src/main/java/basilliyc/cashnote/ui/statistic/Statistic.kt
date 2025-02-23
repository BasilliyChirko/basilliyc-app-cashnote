@file:OptIn(ExperimentalLayoutApi::class)

package basilliyc.cashnote.ui.statistic

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import basilliyc.cashnote.AppNavigation
import basilliyc.cashnote.R
import basilliyc.cashnote.ui.base.handleResult
import basilliyc.cashnote.ui.components.BoxLoading
import basilliyc.cashnote.ui.components.CardText
import basilliyc.cashnote.ui.components.IconButton
import basilliyc.cashnote.ui.components.SimpleActionBar
import basilliyc.cashnote.ui.stringName
import basilliyc.cashnote.ui.theme.colorGrey99
import basilliyc.cashnote.utils.DefaultPreview
import basilliyc.cashnote.utils.ScaffoldColumn
import basilliyc.cashnote.utils.toPriceString

@Composable
fun Statistic() {
	val viewModel = viewModel<StatisticViewModel>()
	Result(viewModel.state.result, viewModel)
	Page(
		page = viewModel.state.page,
		params = viewModel.state.params,
		listener = viewModel
	)
}

@Composable
private fun Result(
	result: StatisticStateHolder.Result?,
	listener: StatisticListener,
) {
	handleResult(result, listener) {
		when (it) {
			is StatisticStateHolder.Result.NavigateStatisticParams -> {
				navigateForward(AppNavigation.StatisticParams)
			}
		}
	}
}

@Composable
fun Page(
	page: StatisticStateHolder.Page,
	params: StatisticStateHolder.Params,
	listener: StatisticListener,
) {
	ScaffoldColumn(
		topBar = {
			SimpleActionBar(
				navigationIcon = {},
				title = stringResource(R.string.statistic_title),
				actions = {
					IconButton(
						imageVector = Icons.Filled.FilterAlt,
						onClick = listener::onParamsClicked,
						contentDescription = stringResource(R.string.statistic_params)
					)
				}
			)
		},
		content = {
			
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.Center,
			) {
				CardText(
					text = params.selectedPeriod.stringName,
					onClick = listener::onParamsClicked,
				)
				Spacer(Modifier.width(8.dp))
				CardText(
					text = params.currency.name,
					onClick = listener::onParamsClicked,
				)
				Spacer(Modifier.width(8.dp))
				CardText(
					text = when (params.accounts.size) {
						0 -> stringResource(R.string.statistic_params_account_not_selected)
						1 -> stringResource(R.string.statistic_params_account_one)
						else -> stringResource(
							R.string.statistic_params_account_many,
							params.accounts.size
						)
					},
					onClick = listener::onParamsClicked,
				)
			}
			
			//TODO show tab bar with tabs: balance, income, expense
			
			val pagerState = rememberPagerState(
				pageCount = { 3 },
			)
			HorizontalPager(
				state = pagerState,
			) {
				when (page) {
					is StatisticStateHolder.Page.Data -> when (it) {
						0 -> PageDataBalance(page, params, listener)
//						1 -> PageDataIncome(page,params, listener)
//						2 -> PageDataExpense(page, params,listener)
						else -> Unit
					}
					
					StatisticStateHolder.Page.Loading -> BoxLoading()
					is StatisticStateHolder.Page.LoadingError -> PageLoadingError(page)
				}
			}
		}
	)
}

@Preview(showBackground = true)
@Composable
private fun PageDataPreview() = DefaultPreview {
	Statistic()
}


@Composable
private fun PageLoadingError(
	page: StatisticStateHolder.Page.LoadingError,
) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(16.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center,
	) {
		Text(text = stringResource(R.string.statistic_loading_error_title))
		page.throwable.message?.let {
			Text(text = it)
		}
	}
}


@Composable
private fun PageDataBalance(
	page: StatisticStateHolder.Page.Data,
	params: StatisticStateHolder.Params,
	listener: StatisticListener,
) {
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.verticalScroll(rememberScrollState())
	) {
		Row(modifier = Modifier.padding(16.dp)) {
			Text(
				text = stringResource(R.string.statistic_current_balance),
				modifier = Modifier.weight(1F),
				style = MaterialTheme.typography.titleLarge,
			)
			Text(
				text = page.totalBalance.toPriceString(false) + " " + params.currency.symbol,
				modifier = Modifier.padding(start = 8.dp),
				style = MaterialTheme.typography.titleLarge,
			)
		}
		
		
		//Titles
		RowTitles(
			titles = arrayOf(
				"",
				stringResource(R.string.statistic_balance),
				stringResource(R.string.statistic_profit),
			)
		)
		var balance = page.totalBalance
			//Values
		page.values.entries.sortedByDescending { it.key }.forEach { (month, categoryMap) ->
			val profit = categoryMap.values.sumOf { it.income + it.expense }
			RowValues(
				values = arrayOf(
					month.stringName.invoke(),
					balance.toPriceString(false, false),
					profit.toPriceString(true, false),
				)
			)
			balance -= profit
		}
		
	}
}


@Composable
private fun RowTitles(
	modifier: Modifier = Modifier,
	titles: Array<String>,
) {
	Row(modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
		titles.forEach {
			Text(
				text = it,
				modifier = Modifier.weight(1F),
				textAlign = TextAlign.End,
				color = colorGrey99,
			)
		}
	}
}

@Composable
private fun RowValues(
	modifier: Modifier = Modifier,
	values: Array<String>,
	endingIcon: @Composable (() -> Unit)? = null,
) {
	Row(modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
		values.forEachIndexed { index, it ->
			Text(
				text = it,
				modifier = Modifier.weight(1F),
				textAlign = if (index == 0) TextAlign.Start else TextAlign.End,
			)
		}
		endingIcon?.let {
			Box(modifier = Modifier.weight(1F)) {
				endingIcon()
			}
		}
	}
}

//@Composable
//private fun PageDataIncome(
//	page: StatisticStateHolder.Page.Data,
//	listener: StatisticListener,
//) {
//
//}
//
//@Composable
//private fun PageDataExpense(
//	page: StatisticStateHolder.Page.Data,
//	listener: StatisticListener,
//) {
//
//}



