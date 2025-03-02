package basilliyc.cashnote.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import basilliyc.cashnote.R

@Composable
fun ItemVisibilitySelectable(
	modifier: Modifier = Modifier,
	title: String,
	icon: (@Composable () -> Unit)? = null,
	isSelected: Boolean,
	onClick: () -> Unit,
) {
	CardSelectable(
		modifier = modifier,
		onClick = onClick,
		isSelected = isSelected,
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(8.dp),
			verticalAlignment = Alignment.CenterVertically,
		) {
			if (icon != null) {
				icon()
				Spacer(modifier = Modifier.width(8.dp))
			}
			Column {
				Text(
					text = title,
					style = MaterialTheme.typography.titleMedium,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis,
				)
				Text(
					text = stringResource(
						if (isSelected) R.string.category_form_account_using_true
						else R.string.category_form_account_using_false,
					),
				)
			}
		}
	}


}