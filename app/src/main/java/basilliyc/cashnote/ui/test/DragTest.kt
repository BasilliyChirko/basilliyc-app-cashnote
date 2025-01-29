package basilliyc.cashnote.ui.test

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import basilliyc.cashnote.data.AccountCurrency
import basilliyc.cashnote.data.FinancialAccount
import basilliyc.cashnote.data.color
import basilliyc.cashnote.data.symbol
import basilliyc.cashnote.utils.DraggableVerticalGrid
import basilliyc.cashnote.utils.LocalLogcat
import basilliyc.cashnote.utils.applyIf
import basilliyc.cashnote.utils.asPriceString
import basilliyc.cashnote.utils.reordered

@Composable
fun DragTest() {
	
	var accountList by remember {
		mutableStateOf(
			listOf(
				FinancialAccount(
					id = 1,
					name = "1 Cash",
					currency = AccountCurrency.USD,
					color = null,
					balance = 100.0,
					position = 0,
				),
				FinancialAccount(
					id = 2,
					name = "2 Card",
					currency = AccountCurrency.USD,
					color = null,
					balance = 1000.0,
					position = 1,
				),
				FinancialAccount(
					id = 3,
					name = "3 Savings",
					currency = AccountCurrency.USD,
					color = null,
					balance = 10000.0,
					position = 2,
				),
				FinancialAccount(
					id = 4,
					name = "4 Credit",
					currency = AccountCurrency.USD,
					color = null,
					balance = 100000.0,
					position = 3,
				),
				FinancialAccount(
					id = 5,
					name = "5 Crypto",
					currency = AccountCurrency.USD,
					color = null,
					balance = 1000000.0,
					position = 4,
				),
				FinancialAccount(
					id = 6,
					name = "6 Other",
					currency = AccountCurrency.USD,
					color = null,
					balance = 10000000.0,
					position = 5,
				),
				FinancialAccount(
					id = 7,
					name = "7 Other",
					currency = AccountCurrency.USD,
					color = null,
					balance = 10000000.0,
					position = 6,
				),
				FinancialAccount(
					id = 8,
					name = "8 Other",
					currency = AccountCurrency.USD,
					color = null,
					balance = 10000000.0,
					position = 7,
				),
				FinancialAccount(
					id = 9,
					name = "9 Other",
					currency = AccountCurrency.USD,
					color = null,
					balance = 10000000.0,
					position = 8,
				),
			)
		)
	}
	
	val logcat = LocalLogcat.current
	
	DraggableVerticalGrid(
		modifier = Modifier.padding(
			top = 24.dp,
			bottom = 24.dp
		),
		columns = GridCells.Adaptive(128.dp),
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		verticalArrangement = Arrangement.spacedBy(8.dp),
		contentPadding = PaddingValues(8.dp),
		onDragStarted = {
			logcat.debug("onDragStarted")
		},
		onDragCompleted = { from, to ->
			logcat.debug("onDragCompleted: $from -> $to")
		},
		onDragReverted = {
			logcat.debug("onDragReverted")
		},
		onDragMoved = { from, to ->
			accountList = accountList.let { ArrayList(it) }.reordered(from, to)
		}
	
	) {
		items(
			count = accountList.size,
			key = { accountList[it].id },
			itemContent = { index, isDragged ->
				ContentDataItem(
					modifier = Modifier
						.padding()
						.applyIf({ isDragged }) {
							this.shadow(elevation = 4.dp, shape = MaterialTheme.shapes.medium)
						},
					financialAccount = accountList[index],
					onClickAccount = {},
				)
			}
		)
	}
	
}


@Composable
private fun ContentDataItem(
	modifier: Modifier,
	financialAccount: FinancialAccount,
	onClickAccount: (id: Long) -> Unit,
) {
	Card(
		modifier = modifier,
		onClick = { onClickAccount(financialAccount.id) },
		colors = CardDefaults.cardColors(
			containerColor = financialAccount.color?.color ?: Color.Unspecified
		)
	) {
		Column {
			Text(text = financialAccount.name, modifier = Modifier.padding(8.dp))
			Row {
				Text(
					text = financialAccount.currency.symbol,
					modifier = Modifier.padding(horizontal = 8.dp)
				)
				Spacer(modifier = Modifier.weight(1F))
				Text(
					text = financialAccount.balance.asPriceString(showPlus = false),
					modifier = Modifier.padding(horizontal = 8.dp)
				)
			}
			//TODO set real day difference
			Text(
				modifier = Modifier
					.padding(8.dp)
					.fillMaxWidth(),
				text = (-50.7).asPriceString(showPlus = true),
				textAlign = TextAlign.End
			)
		}
	}
}
