@file:OptIn(ExperimentalMaterial3Api::class)

package basilliyc.cashnote.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun SimpleActionBar(
	modifier: Modifier = Modifier,
	title: @Composable () -> Unit = {},
	navigationIcon: @Composable () -> Unit = { BackButton() },
	actions: @Composable RowScope.() -> Unit = {},
	containerColor: Color = Color.Unspecified,
) {
	TopAppBar(
		modifier = modifier,
		title = title,
		navigationIcon = navigationIcon,
		actions = actions,
		colors = TopAppBarDefaults.topAppBarColors(
			containerColor = containerColor,
		)
	)
}