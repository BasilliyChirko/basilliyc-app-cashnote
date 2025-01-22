package basilliyc.cashnote.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.runtime.setValue

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
					
					Surface(
						color = MaterialTheme.colorScheme.background,
					) {
						Column {
							NavHost(
								navController = LocalNavController.current as NavHostController,
								startDestination = AppNavigation.AccountList,
								modifier = Modifier.weight(1F),
								builder = { createNavigationGraph() },
							)
							MainBottomNavigationBar()
						}
					}

//					Scaffold(
//						modifier = Modifier.fillMaxSize(),
//						bottomBar = { MainBottomNavigationBar() }
//					) { innerPadding ->
//						NavHost(
//							navController = LocalNavController.current as NavHostController,
//							startDestination = AppNavigation.AccountList,
//							modifier = Modifier.padding(innerPadding),
//							builder = { createNavigationGraph() },
//						)
//					}
				
				}
				
			}
			
		}
		
	}
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
	
	if (isNavBarVisible) {
		NavigationBar(
			modifier = Modifier.fillMaxWidth()
		) {
			entities.forEach { NavigationBarItem(it) }
		}
	}
}

@Composable
@Preview(showBackground = true)
fun MainBottomNavigationBarPreview() = DefaultPreview {
	MainBottomNavigationBar()
}


//
//@Composable
//fun Page1(
//	argument: AppNavigation.AccountList,
//) {
//	val navController = LocalNavController.current
//	val number = navController.getResult<Int>()
//	Column {
//		Text(text = "Hello Page! $number", modifier = Modifier)
//		Button(
//			onClick = {
//				navController.navigate(AppNavigation.PageTest3(55))
//			}
//		) {
//			Text(text = "Go to page 2")
//		}
//	}
//}
//
//@Composable
//fun Page2(argument: AppNavigation.Page2) {
//	Column(modifier = Modifier.padding(16.dp)) {
//		Text("NUMBER ${argument.number}", style = MaterialTheme.typography.displayLarge)
//		Text("displayLarge", style = MaterialTheme.typography.displayLarge)
//		Text("displayMedium", style = MaterialTheme.typography.displayMedium)
//		Text("displaySmall", style = MaterialTheme.typography.displaySmall)
//		Text("headlineLarge", style = MaterialTheme.typography.headlineLarge)
//		Text("headlineMedium", style = MaterialTheme.typography.headlineMedium)
//		Text("headlineSmall", style = MaterialTheme.typography.headlineSmall)
//		Text("titleLarge", style = MaterialTheme.typography.titleLarge)
//		Text("titleMedium", style = MaterialTheme.typography.titleMedium)
//		Text("titleSmall", style = MaterialTheme.typography.titleSmall)
//		Text("bodyLarge", style = MaterialTheme.typography.bodyLarge)
//		Text("bodyMedium", style = MaterialTheme.typography.bodyMedium)
//		Text("bodySmall", style = MaterialTheme.typography.bodySmall)
//		Text("labelLarge", style = MaterialTheme.typography.labelLarge)
//		Text("labelMedium", style = MaterialTheme.typography.labelMedium)
//		Text("labelSmall", style = MaterialTheme.typography.labelSmall)
//	}
//}
//
//@Composable
//fun PageTest3(
//	argument: AppNavigation.PageTest3,
//) {
//	val navController = LocalNavController.current
//
//	fun onClick(it: Int) {
//		navController.setResult(true, it)
//		navController.popBackStack()
//	}
//
//	Column(
//		verticalArrangement = Arrangement.Center,
//		horizontalAlignment = Alignment.CenterHorizontally,
//	) {
//		Text(text = "PageTest3 ${argument.testInput}")
//		Button(
//			onClick = { onClick(1) }
//		) {
//			Text(text = "Button 1")
//		}
//		Button(
//			onClick = { onClick(2) }
//		) {
//			Text(text = "Button 2")
//		}
//		Button(
//			onClick = { onClick(3) }
//		) {
//			Text(text = "Button 3")
//		}
//	}
//}
