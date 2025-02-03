package basilliyc.cashnote.ui.components

import androidx.compose.runtime.Composable
import basilliyc.cashnote.utils.ScaffoldBox

@Composable
fun PageLoading(){
	ScaffoldBox(
		topBar = { SimpleActionBar() },
		content = { BoxLoading() }
	)
}