package basilliyc.cashnote.ui.components

import androidx.compose.runtime.Composable
import basilliyc.cashnote.utils.ScaffoldBox

@Composable
fun PageLoading(
	title: String = "",
	showBackButton: Boolean = true,
) {
	ScaffoldBox(
		topBar = {
			SimpleActionBar(
				navigationIcon = {
					if (showBackButton) {
						BackButton()
					}
				},
				title = title,
			)
		},
		content = { BoxLoading() }
	)
}