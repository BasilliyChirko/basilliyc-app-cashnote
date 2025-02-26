@file:OptIn(ExperimentalLayoutApi::class)

package basilliyc.cashnote.ui.statistic

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import basilliyc.cashnote.R
import basilliyc.cashnote.backend.manager.FinancialManager
import basilliyc.cashnote.backend.preferences.StatisticPreferences
import basilliyc.cashnote.data.FinancialCurrency
import basilliyc.cashnote.data.StatisticMonth
import basilliyc.cashnote.data.StatisticSelectedPeriod
import basilliyc.cashnote.ui.base.handleResult
import basilliyc.cashnote.ui.components.BoxLoading
import basilliyc.cashnote.ui.components.CardText
import basilliyc.cashnote.ui.components.ItemVisibilitySelectable
import basilliyc.cashnote.ui.components.PopupMenu
import basilliyc.cashnote.ui.components.PopupMenuItem
import basilliyc.cashnote.ui.components.VerticalGrid
import basilliyc.cashnote.ui.components.VerticalGridCells
import basilliyc.cashnote.ui.stringName
import basilliyc.cashnote.ui.theme.colorGrey99
import basilliyc.cashnote.utils.DefaultPreview
import basilliyc.cashnote.utils.ScaffoldColumn
import basilliyc.cashnote.utils.inject
import basilliyc.cashnote.utils.toPriceColor
import basilliyc.cashnote.utils.toPriceString
import java.util.Calendar
import kotlin.getValue

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
//		when (it) {
//		}
	}
}

@Composable
private fun Page(
	page: StatisticStateHolder.Page,
	params: StatisticStateHolder.Params,
	listener: StatisticListener,
) {
	ScaffoldColumn(
		topBar = {
//			SimpleActionBar(
//				navigationIcon = {},
//				title = stringResource(R.string.statistic_title),
//			)
		},
		content = {
			
			
			//TODO show tab bar with tabs: balance, income, expense
			
			val pagerState = rememberPagerState(
				pageCount = { 3 },
			)
			
			Column(
				modifier = Modifier
					.fillMaxSize()
					.verticalScroll(rememberScrollState())
			) {
				RowFilters()
				HorizontalPager(
					modifier = Modifier.fillMaxSize(),
					state = pagerState,
				) {
					when (page) {
						is StatisticStateHolder.Page.Data -> {
							
							when (it) {
								0 -> PageDataBalance(page, params, listener)
								1 -> PageDataIncome(page, params, listener)
//						2 -> PageDataExpense(page, params,listener)
								else -> Unit
							}
						}
						
						is StatisticStateHolder.Page.Loading -> BoxLoading()
						is StatisticStateHolder.Page.LoadingError -> PageLoadingError(page)
					}
				}
			}
		}
	)
}

@Composable
private fun RowFilters() {
	val preferences by remember { inject<StatisticPreferences>() }
	val financialManager by remember { inject<FinancialManager>() }
	val accounts by financialManager.getAccountListAsFlow().collectAsState(emptyList())
	val selectedAccountIds = preferences.accountIds.collectValue()
	val categories by financialManager.getCategoryListAsFlow().collectAsState(emptyList())
	val selectedCategoryIds = preferences.categoryIds.collectValue()
	
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.Center,
	) {
		
		PopupMenu(
			anchor = {
				CardText(
					text = when (val count = selectedAccountIds.size) {
						accounts.size -> stringResource(R.string.statistic_params_account_all)
						0 -> stringResource(R.string.statistic_params_account_not_selected)
						1 -> stringResource(R.string.statistic_params_account_one)
						else -> stringResource(
							R.string.statistic_params_account_many,
							count
						)
					},
					onClick = { expand() }
				)
			},
			items = {
				VerticalGrid(
					modifier = Modifier
						.sizeIn(
							maxWidth = 300.dp,
							maxHeight = 400.dp
						)
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
		)
		
		Spacer(Modifier.width(8.dp))
		
		PopupMenu(
			anchor = {
				CardText(
					text = when (val count = selectedCategoryIds.size) {
						categories.size -> stringResource(R.string.statistic_params_category_all)
						0 -> stringResource(R.string.statistic_params_category_not_selected)
						1 -> stringResource(R.string.statistic_params_category_one)
						else -> stringResource(
							R.string.statistic_params_category_many,
							count
						)
					},
					onClick = { expand() }
				)
			},
			items = {
				VerticalGrid(
					modifier = Modifier
						.sizeIn(
							maxWidth = 300.dp,
							maxHeight = 400.dp
						)
						.verticalScroll(rememberScrollState())
						.padding(8.dp),
					columns = VerticalGridCells.Fixed(2),
					itemsCount = categories.size,
					verticalSpace = 8.dp,
					horizontalSpace = 8.dp,
					content = {
						val category = categories[it]
						ItemVisibilitySelectable(
							title = category.name,
							icon = {
								if (category.icon != null) {
									Icon(
										imageVector = category.icon.imageVector,
										contentDescription = category.name
									)
								}
							},
							isSelected = category.id in selectedCategoryIds,
							onClick = {
								preferences.categoryIds.update {
									it.toMutableList().apply {
										if (category.id in this) {
											remove(category.id)
										} else {
											add(category.id)
										}
									}
								}
							}
						)
					}
				)
			}
		)
	}
	
	
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.Center,
	) {
		
		PopupMenu(
			anchor = {
				CardText(
					text = preferences.selectedPeriod.collectValue().stringName,
					onClick = { expand() },
				)
			},
			items = {
				StatisticSelectedPeriod.entries.forEach {
					PopupMenuItem(
						text = it.stringName,
						onClick = { preferences.selectedPeriod.set(it) }
					)
				}
			}
		)
		
		Spacer(Modifier.width(8.dp))
		
		PopupMenu(
			anchor = {
				CardText(
					text = preferences.currency.collectValue().name,
					onClick = { expand() },
				)
			},
			items = {
				FinancialCurrency.entries.forEach {
					PopupMenuItem(
						text = it.name,
						onClick = { preferences.currency.set(it) }
					)
				}
			}
		)
	}
	
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
private fun ColumnScope.PageDataBalance(
	page: StatisticStateHolder.Page.Data,
	params: StatisticStateHolder.Params,
	listener: StatisticListener,
) {
	Column(
		modifier = Modifier
			.fillMaxSize()
//			.verticalScroll(rememberScrollState())
	) {
		val currentMonth = StatisticMonth(Calendar.getInstance())
		val entryCurrent = page.values[currentMonth]
		val entriesAll =
			page.values.entries.sortedByDescending { it.key }.filter { it.key != currentMonth }
		val profitCurrent = entryCurrent?.values?.sumOf { it.profit } ?: 0.0
		
		Spacer(modifier = Modifier.height(8.dp))
		Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
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
		Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
			Text(
				text = stringResource(R.string.statistic_current_profit),
				modifier = Modifier.weight(1F),
				style = MaterialTheme.typography.titleMedium,
			)
			Text(
				text = profitCurrent.toPriceString(true) + " " + params.currency.symbol,
				modifier = Modifier.padding(start = 8.dp),
				style = MaterialTheme.typography.titleMedium,
				color = profitCurrent.toPriceColor()
			)
		}
		
		val monthlyProfit = entriesAll.let {
			it.sumOf { it.value.values.sumOf { it.profit } } / it.size
		}
		Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
			Text(
				text = stringResource(R.string.statistic_average_profit),
				modifier = Modifier.weight(1F),
				style = MaterialTheme.typography.titleMedium,
			)
			Text(
				text = monthlyProfit.toPriceString(true) + " " + params.currency.symbol,
				modifier = Modifier.padding(start = 8.dp),
				style = MaterialTheme.typography.titleMedium,
				color = monthlyProfit.toPriceColor()
			)
		}
		
		
		RowTitles(
			titles = listOf(
				"",
				stringResource(R.string.statistic_balance),
				stringResource(R.string.statistic_profit),
			)
		)
		var balance = page.totalBalance - profitCurrent
		entriesAll.forEach { (month, categoryMap) ->
			val profit = categoryMap.values.sumOf { it.profit }
			RowValues(
				values = listOf(
					month.stringName.invoke(),
					balance.toPriceString(false, false) + " ${params.currency.symbol}",
					profit.toPriceString(true, false) + " ${params.currency.symbol}",
				),
				colors = listOf(
					Color.Unspecified,
					Color.Unspecified,
					profit.toPriceColor(),
				)
			)
			balance -= profit
		}
		
	}
}

@Composable
private fun ColumnScope.PageDataIncome(
	page: StatisticStateHolder.Page.Data,
	params: StatisticStateHolder.Params,
	listener: StatisticListener,
) {
//	Row(modifier = Modifier.padding(16.dp)) {
//		Text(
//			text = stringResource(R.string.statistic_current_balance),
//			modifier = Modifier.weight(1F),
//			style = MaterialTheme.typography.titleLarge,
//		)
//		Text(
//			text = page.totalBalance.toPriceString(false) + " " + params.currency.symbol,
//			modifier = Modifier.padding(start = 8.dp),
//			style = MaterialTheme.typography.titleLarge,
//		)
//	}
//
//
//	//Titles
//	RowTitles(
//		titles = arrayOf(
//			"",
//			stringResource(R.string.statistic_balance),
//			stringResource(R.string.statistic_profit),
//		)
//	)
//	var balance = page.totalBalance
//	//Values
//	page.values.entries.sortedByDescending { it.key }.forEach { (month, categoryMap) ->
//		val profit = categoryMap.values.sumOf { it.income + it.expense }
//		balance -= profit
//		RowValues(
//			values = arrayOf(
//				month.stringName.invoke(),
//				balance.toPriceString(false, false) + " ${params.currency.symbol}",
//				profit.toPriceString(true, false) + " ${params.currency.symbol}",
//			)
//		)
//	}
//
}
//
//@Composable
//private fun ColumnScope.PageDataExpense(
//	page: StatisticStateHolder.Page.Data,
//params: StatisticStateHolder.Params,
//	listener: StatisticListener,
//) {
//
//}


@Composable
private fun RowTitles(
	modifier: Modifier = Modifier,
	titles: List<String>,
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
	values: List<String>,
	colors: List<Color> = values.map { Color.Unspecified },
	endingIcon: @Composable (() -> Unit)? = null,
) {
	Row(modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
		values.forEachIndexed { index, it ->
			Text(
				text = it,
				modifier = Modifier.weight(1F),
				textAlign = if (index == 0) TextAlign.Start else TextAlign.End,
				color = colors[index],
			)
		}
		endingIcon?.let {
			Box(modifier = Modifier.weight(1F)) {
				endingIcon()
			}
		}
	}
}

