@file:OptIn(ExperimentalLayoutApi::class)

package basilliyc.cashnote.ui.statistic

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.ArrowDropUp
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import basilliyc.cashnote.R
import basilliyc.cashnote.backend.manager.FinancialManager
import basilliyc.cashnote.backend.preferences.StatisticPreferences
import basilliyc.cashnote.backend.preferences.base.toggle
import basilliyc.cashnote.data.FinancialCategory
import basilliyc.cashnote.data.FinancialCurrency
import basilliyc.cashnote.data.StatisticMonth
import basilliyc.cashnote.data.StatisticSelectedPeriod
import basilliyc.cashnote.ui.base.handleResult
import basilliyc.cashnote.ui.components.BoxLoading
import basilliyc.cashnote.ui.components.CardText
import basilliyc.cashnote.ui.components.ColumnChart
import basilliyc.cashnote.ui.components.IconButton
import basilliyc.cashnote.ui.components.ItemVisibilitySelectable
import basilliyc.cashnote.ui.components.LineChart
import basilliyc.cashnote.ui.components.PopupMenu
import basilliyc.cashnote.ui.components.PopupMenuItem
import basilliyc.cashnote.ui.components.SimpleActionBar
import basilliyc.cashnote.ui.components.VerticalGrid
import basilliyc.cashnote.ui.components.VerticalGridCells
import basilliyc.cashnote.ui.components.rememberPopupState
import basilliyc.cashnote.ui.stringName
import basilliyc.cashnote.ui.stringNameFull
import basilliyc.cashnote.ui.stringNameMonth
import basilliyc.cashnote.ui.theme.colorGrey99
import basilliyc.cashnote.utils.DefaultPreview
import basilliyc.cashnote.utils.ScaffoldColumn
import basilliyc.cashnote.utils.applyIf
import basilliyc.cashnote.utils.inject
import basilliyc.cashnote.utils.toPercent
import basilliyc.cashnote.utils.toPriceColor
import basilliyc.cashnote.utils.toPriceString
import java.util.Calendar
import kotlin.collections.component1
import kotlin.collections.component2
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
	val preferences by remember { inject<StatisticPreferences>() }
	ScaffoldColumn(
		topBar = {
			SimpleActionBar(
				navigationIcon = {},
				title = stringResource(R.string.statistic_title),
				actions = {
					IconButton(
						imageVector = Icons.Outlined.FilterAlt,
						contentDescription = stringResource(R.string.statistic_filters),
						onClick = { preferences.showFilters.toggle() }
					)
				}
			)
		},
		content = {
			
			
			//TODO show tab bar with tabs: balance, income, expense
			
			val pagerState = rememberPagerState(
				pageCount = { 3 },
			)
			
			if (preferences.showFilters.collectAsState()) {
				RowFilters()
			}
			
			HorizontalPager(
				modifier = Modifier.fillMaxSize(),
				state = pagerState,
				beyondViewportPageCount = 3
			) {
				when (page) {
					is StatisticStateHolder.Page.Data -> {
						
						when (it) {
							0 -> PageDataBalance(page, params)
							1 -> PageDataIncome(page, params)
							2 -> PageDataExpense(page, params)
							else -> Unit
						}
					}
					
					is StatisticStateHolder.Page.Loading -> BoxLoading()
					is StatisticStateHolder.Page.LoadingError -> PageLoadingError(page)
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
	val selectedAccountIds = preferences.accountIds.collectAsState()
	val categories by financialManager.getCategoryListAsFlow().collectAsState(emptyList())
	val selectedCategoryIds = preferences.categoryIds.collectAsState()
	
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
					text = preferences.selectedPeriod.collectAsState().stringName,
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
					text = preferences.currency.collectAsState().name,
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
) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.verticalScroll(rememberScrollState()),
	) {
		val currentMonth = StatisticMonth(Calendar.getInstance())
		val entryCurrent = page.values[currentMonth]
		val entriesAll =
			page.values.entries.sortedByDescending { it.key }.filter { it.key != currentMonth }
		val profitCurrent = entryCurrent?.values?.sumOf { it.profit } ?: 0.0
		
		
		val monthlyProfit = entriesAll.let {
			it.sumOf { it.value.values.sumOf { it.profit } } / it.size
		}
		RowHeadline(
			modifier = Modifier.padding(top = 8.dp),
			title = stringResource(R.string.statistic_current_balance),
			value = page.totalBalance.toPriceString(false, true, params.currency),
			textStyle = MaterialTheme.typography.titleLarge,
		)
		RowHeadline(
			title = stringResource(R.string.statistic_average_profit),
			value = monthlyProfit.toPriceString(true, true, params.currency),
			textStyle = MaterialTheme.typography.titleMedium,
			valueColor = monthlyProfit.toPriceColor()
		)
		RowHeadline(
			title = stringResource(R.string.statistic_current_profit),
			value = profitCurrent.toPriceString(true, true, params.currency),
			textStyle = MaterialTheme.typography.titleMedium,
		)
		
		if (entriesAll.size > 1) {
			
			HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
			Text(
				modifier = Modifier.padding(start = 16.dp),
				text = stringResource(R.string.statistic_profit_by_month),
				style = MaterialTheme.typography.titleMedium,
			)
			ColumnChart(
				data = entriesAll.map { (month, categoryMap) ->
					month.stringNameMonth to categoryMap.values.sumOf { it.profit }
				}.reversed()
			)
			
			HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
			Text(
				modifier = Modifier.padding(start = 16.dp),
				text = stringResource(R.string.statistic_balance_by_month),
				style = MaterialTheme.typography.titleMedium,
			)
			LineChart(
				data = let {
					var balance = page.totalBalance - profitCurrent
					entriesAll.map { (month, categoryMap) ->
						val profit = categoryMap.values.sumOf { it.profit }
						(month.stringNameMonth to balance).also { balance -= profit }
					}.reversed()
				}
			)
			
		}
		
		HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
		
		RowTableTitle(
			titles = listOf(
				stringResource(R.string.statistic_month),
				stringResource(R.string.statistic_balance),
				stringResource(R.string.statistic_profit),
			)
		)
		var balance = page.totalBalance - profitCurrent
		entriesAll.forEach { (month, categoryMap) ->
			val profit = categoryMap.values.sumOf { it.profit }
			RowTableValue(
				values = listOf(
					month.stringNameFull,
					balance.toPriceString(false, false, params.currency),
					profit.toPriceString(true, false, params.currency),
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
) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.verticalScroll(rememberScrollState()),
	) {
		val entriesAll = page.values.entries.sortedByDescending { it.key }
			.map { it.key to it.value.filter { it.key.id in params.categoryIds && it.value.income > 0.0 } }
		
		//Total values for all period
		val totalIncome = entriesAll.sumOf { it.second.values.sumOf { it.income } }
		val averageIncome = totalIncome / entriesAll.size
		RowHeadline(
			modifier = Modifier.padding(top = 8.dp),
			title = stringResource(R.string.statistic_total_income),
			value = totalIncome.toPriceString(true, true, params.currency),
			textStyle = MaterialTheme.typography.titleLarge,
			valueColor = totalIncome.toPriceColor()
		)
		RowHeadline(
			title = stringResource(R.string.statistic_average_income),
			value = averageIncome.toPriceString(true, true, params.currency),
			textStyle = MaterialTheme.typography.titleMedium,
			valueColor = averageIncome.toPriceColor()
		)
		
		if (entriesAll.size > 1) {
			HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
			Text(
				modifier = Modifier.padding(start = 16.dp),
				text = stringResource(R.string.statistic_income_by_month),
				style = MaterialTheme.typography.titleMedium,
			)
			ColumnChart(
				data = entriesAll.map { (month, categoryMap) ->
					month.stringNameMonth to categoryMap.values.sumOf { it.income }
				}.reversed()
			)
		}
		
		//total values for each category for all period
		val categoryTotal = HashMap<FinancialCategory, Double>()
		entriesAll.forEach { (month, categoryMap) ->
			categoryMap.forEach {
				categoryTotal[it.key] = (categoryTotal[it.key] ?: 0.0) + it.value.income
			}
		}
		RowTableTitle(
			titles = listOf(
				stringResource(R.string.statistic_category),
				stringResource(R.string.statistic_total),
				stringResource(R.string.statistic_percent),
				stringResource(R.string.statistic_average),
			)
		)
		categoryTotal.toList().sortedByDescending { it.second }.forEach { (category, total) ->
			RowTableValue(
				values = listOf(
					category.name,
					total.toPriceString(true, false, params.currency),
					(total / totalIncome).toPercent(),
					(total / entriesAll.size).toPriceString(
						true,
						false
					) + " " + params.currency.symbol,
				),
			)
		}
		
		HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
		
		
		//Values for each month
		RowTableTitle(
			titles = listOf(
				stringResource(R.string.statistic_month),
				stringResource(R.string.statistic_income),
			),
			endingIcon = true
		)
		entriesAll.forEach { (month, categoryMap) ->
			val income = categoryMap.values.sumOf { it.income }
			val popupState = rememberPopupState()
			RowTableValue(
				values = listOf(
					month.stringNameFull,
					income.toPriceString(true, false, params.currency),
				),
				endingIcon = if (popupState.isExpanded) Icons.Outlined.ArrowDropUp else Icons.Outlined.ArrowDropDown,
				onClick = { popupState.toggle() }
			)
			
			if (popupState.isExpanded) {
				Column(
					modifier = Modifier.padding(start = 16.dp)
				) {
					RowTableTitle(
						titles = listOf(
							stringResource(R.string.statistic_category),
							stringResource(R.string.statistic_percent),
							stringResource(R.string.statistic_income),
						)
					)
					categoryMap.toList().sortedByDescending { it.second.income }
						.forEach { (category, value) ->
							RowTableValue(
								values = listOf(
									category.name,
									(value.income / income).toPercent(),
									value.income.toPriceString(
										true,
										false,
										params.currency
									)
								),
							)
						}
				}
				HorizontalDivider(modifier = Modifier.padding(top = 8.dp, start = 16.dp))
				
			}
			
		}
		
		
	}
	
}

@Composable
private fun ColumnScope.PageDataExpense(
	page: StatisticStateHolder.Page.Data,
	params: StatisticStateHolder.Params,
) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.verticalScroll(rememberScrollState()),
	) {
		val entriesAll = page.values.entries.sortedByDescending { it.key }
			.map { it.key to it.value.filter { it.key.id in params.categoryIds && it.value.expense < 0.0 } }
		
		val totalExpense = entriesAll.sumOf { it.second.values.sumOf { it.expense } }
		val averageExpense = totalExpense / entriesAll.size
		RowHeadline(
			modifier = Modifier.padding(top = 8.dp),
			title = stringResource(R.string.statistic_total_expense),
			value = totalExpense.toPriceString(true, true, params.currency),
			textStyle = MaterialTheme.typography.titleLarge,
			valueColor = totalExpense.toPriceColor()
		)
		RowHeadline(
			title = stringResource(R.string.statistic_average_expense),
			value = averageExpense.toPriceString(true, true, params.currency),
			textStyle = MaterialTheme.typography.titleMedium,
			valueColor = averageExpense.toPriceColor()
		)
		
		if (entriesAll.size > 1) {
			HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
			Text(
				modifier = Modifier.padding(start = 16.dp),
				text = stringResource(R.string.statistic_expense_by_month),
				style = MaterialTheme.typography.titleMedium,
			)
			ColumnChart(
				data = entriesAll.map { (month, categoryMap) ->
					month.stringNameMonth to categoryMap.values.sumOf { it.expense }
				}.reversed()
			)
		}
		
		//total values for each category for all period
		val categoryTotal = HashMap<FinancialCategory, Double>()
		entriesAll.forEach { (month, categoryMap) ->
			categoryMap.forEach {
				categoryTotal[it.key] = (categoryTotal[it.key] ?: 0.0) + it.value.expense
			}
		}
		RowTableTitle(
			titles = listOf(
				stringResource(R.string.statistic_category),
				stringResource(R.string.statistic_total),
				stringResource(R.string.statistic_percent),
				stringResource(R.string.statistic_average),
			)
		)
		categoryTotal.toList().sortedBy { it.second }.forEach { (category, total) ->
			RowTableValue(
				values = listOf(
					category.name,
					total.toPriceString(true, false, params.currency),
					(total / totalExpense).toPercent(),
					(total / entriesAll.size).toPriceString(
						true,
						false
					) + " " + params.currency.symbol,
				),
			)
		}
		
		HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
		
		//Values for each month
		RowTableTitle(
			titles = listOf(
				stringResource(R.string.statistic_month),
				stringResource(R.string.statistic_expense),
			),
			endingIcon = true
		)
		entriesAll.forEach { (month, categoryMap) ->
			val expense = categoryMap.values.sumOf { it.expense }
			val popupState = rememberPopupState()
			RowTableValue(
				values = listOf(
					month.stringNameFull,
					expense.toPriceString(true, false, params.currency),
				),
				endingIcon = if (popupState.isExpanded) Icons.Outlined.ArrowDropUp else Icons.Outlined.ArrowDropDown,
				onClick = { popupState.toggle() }
			)
			
			if (popupState.isExpanded) {
				Column(
					modifier = Modifier.padding(start = 16.dp)
				) {
					RowTableTitle(
						titles = listOf(
							stringResource(R.string.statistic_category),
							stringResource(R.string.statistic_percent),
							stringResource(R.string.statistic_expense),
						)
					)
					categoryMap.toList().sortedBy { it.second.expense }
						.forEach { (category, value) ->
							RowTableValue(
								values = listOf(
									category.name,
									(value.expense / expense).toPercent(),
									value.expense.toPriceString(
										true,
										false,
										params.currency
									)
								),
							)
						}
				}
				HorizontalDivider(modifier = Modifier.padding(top = 8.dp, start = 16.dp))
				
			}
			
		}
		
	}
}

@Composable
private fun RowHeadline(
	modifier: Modifier = Modifier,
	title: String,
	value: String,
	valueColor: Color = Color.Unspecified,
	textStyle: TextStyle,
) {
	Row(modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
		Text(
			text = title,
			modifier = Modifier.weight(1F),
			style = textStyle,
		)
		Text(
			text = value,
			modifier = Modifier.padding(start = 8.dp),
			style = textStyle,
			color = valueColor,
		)
	}
}

@Composable
private fun RowTableTitle(
	modifier: Modifier = Modifier,
	titles: List<String>,
	endingIcon: Boolean = false,
) {
	Row(modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
		titles.forEachIndexed { index, it ->
			Text(
				text = it,
				modifier = Modifier.weight(1F),
				textAlign = if (index == 0) TextAlign.Start else TextAlign.End,
				color = colorGrey99,
			)
		}
		if (endingIcon) {
			Spacer(Modifier.width(32.dp))
		}
	}
}

@Composable
private fun RowTableValue(
	modifier: Modifier = Modifier,
	values: List<String>,
	colors: List<Color> = values.map { Color.Unspecified },
	endingIcon: ImageVector? = null,
	onClick: (() -> Unit)? = null,
) {
	Box(
		modifier = Modifier.applyIf(onClick != null) { clickable { onClick?.invoke() } }
	) {
		Row(
			modifier = modifier
				.padding(horizontal = 16.dp, vertical = 4.dp)
		) {
			values.forEachIndexed { index, it ->
				Text(
					text = it,
					modifier = Modifier.weight(1F),
					textAlign = if (index == 0) TextAlign.Start else TextAlign.End,
					color = colors[index],
				)
			}
			endingIcon?.let {
				Icon(
					imageVector = it,
					contentDescription = null,
					modifier = Modifier.padding(start = 8.dp)
				)
			}
		}
	}
}

