package basilliyc.cashnote.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import basilliyc.cashnote.utils.LocalNavController
import basilliyc.cashnote.R

@Composable
fun BackButton() {
	val navController = LocalNavController.current
	IconButton(
		onClick = { navController.popBackStack() }
	) {
		Icon(
			imageVector = Icons.AutoMirrored.Filled.ArrowBack,
			contentDescription = stringResource(R.string.common_navigation_back)
		)
	}
}