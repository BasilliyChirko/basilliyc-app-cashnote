package basilliyc.cashnote.ui.activity

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.dialog
import basilliyc.cashnote.ui.account.details.AccountDetails
import basilliyc.cashnote.ui.account.form.AccountForm
import basilliyc.cashnote.ui.account.history.AccountHistory
import basilliyc.cashnote.ui.account.list.AccountList
import basilliyc.cashnote.ui.statistic.AccountStatistic
import basilliyc.cashnote.ui.account.transaction.form_old.AccountTransactionForm
import basilliyc.cashnote.ui.settings.AppSettings
import basilliyc.cashnote.ui.account.transaction.category.form.CategoryForm
import basilliyc.cashnote.ui.account.transaction.category.list.CategoryList
import basilliyc.cashnote.ui.account.transaction.form.TransactionForm
import basilliyc.cashnote.utils.LocalNavController
import kotlinx.serialization.Serializable

sealed interface AppNavigation {
	
	@Serializable
	data object AccountList : AppNavigation
	
	@Serializable
	data class AccountDetails(val accountId: Long) : AppNavigation
	
	@Serializable
	data class AccountForm(val accountId: Long?) : AppNavigation
	
	@Serializable
	data class AccountHistory(val accountId: Long) : AppNavigation
	
	@Serializable
	data class TransactionFormOld(val accountId: Long, val transactionId: Long?) : AppNavigation
	
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
	data object Statistic : AppNavigation
	
	@Serializable
	data object Settings : AppNavigation
	
}

fun NavGraphBuilder.createNavigationGraph() = this.apply {
	composable<AppNavigation.AccountList> { AccountList() }
	composable<AppNavigation.Statistic> { AccountStatistic() }
	composable<AppNavigation.Settings> { AppSettings() }
	composable<AppNavigation.AccountForm> { AccountForm() }
	composable<AppNavigation.TransactionFormOld> { AccountTransactionForm() }
	composable<AppNavigation.CategoryList> { CategoryList() }
	dialog<AppNavigation.CategoryForm> { CategoryForm() }
	composable<AppNavigation.AccountHistory> { AccountHistory() }
	composable<AppNavigation.AccountDetails> { AccountDetails() }
	composable<AppNavigation.TransactionForm> { TransactionForm() }
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
