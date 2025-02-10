package basilliyc.cashnote.ui.components

import androidx.compose.runtime.Composable
import basilliyc.cashnote.ui.base.BaseViewModel

@Composable
fun RegisterActivityRequests(
	viewModel: BaseViewModel,
) {
	
	viewModel.activityResultRegistrationList.forEach {
		it.registerOnCompose()
	}
	
	viewModel.activitySuspendResultRegistrationList.forEach {
		it.registerOnCompose()
	}
	
}