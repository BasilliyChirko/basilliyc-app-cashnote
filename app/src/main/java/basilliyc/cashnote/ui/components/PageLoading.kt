package basilliyc.cashnote.ui.components

import androidx.compose.runtime.Composable
import basilliyc.cashnote.utils.ScaffoldBox

@Composable
fun PageLoading(
	showBackButton: Boolean = true,
) {
	ScaffoldBox(
		topBar = {
			SimpleActionBar(
				navigationIcon = {
					if (showBackButton) {
						BackButton()
					}
				}
			)
		},
		content = { BoxLoading() }
	)
}