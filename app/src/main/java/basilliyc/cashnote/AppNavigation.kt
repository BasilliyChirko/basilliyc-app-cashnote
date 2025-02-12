package basilliyc.cashnote

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.dialog
import basilliyc.cashnote.ui.account.details.AccountDetails
import basilliyc.cashnote.ui.account.form.AccountForm
import basilliyc.cashnote.ui.account.history.AccountHistory
import basilliyc.cashnote.ui.account.list.AccountList
import basilliyc.cashnote.ui.category.deletion.CategoryExtendedDeletion
import basilliyc.cashnote.ui.category.form.CategoryForm
import basilliyc.cashnote.ui.category.list.CategoryList
import basilliyc.cashnote.ui.settings.account_params.AccountParams
import basilliyc.cashnote.ui.settings.page.AppSettings
import basilliyc.cashnote.ui.statistic.AccountStatistic
import basilliyc.cashnote.ui.transaction.form.TransactionForm
import basilliyc.cashnote.utils.LocalNavController
import kotlinx.serialization.Serializable

sealed interface AppNavigation {
	
	@Serializable
	data object AccountList : AppNavigation
	
	@Serializable
	data class AccountForm(val accountId: Long?) : AppNavigation
	
	@Serializable
	data class AccountDetails(
		val isFromNavigation: Boolean,
		val accountId: Long,
	) : AppNavigation
	
	@Serializable
	data class AccountHistory(val accountId: Long) : AppNavigation
	
	@Serializable
	data class TransactionForm(
		val accountId: Long,
		val categoryId: Long,
		val transactionId: Long?,
	) : AppNavigation
	
	@Serializable
	data object CategoryList : AppNavigation
	
	@Serializable
	data class CategoryForm(val categoryId: Long?) : AppNavigation
	
	@Serializable
	data class CategoryExtendedDeletion(val categoryId: Long) : AppNavigation
	
	@Serializable
	data object Statistic : AppNavigation
	
	@Serializable
	data object Settings : AppNavigation
	
}

fun NavGraphBuilder.createNavigationGraph() = this.apply {
	composable<AppNavigation.AccountList> { AccountList() }
	composable<AppNavigation.Statistic> { AccountStatistic() }
	composable<AppNavigation.Settings> { AppSettings() }
	composable<AppNavigation.AccountForm> { AccountForm() }
	composable<AppNavigation.CategoryList> { CategoryList() }
	composable<AppNavigation.CategoryForm> { CategoryForm() }
	composable<AppNavigation.AccountHistory> { AccountHistory() }
	composable<AppNavigation.AccountDetails> { AccountDetails() }
	composable<AppNavigation.TransactionForm> { TransactionForm() }
	dialog<AppNavigation.CategoryExtendedDeletion> { CategoryExtendedDeletion() }
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
	val currentBackStackEntry by navController.currentBackStackEntryAsState()
	
	NavigationBarItem(
		selected = entity.page.toString() == currentBackStackEntry?.toAppNavigationPath(),
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

fun NavBackStackEntry.toAppNavigationPath(): String? {
	
	var path = this.destination.route?.let {
		it.substring(it.lastIndexOf('.') + 1, it.length)
	} ?: return null
	
	this.arguments?.let { arguments ->
		arguments.keySet()?.filterNotNull()?.forEach { key ->
			val objectValue = key + "=" + arguments.get(key).toString()
			val routeValue = "{$key}"
			path = path.replace(routeValue, objectValue)
		}
		
		if (path.contains('/')) {
			path = path.replaceFirst('/', '(').replace("/", ", ") + ')'
		}
		
	}
	
	return path
	
}