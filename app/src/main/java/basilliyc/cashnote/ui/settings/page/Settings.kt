package basilliyc.cashnote.ui.settings.page

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import basilliyc.cashnote.AppNavigation
import basilliyc.cashnote.R
import basilliyc.cashnote.backend.preferences.AppPreferences
import basilliyc.cashnote.ui.base.handleResult
import basilliyc.cashnote.ui.base.rememberInteractionHelper
import basilliyc.cashnote.ui.components.ConfirmationDialog
import basilliyc.cashnote.ui.components.PopupMenuItem
import basilliyc.cashnote.ui.components.RegisterActivityRequests
import basilliyc.cashnote.ui.components.SimpleActionBar
import basilliyc.cashnote.ui.components.menu.MenuRowPopup
import basilliyc.cashnote.ui.components.menu.MenuRowSwitch
import basilliyc.cashnote.ui.components.menu.MenuRowText
import basilliyc.cashnote.ui.settings.account_params.AccountParams
import basilliyc.cashnote.ui.stringName
import basilliyc.cashnote.ui.theme.ThemeMode
import basilliyc.cashnote.utils.DefaultPreview
import basilliyc.cashnote.utils.ScaffoldColumn
import basilliyc.cashnote.utils.rememberInject

@Composable
fun AppSettings() {
	val viewModel = viewModel<SettingsViewModel>()
	RegisterActivityRequests(viewModel)
	Page(page = viewModel.state.page, listener = viewModel)
	Dialog(dialog = viewModel.state.dialog, listener = viewModel)
	Result(result = viewModel.state.result, listener = viewModel)
}

@Composable
private fun Result(
	result: SettingsStateHolder.Result?,
	listener: SettingsListener,
) {
	handleResult(result, listener) {
		when (it) {
			SettingsStateHolder.Result.BackupRestoreSuccess -> {
				showToast(R.string.settings_backup_restore_success)
			}
			
			SettingsStateHolder.Result.BackupRestoreFailure -> {
				showToast(R.string.settings_backup_restore_failure)
			}
		}
	}
}

@Preview(showBackground = true)
@Composable
private fun PageDataPreview() = DefaultPreview {
	PageData(
		page = SettingsStateHolder.Page.Data(
			common = SettingsStateHolder.Common(
				themeMode = ThemeMode.System,
			)
		),
		listener = object : SettingsListener {
			override fun onResultHandled() {}
			override fun onThemeModeChanged(themeMode: ThemeMode) {}
			override fun onBackupCreateClicked(activity: Activity) {}
			override fun onBackupRestoreClicked() {}
			override fun onBackupRestoreConfirmed() {}
			override fun onBackupRestoreCanceled() {}
		}
	)
}

@Composable
private fun Page(
	page: SettingsStateHolder.Page,
	listener: SettingsListener,
) {
	when (page) {
		is SettingsStateHolder.Page.Data -> PageData(page, listener)
	}
}

@Composable
private fun PageData(
	page: SettingsStateHolder.Page.Data,
	listener: SettingsListener,
) {
	ScaffoldColumn(
		topBar = {
			SimpleActionBar(
				title = stringResource(R.string.settings_title),
				navigationIcon = {}
			)
		},
		columnModifier = Modifier.verticalScroll(rememberScrollState())
	) {
		SettingsCommon(common = page.common, listener = listener)
		SettingsPeriodicStatistic()
		SettingsAccountList()
		SettingsBackup(listener = listener)
	}
}

@Composable
private fun ColumnScope.Group(
	title: String,
	content: @Composable ColumnScope.() -> Unit,
) {
	Spacer(modifier = Modifier.height(8.dp))
	Text(
		text = title,
		modifier = Modifier.padding(horizontal = 16.dp),
		style = MaterialTheme.typography.titleMedium
	)
	OutlinedCard(
		modifier = Modifier.padding(8.dp)
	) {
		content()
	}
}

@Composable
private fun ColumnScope.SettingsCommon(
	common: SettingsStateHolder.Common,
	listener: SettingsListener,
) {
	Group(title = stringResource(R.string.settings_common_group_title)) {
		
		val interactionHelper = rememberInteractionHelper()
		MenuRowText(
			title = stringResource(R.string.settings_common_categories),
			leadingIcon = {
				Icon(
					imageVector = Icons.Filled.Category,
					contentDescription = stringResource(R.string.settings_common_categories)
				)
			},
			onClick = {
				interactionHelper.navigateForward(AppNavigation.CategoryList)
			}
		)
		HorizontalDivider()
		MenuRowPopup(
			title = stringResource(R.string.settings_common_theme_mode),
			value = common.themeMode.stringName,
			leadingIcon = {
				Icon(
					imageVector = Icons.Filled.DarkMode,
					contentDescription = stringResource(R.string.settings_common_theme_mode)
				)
			},
			items = {
				ThemeMode.entries.forEach { theme ->
					PopupMenuItem(
						text = theme.stringName,
						onClick = { listener.onThemeModeChanged(theme) },
					)
				}
			},
		)
		
	}
}

@Composable
private fun ColumnScope.SettingsBackup(
	listener: SettingsListener,
) {
	Group(title = stringResource(R.string.settings_backup_group_title)) {
		
		val activity = LocalActivity.current
		
		MenuRowText(
			title = stringResource(R.string.settings_backup_create),
			leadingIcon = {
				Icon(
					imageVector = Icons.Filled.FileUpload,
					contentDescription = stringResource(R.string.settings_backup_create)
				)
			},
			onClick = {
				activity?.let { listener.onBackupCreateClicked(it) }
			},
		)
		
		HorizontalDivider()
		
		MenuRowText(
			title = stringResource(R.string.settings_backup_restore),
			leadingIcon = {
				Icon(
					imageVector = Icons.Filled.FileDownload,
					contentDescription = stringResource(R.string.settings_backup_restore)
				)
			},
			onClick = listener::onBackupRestoreClicked,
		)
		
	}
}

@Composable
private fun ColumnScope.SettingsPeriodicStatistic() {
	Group(title = stringResource(R.string.account_params_title)) {
		AccountParams()
	}
}

@Composable
private fun ColumnScope.SettingsAccountList() {
	Group(title = stringResource(R.string.settings_account_list_title)) {
		val preferences = rememberInject<AppPreferences>()
		MenuRowSwitch(
			title = stringResource(R.string.account_params_show_accounts_list_in_single_line),
			checked = preferences.accountListSingleLine.collectValue(),
			onCheckedChange = {
				preferences.accountListSingleLine.value = it
			}
		)
		HorizontalDivider()
		MenuRowSwitch(
			title = stringResource(R.string.account_params_quick_transaction),
			checked = preferences.accountListQuickTransaction.collectValue(),
			onCheckedChange = {
				preferences.accountListQuickTransaction.value = it
			}
		)
	}
}

@Composable
private fun Dialog(
	dialog: SettingsStateHolder.Dialog?,
	listener: SettingsListener,
) {
	when (dialog) {
		null -> Unit
		
		SettingsStateHolder.Dialog.RestoreBackupConfirmation -> ConfirmationDialog(
			title = stringResource(R.string.settings_backup_restore_confirmation_title),
			text = stringResource(R.string.settings_backup_restore_confirmation_text),
			confirm = stringResource(R.string.settings_backup_restore_confirmation_confirm),
			onConfirm = listener::onBackupRestoreConfirmed,
			onCancel = listener::onBackupRestoreCanceled
		)
	}
}