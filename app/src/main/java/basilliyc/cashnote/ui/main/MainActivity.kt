package basilliyc.cashnote.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import basilliyc.cashnote.backend.database.AppDatabaseAccountRepository
import basilliyc.cashnote.ui.theme.CashNoteTheme
import basilliyc.cashnote.utils.LocalNavController
import basilliyc.cashnote.utils.Logcat
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
	
	private val accountRepository: AppDatabaseAccountRepository by inject()
	
	private val logcat by lazy { Logcat(this) }
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			
			val navController = rememberNavController()
			CompositionLocalProvider(LocalNavController provides navController) {
				
				val navBackStack by navController.currentBackStackEntryAsState()
				
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

//			LaunchedEffect(this) {
//				logcat.debug("--------- Account List 1 ---------")
//				accountRepository.getAccountsList().forEach {
//					logcat.debug(it)
//				}
//				accountRepository.saveAccount(Account(name = "Test Account 4"))
//				logcat.debug("--------- Account List 2 ---------")
//				accountRepository.getAccountsList().forEach {
//					logcat.debug(it)
//				}
//			}
				
				CashNoteTheme {
					Scaffold(
						modifier = Modifier.fillMaxSize(),
						bottomBar = {
							val currentDestination = navBackStack?.destination
							NavigationBar {
								NavigationBarItem(
									selected = currentDestination?.hierarchy?.any { it.route == AppNavigation.Page1::class.qualifiedName } == true,
									onClick = {
										navController.navigate(AppNavigation.Page1) {
											popUpTo(navController.graph.findStartDestination().id) {
												saveState = true
											}
											launchSingleTop = true
											restoreState = true
										}
									},
									icon = { /*TODO*/ },
									label = { Text("Page 1") }
								)
								NavigationBarItem(
									selected = currentDestination?.hierarchy?.any { it.route == AppNavigation.Page2::class.qualifiedName } == true,
									onClick = {
										navController.navigate(AppNavigation.Page2(42)) {
											popUpTo(navController.graph.findStartDestination().id) {
												saveState = true
											}
											launchSingleTop = true
											restoreState = true
										}
									},
									icon = { /*TODO*/ },
									label = { Text("Page 2") }
								)
							}
						}
					) { innerPadding ->
						NavHost(
							navController,
							startDestination = AppNavigation.Page1,
							Modifier.padding(innerPadding)
						) {
							
							composable<AppNavigation.Page1> { Page1(it.toRoute()) }
							composable<AppNavigation.Page2> { Page2(it.toRoute()) }
							composable<AppNavigation.PageTest3> { PageTest3(argument = it.toRoute()) }
						}
					}
				}
			}
			
		}
		
	}
}


@Composable
fun Page1(
	argument: AppNavigation.Page1
) {
	val navController = LocalNavController.current
	val number = navController.currentBackStackEntry?.savedStateHandle?.get<Int>("key")
	Column {
		Text(text = "Hello Page! $number", modifier = Modifier)
		Button(
			onClick = {
				navController.navigate(AppNavigation.PageTest3(55))
			}
		) {
			Text(text = "Go to page 2")
		}
	}
}

@Composable
fun Page2(argument: AppNavigation.Page2) {
	Column(modifier = Modifier.padding(16.dp)) {
		Text("NUMBER ${argument.number}", style = MaterialTheme.typography.displayLarge)
		Text("displayLarge", style = MaterialTheme.typography.displayLarge)
		Text("displayMedium", style = MaterialTheme.typography.displayMedium)
		Text("displaySmall", style = MaterialTheme.typography.displaySmall)
		Text("headlineLarge", style = MaterialTheme.typography.headlineLarge)
		Text("headlineMedium", style = MaterialTheme.typography.headlineMedium)
		Text("headlineSmall", style = MaterialTheme.typography.headlineSmall)
		Text("titleLarge", style = MaterialTheme.typography.titleLarge)
		Text("titleMedium", style = MaterialTheme.typography.titleMedium)
		Text("titleSmall", style = MaterialTheme.typography.titleSmall)
		Text("bodyLarge", style = MaterialTheme.typography.bodyLarge)
		Text("bodyMedium", style = MaterialTheme.typography.bodyMedium)
		Text("bodySmall", style = MaterialTheme.typography.bodySmall)
		Text("labelLarge", style = MaterialTheme.typography.labelLarge)
		Text("labelMedium", style = MaterialTheme.typography.labelMedium)
		Text("labelSmall", style = MaterialTheme.typography.labelSmall)
	}
}

@Composable
fun PageTest3(
	argument: AppNavigation.PageTest3,
) {
	val navController = LocalNavController.current
	
	fun onClick(it: Int) {
		navController.previousBackStackEntry?.savedStateHandle?.set("key", it)
		navController.popBackStack()
//		logcat.info("Click on button $it")
	}
	
	Column(
		verticalArrangement = Arrangement.Center,
		horizontalAlignment = Alignment.CenterHorizontally,
	) {
		Text(text = "PageTest3 ${argument.testInput}")
		Button(
			onClick = { onClick(1) }
		) {
			Text(text = "Button 1")
		}
		Button(
			onClick = { onClick(2) }
		) {
			Text(text = "Button 2")
		}
		Button(
			onClick = { onClick(3) }
		) {
			Text(text = "Button 3")
		}
	}
}

//
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//	CashNoteTheme {
//		Greeting("Android")
//	}
//}