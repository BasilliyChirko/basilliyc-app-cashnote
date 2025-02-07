package basilliyc.cashnote.ui.category.form

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import basilliyc.cashnote.R
import basilliyc.cashnote.data.FinancialColor
import basilliyc.cashnote.data.FinancialIcon
import basilliyc.cashnote.ui.components.BackButton
import basilliyc.cashnote.ui.components.BoxLoading
import basilliyc.cashnote.ui.components.IconButton
import basilliyc.cashnote.ui.components.OutlinedTextField
import basilliyc.cashnote.ui.components.SimpleActionBar
import basilliyc.cashnote.ui.components.TextFieldState
import basilliyc.cashnote.ui.components.menu.MenuRowPopupIcon
import basilliyc.cashnote.ui.components.menu.MenuRowPopupColor
import basilliyc.cashnote.utils.Button
import basilliyc.cashnote.utils.DefaultPreview
import basilliyc.cashnote.utils.LocalNavController
import basilliyc.cashnote.utils.OutlinedButton
import basilliyc.cashnote.utils.showToast

//--------------------------------------------------------------------------------------------------
//  ROOT
//--------------------------------------------------------------------------------------------------

@Composable
fun CategoryForm() {
	
	val navController = LocalNavController.current
	val context = LocalContext.current
	val viewModel = viewModel<CategoryFormViewModel>()
	val state = viewModel.state
	
	Content(
		state = state,
		onNameChanged = viewModel::onNameChanged,
		onIconChanged = viewModel::onIconChanged,
		onSaveClicked = viewModel::onSaveClicked,
		onDeletedClicked = viewModel::onDeleteClicked,
		onColorSelected = viewModel::onColorChanged,
	)
	
	val action = state.action
	LaunchedEffect(action) {
		when (action) {
			CategoryFormState.Action.Cancel -> navController.popBackStack()
			CategoryFormState.Action.DeleteError -> {
				context.showToast(R.string.transaction_category_form_delete_error)
			}
			
			CategoryFormState.Action.DeleteSuccess -> {
				context.showToast(R.string.transaction_category_form_delete_success)
				navController.popBackStack()
			}
			
			CategoryFormState.Action.SaveError -> {
				context.showToast(R.string.transaction_category_form_save_error)
			}
			
			CategoryFormState.Action.SaveSuccess -> {
				val isNew = (state.content as? CategoryFormState.Content.Data)?.isNew
				when (isNew) {
					true -> context.showToast(R.string.transaction_category_form_save_success)
					false -> Unit
					null -> Unit
				}
				navController.popBackStack()
			}
			
			null -> Unit
		}
		viewModel.onActionConsumed()
	}
}

@Composable
@Preview(showBackground = true)
private fun TransactionCategoryFormPreview() = DefaultPreview {
	Content(
		state = CategoryFormState(
			content = CategoryFormState.Content.Data(
				isNew = true,
				name = TextFieldState(value = ""),
				icon = null,
				color = null,
			),
		),
		onNameChanged = {},
		onIconChanged = {},
		onSaveClicked = {},
		onDeletedClicked = {},
		onColorSelected = {},
	)
}

//--------------------------------------------------------------------------------------------------
//  CONTENT
//--------------------------------------------------------------------------------------------------

@Composable
private fun Content(
	state: CategoryFormState,
	onNameChanged: (String) -> Unit,
	onIconChanged: (FinancialIcon?) -> Unit,
	onColorSelected: (FinancialColor?) -> Unit,
	onSaveClicked: () -> Unit,
	onDeletedClicked: () -> Unit,
) {
	Surface(
		color = MaterialTheme.colorScheme.background,
		shape = MaterialTheme.shapes.medium,
	) {
		Column {
			ActionBar(
				state = state,
				onSaveClicked = onSaveClicked
			)
			
			when (val content = state.content) {
				is CategoryFormState.Content.Data -> ContentData(
					modifier = Modifier,
					content = content,
					onNameChanged = onNameChanged,
					onIconChanged = onIconChanged,
					onSaveClicked = onSaveClicked,
					onDeletedClicked = onDeletedClicked,
					onColorSelected = onColorSelected,
				)
				
				is CategoryFormState.Content.Loading -> BoxLoading()
			}
		}
	}
}

//--------------------------------------------------------------------------------------------------
//  ACTION BAR
//--------------------------------------------------------------------------------------------------

@Composable
private fun ActionBar(
	state: CategoryFormState,
	onSaveClicked: () -> Unit,
) {
	SimpleActionBar(
		title = {
			Text(
				text = when (state.content) {
					is CategoryFormState.Content.Data -> {
						if (state.content.isNew) stringResource(R.string.transaction_cagetory_form_new_category)
						else stringResource(R.string.transaction_category_form_edit_category)
					}
					
					is CategoryFormState.Content.Loading -> ""
				}
			)
		},
		navigationIcon = {
			BackButton(imageVector = Icons.Filled.Close)
		},
		actions = {
			IconButton(
				onClick = onSaveClicked,
				imageVector = Icons.Filled.Done,
				contentDescription = stringResource(R.string.transaction_category_form_action_save)
			)
		}
	)
}

//--------------------------------------------------------------------------------------------------
//  CONTENT.DATA
//--------------------------------------------------------------------------------------------------

@Composable
private fun ContentData(
	modifier: Modifier,
	content: CategoryFormState.Content.Data,
	onNameChanged: (String) -> Unit,
	onIconChanged: (FinancialIcon?) -> Unit,
	onColorSelected: (FinancialColor?) -> Unit,
	onSaveClicked: () -> Unit,
	onDeletedClicked: () -> Unit,
) {
	Column(modifier = modifier) {
		OutlinedTextField(
			state = content.name,
			onValueChange = onNameChanged,
			label = { Text(text = stringResource(R.string.transaction_category_form_label_name)) },
			singleLine = true,
			keyboardOptions = KeyboardOptions(
				capitalization = KeyboardCapitalization.Sentences,
				keyboardType = KeyboardType.Text
			),
		)
		
		MenuRowPopupIcon(
			title = stringResource(R.string.transaction_category_form_label_icon),
			icon = content.icon,
			onIconSelected = onIconChanged
		)
		
		MenuRowPopupColor(
			title = stringResource(R.string.transaction_category_form_label_color),
			color = content.color,
			onColorSelected = onColorSelected,
		)
		
		Spacer(modifier = Modifier.height(16.dp))
		
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp),
		) {
			if (!content.isNew) {
				OutlinedButton(
					onClick = onDeletedClicked,
					text = stringResource(R.string.transaction_category_form_action_delete),
					modifier = Modifier.weight(1f),
				)
				Spacer(modifier = Modifier.width(16.dp))
			}
			
			Button(
				onClick = onSaveClicked,
				text = stringResource(
					if (content.isNew) R.string.transaction_category_form_action_save
					else R.string.transaction_category_form_action_save_short
				),
				modifier = Modifier.weight(1f),
			)
		}
		
	}
}




