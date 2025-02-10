package basilliyc.cashnote.ui.settings

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
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
import basilliyc.cashnote.R
import basilliyc.cashnote.ui.components.PopupMenuItem
import basilliyc.cashnote.ui.components.SimpleActionBar
import basilliyc.cashnote.ui.components.menu.MenuRowPopup
import basilliyc.cashnote.ui.settings.SettingsStateHolder.Page
import basilliyc.cashnote.ui.stringName
import basilliyc.cashnote.ui.theme.ThemeMode
import basilliyc.cashnote.utils.DefaultPreview
import basilliyc.cashnote.utils.ScaffoldColumn

@Composable
fun AppSettings() {
	val viewModel = viewModel<SettingsViewModel>()
	Page(page = viewModel.state.page, listener = viewModel)
}

@Preview(showBackground = true)
@Composable
private fun PageDataPreview() = DefaultPreview {
	PageData(
		page = Page.Data(
			common = SettingsStateHolder.Common(
				themeMode = ThemeMode.System,
			)
		),
		listener = object : SettingsListener {
			override fun onThemeModeChanged(themeMode: ThemeMode) {}
		}
	)
}

@Composable
private fun Page(
	page: Page,
	listener: SettingsListener,
) {
	when (page) {
		is Page.Data -> PageData(page, listener)
	}
}

@Composable
private fun PageData(
	page: Page.Data,
	listener: SettingsListener,
) {
	ScaffoldColumn(
		topBar = {
			SimpleActionBar(
				title = stringResource(R.string.settings_title),
				navigationIcon = {}
			)
		}
	) {
		SettingsCommon(common = page.common, listener = listener)
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