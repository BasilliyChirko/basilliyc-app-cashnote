package basilliyc.cashnote.ui.main

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.toRoute
import basilliyc.cashnote.ui.account.form.AccountForm
import basilliyc.cashnote.ui.account.list.AccountList
import basilliyc.cashnote.ui.account.statistic.AccountStatistic
import basilliyc.cashnote.ui.app_settings.AppSettings
import basilliyc.cashnote.utils.LocalNavController
import kotlinx.serialization.Serializable

sealed interface AppNavigation {
	@Serializable
	data object AccountList : AppNavigation
	
	@Serializable
	data object AccountStatistic : AppNavigation
	
	@Serializable
	data object AppSettings : AppNavigation
	
	@Serializable
	data class AccountForm(val id: Long?) : AppNavigation
}

fun NavGraphBuilder.createNavigationGraph() = this.apply {
	composable<AppNavigation.AccountList> { AccountList() }
	composable<AppNavigation.AccountStatistic> { AccountStatistic() }
	composable<AppNavigation.AppSettings> { AppSettings() }
	composable<AppNavigation.AccountForm> { AccountForm() }
}


//--------------------------------------------------------------------------------------------------

data class NavigationBarEntity(
	val page: AppNavigation,
	val label: @Composable () -> Unit,
	val icon: @Composable () -> Unit,
)

@Composable
fun RowScope.NavigationBarItem(entity: NavigationBarEntity) {
	val navController = LocalNavController.current
	val currentBackStackEntry = navController.currentBackStackEntryAsState()
	val currentDestination = currentBackStackEntry.value?.destination
	
	NavigationBarItem(
		selected = currentDestination?.route == entity.page::class.qualifiedName == true,
		onClick = {
			navController.navigate(entity.page) {
				popUpTo(navController.graph.findStartDestination().id) {
					saveState = true
				}
				launchSingleTop = true
				restoreState = true
			}
		},
		icon = entity.icon,
		label = entity.label
	)
}
