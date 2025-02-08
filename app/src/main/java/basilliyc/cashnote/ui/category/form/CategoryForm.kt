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
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import basilliyc.cashnote.AppNavigation
import basilliyc.cashnote.R
import basilliyc.cashnote.data.FinancialColor
import basilliyc.cashnote.data.FinancialIcon
import basilliyc.cashnote.ui.base.rememberResultHandler
import basilliyc.cashnote.ui.components.IconButton
import basilliyc.cashnote.ui.components.OutlinedTextField
import basilliyc.cashnote.ui.components.PageLoading
import basilliyc.cashnote.ui.components.SimpleActionBar
import basilliyc.cashnote.ui.components.TextFieldState
import basilliyc.cashnote.ui.components.menu.MenuRowPopupColor
import basilliyc.cashnote.ui.components.menu.MenuRowPopupIcon
import basilliyc.cashnote.utils.Button
import basilliyc.cashnote.utils.DefaultPreview
import basilliyc.cashnote.utils.OutlinedButton
import basilliyc.cashnote.utils.ScaffoldColumn
import basilliyc.cashnote.utils.showToast

@Composable
fun CategoryForm() {
	val viewModel = viewModel<CategoryFormViewModel>()
	Page(page = viewModel.state.page, listener = viewModel)
	Result(result = viewModel.state.result, listener = viewModel)
}


@Composable
private fun Result(
	result: CategoryFormStateHolder.Result?,
	listener: CategoryFormListener,
) {
	rememberResultHandler().value.consume(result) {
		listener.onResultConsumed()
		when (result) {
			
			null -> Unit
			
			CategoryFormStateHolder.Result.DeleteError -> {
				context.showToast(R.string.transaction_category_form_delete_error)
			}
			
			CategoryFormStateHolder.Result.DeleteSuccess -> {
				context.showToast(R.string.transaction_category_form_delete_success)
				navController.popBackStack()
			}
			
			CategoryFormStateHolder.Result.SaveError -> {
				context.showToast(R.string.transaction_category_form_save_error)
			}
			
			is CategoryFormStateHolder.Result.SaveSuccess -> {
				if (result.isNew) context.showToast(R.string.transaction_category_form_save_success)
				navController.popBackStack()
			}
			
			CategoryFormStateHolder.Result.NavigateBack -> {
				navController.popBackStack()
			}
			
			is CategoryFormStateHolder.Result.NavigateCategoryExtendedDeletion -> {
				navigateForward(AppNavigation.CategoryExtendedDeletion(result.categoryId))
			}
		}
	}
}


@Composable
@Preview(showBackground = true)
private fun PageDataPreview() = DefaultPreview {
	PageData(
		page = CategoryFormStateHolder.Page.Data(
			isNew = true,
			name = TextFieldState(value = ""),
			icon = null,
			color = null,
		),
		listener = object : CategoryFormListener {
			override fun onResultConsumed() {}
			override fun onNameChanged(name: String) {}
			override fun onIconChanged(icon: FinancialIcon?) {}
			override fun onColorChanged(color: FinancialColor?) {}
			override fun onSaveClicked() {}
			override fun onDeleteClicked() {}
		}
	)
}

@Composable
private fun Page(
	page: CategoryFormStateHolder.Page,
	listener: CategoryFormListener,
) {
	when (page) {
		is CategoryFormStateHolder.Page.Data -> PageData(
			page = page,
			listener = listener,
		)
		
		CategoryFormStateHolder.Page.Loading -> PageLoading()
	}
}


@Composable
private fun PageData(
	page: CategoryFormStateHolder.Page.Data,
	listener: CategoryFormListener,
) {
	ScaffoldColumn(
		topBar = {
			SimpleActionBar(
				title = if (page.isNew) stringResource(R.string.transaction_cagetory_form_new_category)
				else stringResource(R.string.transaction_category_form_edit_category),
				
				actions = {
					IconButton(
						onClick = listener::onSaveClicked,
						imageVector = Icons.Filled.Done,
						contentDescription = stringResource(R.string.transaction_category_form_action_save)
					)
				}
			)
		}
	) {
		Column {
			OutlinedTextField(
				state = page.name,
				onValueChange = listener::onNameChanged,
				label = { Text(text = stringResource(R.string.transaction_category_form_label_name)) },
				singleLine = true,
				keyboardOptions = KeyboardOptions(
					capitalization = KeyboardCapitalization.Sentences,
					keyboardType = KeyboardType.Text
				),
			)
			
			MenuRowPopupIcon(
				title = stringResource(R.string.transaction_category_form_label_icon),
				icon = page.icon,
				onIconSelected = listener::onIconChanged
			)
			
			MenuRowPopupColor(
				title = stringResource(R.string.transaction_category_form_label_color),
				color = page.color,
				onColorSelected = listener::onColorChanged,
			)
			
			Spacer(modifier = Modifier.height(16.dp))
			
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.padding(16.dp),
			) {
				if (!page.isNew) {
					OutlinedButton(
						onClick = listener::onDeleteClicked,
						text = stringResource(R.string.transaction_category_form_action_delete),
						modifier = Modifier.weight(1f),
					)
					Spacer(modifier = Modifier.width(16.dp))
				}
				
				Button(
					onClick = listener::onSaveClicked,
					text = stringResource(
						if (page.isNew) R.string.transaction_category_form_action_save
						else R.string.transaction_category_form_action_save_short
					),
					modifier = Modifier.weight(1f),
				)
			}
		}
	}
}





