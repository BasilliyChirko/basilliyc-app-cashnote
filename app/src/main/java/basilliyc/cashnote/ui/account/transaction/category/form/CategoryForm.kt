package basilliyc.cashnote.ui.account.transaction.category.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import basilliyc.cashnote.R
import basilliyc.cashnote.data.FinancialCategoryIcon
import basilliyc.cashnote.ui.components.BackButton
import basilliyc.cashnote.ui.components.BoxLoading
import basilliyc.cashnote.ui.components.IconButton
import basilliyc.cashnote.ui.components.SimpleActionBar
import basilliyc.cashnote.ui.components.TextField
import basilliyc.cashnote.ui.components.TextFieldState
import basilliyc.cashnote.utils.Button
import basilliyc.cashnote.utils.DefaultPreview
import basilliyc.cashnote.utils.LocalNavController
import basilliyc.cashnote.utils.OutlinedButton
import basilliyc.cashnote.utils.toast

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
	)
	
	val action = state.action
	LaunchedEffect(action) {
		when (action) {
			CategoryFormState.Action.Cancel -> navController.popBackStack()
			CategoryFormState.Action.DeleteError -> {
				context.toast(R.string.transaction_category_form_delete_error)
			}
			
			CategoryFormState.Action.DeleteSuccess -> {
				context.toast(R.string.transaction_category_form_delete_success)
				navController.popBackStack()
			}
			
			CategoryFormState.Action.SaveError -> {
				context.toast(R.string.transaction_category_form_save_error)
			}
			
			CategoryFormState.Action.SaveSuccess -> {
				val isNew = (state.content as? CategoryFormState.Content.Data)?.isNew
				when (isNew) {
					true -> context.toast(R.string.transaction_category_form_save_success)
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
			),
		),
		onNameChanged = {},
		onIconChanged = {},
		onSaveClicked = {},
		onDeletedClicked = {},
	)
}

//--------------------------------------------------------------------------------------------------
//  CONTENT
//--------------------------------------------------------------------------------------------------

@Composable
private fun Content(
	state: CategoryFormState,
	onNameChanged: (String) -> Unit,
	onIconChanged: (FinancialCategoryIcon?) -> Unit,
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
					onDeletedClicked = onDeletedClicked
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
	onIconChanged: (FinancialCategoryIcon?) -> Unit,
	onSaveClicked: () -> Unit,
	onDeletedClicked: () -> Unit,
) {
	Column(modifier = modifier) {
		
		TextField(
			state = content.name,
			onValueChange = onNameChanged,
			label = { Text(text = stringResource(R.string.transaction_category_form_label_name)) },
			singleLine = true,
			keyboardOptions = KeyboardOptions(
				capitalization = KeyboardCapitalization.Sentences,
				keyboardType = KeyboardType.Text
			),
		)
		
		Spacer(modifier = Modifier.height(16.dp))
		
		TransactionCategoryIconPicker(
			icon = content.icon,
			onIconChanged = onIconChanged
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

@Composable
private fun TransactionCategoryIconPicker(
	icon: FinancialCategoryIcon?,
	onIconChanged: (FinancialCategoryIcon?) -> Unit,
) {
	
	val selectedIcon = icon
	val icons = FinancialCategoryIcon.entries
	
	LazyVerticalGrid(
		columns = GridCells.Adaptive(56.dp),
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		verticalArrangement = Arrangement.spacedBy(8.dp),
		contentPadding = PaddingValues(horizontal = 16.dp)
	) {
		items(icons.size) { index ->
			val icon = icons[index]
			
			Card(
				modifier = Modifier
					.requiredSize(56.dp),
				onClick = { onIconChanged(icon) },
				colors = CardDefaults.cardColors(
					containerColor = if (selectedIcon == icon) {
						MaterialTheme.colorScheme.primaryContainer
					} else {
						MaterialTheme.colorScheme.surface
					}
				),
				border = CardDefaults.outlinedCardBorder(),
			) {
				Icon(
					modifier = Modifier
						.fillMaxSize()
						.padding(8.dp),
					imageVector = icon.imageVector,
					contentDescription = icon.name,
				)
			}
		}
	}
	
}