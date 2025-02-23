package basilliyc.cashnote.ui.statistic

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import basilliyc.cashnote.utils.DefaultPreview

@Composable
fun AccountStatistic() {
	Column(
		modifier = Modifier.fillMaxSize(),
		verticalArrangement = Arrangement.Center,
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Text(text = "Statistic")
		Text(text = "Not implemented yet")
	}
}


@Preview(showBackground = true)
@Composable
fun AccountStatisticPreview() = DefaultPreview {
	AccountStatistic()
}