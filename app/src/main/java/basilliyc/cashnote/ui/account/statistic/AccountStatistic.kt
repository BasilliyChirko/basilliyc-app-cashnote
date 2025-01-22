package basilliyc.cashnote.ui.account.statistic

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import basilliyc.cashnote.utils.DefaultPreview

@Composable
fun AccountStatistic() {
	Text(text = "AccountStatistic")
}


@Preview(showBackground = true)
@Composable
fun AccountStatisticPreview() = DefaultPreview {
	AccountStatistic()
}