@file:OptIn(ExperimentalMaterial3Api::class)

package basilliyc.cashnote.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable

@Composable
fun PopupCustom(
	state: PopupState = rememberPopupState(),
	anchor: @Composable PopupState.() -> Unit,
	content: @Composable PopupState.() -> Unit,
) {
	state.anchor()
	if (state.expanded.value) {
		state.content()
	}
}





@Composable
fun PopupCustom2(
	state: PopupState = rememberPopupState(),
	anchor: @Composable PopupState.() -> Unit,
	content: @Composable PopupState.() -> Unit,
) {
	state.anchor()
	if (state.expanded.value) {
		state.content()
	}
}