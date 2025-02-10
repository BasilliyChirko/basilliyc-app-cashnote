package basilliyc.cashnote.utils

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun ScaffoldBox(
	modifier: Modifier = Modifier,
	topBar: @Composable () -> Unit = {},
	bottomBar: @Composable () -> Unit = {},
	snackbarHost: @Composable () -> Unit = {},
	floatingActionButton: @Composable () -> Unit = {},
	floatingActionButtonPosition: FabPosition = FabPosition.End,
	containerColor: Color = MaterialTheme.colorScheme.background,
	contentColor: Color = contentColorFor(containerColor),
	contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
	content: @Composable BoxScope.() -> Unit,
) {
	Scaffold(
		modifier = modifier,
		topBar = topBar,
		bottomBar = bottomBar,
		snackbarHost = snackbarHost,
		floatingActionButton = floatingActionButton,
		floatingActionButtonPosition = floatingActionButtonPosition,
		containerColor = containerColor,
		contentColor = contentColor,
		contentWindowInsets = contentWindowInsets,
		content = {
			Box(
				modifier = Modifier.padding(it),
				content = content
			)
		},
	)
}

@Composable
fun ScaffoldColumn(
	modifier: Modifier = Modifier,
	columnModifier: Modifier = Modifier,
	topBar: @Composable () -> Unit = {},
	bottomBar: @Composable () -> Unit = {},
	snackbarHost: @Composable () -> Unit = {},
	floatingActionButton: @Composable () -> Unit = {},
	floatingActionButtonPosition: FabPosition = FabPosition.End,
	containerColor: Color = MaterialTheme.colorScheme.background,
	contentColor: Color = contentColorFor(containerColor),
	contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
	content: @Composable ColumnScope.() -> Unit,
) {
	Scaffold(
		modifier = modifier,
		topBar = topBar,
		bottomBar = bottomBar,
		snackbarHost = snackbarHost,
		floatingActionButton = floatingActionButton,
		floatingActionButtonPosition = floatingActionButtonPosition,
		containerColor = containerColor,
		contentColor = contentColor,
		contentWindowInsets = contentWindowInsets,
		content = {
			Column(
				modifier = columnModifier.padding(it),
				content = content
			)
		},
	)
}