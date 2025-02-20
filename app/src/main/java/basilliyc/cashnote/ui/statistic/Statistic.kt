package basilliyc.cashnote.ui.statistic

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import basilliyc.cashnote.AppNavigation
import basilliyc.cashnote.R
import basilliyc.cashnote.ui.base.handleResult
import basilliyc.cashnote.ui.components.IconButton
import basilliyc.cashnote.ui.components.SimpleActionBar
import basilliyc.cashnote.utils.DefaultPreview
import basilliyc.cashnote.utils.ScaffoldColumn

@Composable
fun Statistic() {
	val viewModel = viewModel<StatisticViewModel>()
	Result(viewModel.state.result, viewModel)
	Page(
		page = viewModel.state.page,
		params = viewModel.state.params,
		pageType = viewModel.state.pageType,
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
	pageType: StatisticStateHolder.PageType,
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
		}
	) {
		//TODO show filters
		//TODO show pager
		//TODO show three detailed pages with PageData
//		when (page) {
//			is StatisticStateHolder.Page.Data -> PageData(page, listener)
//			StatisticStateHolder.Page.DataEmpty -> PageDataEmpty(page, listener)
//			StatisticStateHolder.Page.Loading -> PageLoading(
//				title = ,
//				showBackButton = false,
//			)
//		}
	}
	
}

@Preview(showBackground = true)
@Composable
private fun PageDataPreview() = DefaultPreview {
	Statistic()
}


@Composable
private fun PageData(
	state: StatisticStateHolder.Page.Data,
	listener: StatisticListener,
) {

}

@Composable
private fun PageDataEmpty(
	listener: StatisticListener,
) {

}


