package basilliyc.cashnote.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import basilliyc.cashnote.R
import basilliyc.cashnote.ui.theme.CashNoteTheme
import basilliyc.cashnote.utils.DefaultPreview
import basilliyc.cashnote.utils.LocalLogcat
import basilliyc.cashnote.utils.LocalNavController
import basilliyc.cashnote.utils.Logcat

class MainActivity : ComponentActivity() {
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			
			CompositionLocalProvider(
				LocalNavController provides rememberNavController(),
				LocalLogcat provides Logcat(this),
			) {
				val logcat = LocalLogcat.current
				
				val navBackStack by LocalNavController.current.currentBackStackEntryAsState()
				LaunchedEffect(navBackStack) {
					val backStackArguments = navBackStack?.arguments
					val destinationArguments = navBackStack?.destination?.arguments
					logcat.info(
						"Current destination: ${navBackStack?.destination?.route}",
						destinationArguments
							?.map { it.key + "=" + backStackArguments?.get(it.key) }
							?.joinToString() ?: ""
					)
				}
				
				CashNoteTheme {
					Main()
				}
				
			}
			
		}
		
	}
}

@Composable
private fun Main() {
	Scaffold(
		modifier = Modifier.fillMaxSize(),
		bottomBar = { MainBottomNavigationBar() }
	) { innerPadding ->
		val paddingValues = PaddingValues(
			bottom = innerPadding.calculateBottomPadding()
		)
		NavHost(
			navController = LocalNavController.current as NavHostController,
			startDestination = AppNavigation.AccountList,
			modifier = Modifier
				.padding(paddingValues)
				.consumeWindowInsets(paddingValues),
			builder = { createNavigationGraph() },
		)
	}
}

@Composable
@Preview(showBackground = true)
private fun MainPreview() = DefaultPreview {
	Main()
}

@Composable
private fun MainBottomNavigationBar() {
	
	val navController = LocalNavController.current
	val currentBackStackEntry = navController.currentBackStackEntryAsState()
	val currentDestination = currentBackStackEntry.value?.destination
	
	val entities = listOf(
		NavigationBarEntity(
			page = AppNavigation.AccountList,
			label = { Text(stringResource(R.string.main_nav_account_list)) },
			icon = {
				Icon(
					imageVector = Icons.Filled.AccountBalanceWallet,
					contentDescription = stringResource(R.string.main_nav_account_list)
				)
			}
		),
		NavigationBarEntity(
			page = AppNavigation.AccountStatistic,
			label = { Text(stringResource(R.string.main_nav_account_statistic)) },
			icon = {
				Icon(
					imageVector = Icons.Filled.QueryStats,
					contentDescription = stringResource(R.string.main_nav_account_statistic)
				)
			}
		),
		NavigationBarEntity(
			page = AppNavigation.AppSettings,
			label = { Text(stringResource(R.string.main_nav_app_settings)) },
			icon = {
				Icon(
					imageVector = Icons.Filled.Settings,
					contentDescription = stringResource(R.string.main_nav_app_settings)
				)
			}
		)
	)
	
	val isNavBarVisible = entities.any {
		currentDestination?.route == it.page::class.qualifiedName
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
