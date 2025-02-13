package basilliyc.cashnote.ui.main

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import basilliyc.cashnote.AppNavigation
import basilliyc.cashnote.NavigationBarEntity
import basilliyc.cashnote.NavigationBarItem
import basilliyc.cashnote.R
import basilliyc.cashnote.createNavigationGraph
import basilliyc.cashnote.data.FinancialAccount
import basilliyc.cashnote.toAppNavigationPath
import basilliyc.cashnote.ui.theme.CashNoteTheme
import basilliyc.cashnote.utils.LocalNavController


@Composable
fun Main() {
	
	val viewModel = viewModel<MainViewModel>()
	val state by viewModel.state.collectAsState()
	
	val startDestination = state.accountOnNavigation?.let {
		AppNavigation.AccountDetails(
			accountId = it.id,
			isFromNavigation = true,
		)
	} ?: AppNavigation.AccountList
	
	
	CashNoteTheme(
		themeMode = state.themeMode
	) {
		
		Scaffold(
			modifier = Modifier.fillMaxSize(),
			bottomBar = {
				MainBottomNavigationBar(
					accountOnNavigation = state.accountOnNavigation
				)
			}
		) { innerPadding ->
			val paddingValues = PaddingValues(
				bottom = innerPadding.calculateBottomPadding()
			)
			NavHost(
				modifier = Modifier
					.padding(paddingValues)
					.consumeWindowInsets(paddingValues),
				navController = LocalNavController.current as NavHostController,
				startDestination = startDestination,
				builder = { createNavigationGraph() },
				enterTransition = {
					fadeIn(animationSpec = tween(300))
				},
				exitTransition = {
					fadeOut(animationSpec = tween(300))
				},
			)
		}
		
		if (state.isNeedRestartActivity) {
			RestartActivityDialog()
		}
		
	}
	
}

@Composable
private fun MainBottomNavigationBar(
	accountOnNavigation: FinancialAccount?,
) {
	
	val navController = LocalNavController.current
	val currentBackStackEntry by navController.currentBackStackEntryAsState()
	
	val entities = listOf(
		accountOnNavigation?.let { account ->
			NavigationBarEntity(
				page = AppNavigation.AccountDetails(
					accountId = account.id,
					isFromNavigation = true,
				),
				label = {
					Text(
						text = account.name,
						textAlign = TextAlign.Center,
					)
				},
				icon = {
					Icon(
						imageVector = Icons.Filled.AccountBalance,
						contentDescription = account.name,
					)
				}
			)
		},
		NavigationBarEntity(
			page = AppNavigation.AccountList,
			label = {
				Text(
					text = stringResource(R.string.main_nav_account_list),
					textAlign = TextAlign.Center,
				)
			},
			icon = {
				Icon(
					imageVector = Icons.Filled.AccountBalanceWallet,
					contentDescription = stringResource(R.string.main_nav_account_list)
				)
			}
		),
		NavigationBarEntity(
			page = AppNavigation.TransactionHistory(
				accountId = null,
				isFromNavigation = true,
			),
			label = {
				Text(
					text = stringResource(R.string.main_nav_transaction_history),
					textAlign = TextAlign.Center,
				)
			},
			icon = {
				Icon(
					imageVector = Icons.Filled.History,
					contentDescription = stringResource(R.string.main_nav_transaction_history)
				)
			}
		),
		NavigationBarEntity(
			page = AppNavigation.Statistic,
			label = {
				Text(
					text = stringResource(R.string.main_nav_account_statistic),
					textAlign = TextAlign.Center,
				)
			},
			icon = {
				Icon(
					imageVector = Icons.Filled.QueryStats,
					contentDescription = stringResource(R.string.main_nav_account_statistic)
				)
			}
		),
		NavigationBarEntity(
			page = AppNavigation.Settings,
			label = {
				Text(
					text = stringResource(R.string.main_nav_app_settings),
					textAlign = TextAlign.Center,
				)
			},
			icon = {
				Icon(
					imageVector = Icons.Filled.Settings,
					contentDescription = stringResource(R.string.main_nav_app_settings)
				)
			}
		)
	).filterNotNull()
	
	val isNavBarVisible = entities.any {
		it.page.toString() == currentBackStackEntry?.toAppNavigationPath()
	}
	
	AnimatedVisibility(
		visible = isNavBarVisible,
		enter = fadeIn(),
		exit = fadeOut(),
	) {
		NavigationBar(
			modifier = Modifier.fillMaxWidth()
		) {
			entities.forEach { NavigationBarItem(it) }
		}
	}
}


@Composable
private fun RestartActivityDialog() {
	val context = LocalContext.current
	AlertDialog(
		onDismissRequest = { context.restartActivity() },
		title = {
			Text(text = stringResource(R.string.main_restart_activity_dialog_title))
		},
		text = {
			Text(text = stringResource(R.string.main_restart_activity_dialog_text))
		},
		confirmButton = {
			TextButton(
				onClick = { context.restartActivity() },
				content = {
					Text(text = stringResource(android.R.string.ok))
				}
			)
		}
	)
}

private fun Context.restartActivity() {
	val context = this
	val packageManager: PackageManager = context.packageManager
	val intent: Intent = packageManager.getLaunchIntentForPackage(context.packageName)!!
	intent.flags += Intent.FLAG_ACTIVITY_CLEAR_TOP
	context.startActivity(intent)
}
