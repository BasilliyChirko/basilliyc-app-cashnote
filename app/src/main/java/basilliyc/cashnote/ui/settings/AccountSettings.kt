package basilliyc.cashnote.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import basilliyc.cashnote.utils.DefaultPreview

@Composable
fun AppSettings() {
	Scaffold {
		Column(modifier = Modifier.padding(it)) {
			Text("displayLarge", style = MaterialTheme.typography.displayLarge)
			Text("displayMedium", style = MaterialTheme.typography.displayMedium)
			Text("displaySmall", style = MaterialTheme.typography.displaySmall)
			Text("headlineLarge", style = MaterialTheme.typography.headlineLarge)
			Text("headlineMedium", style = MaterialTheme.typography.headlineMedium)
			Text("headlineSmall", style = MaterialTheme.typography.headlineSmall)
			Text("titleLarge", style = MaterialTheme.typography.titleLarge)
			Text("titleMedium", style = MaterialTheme.typography.titleMedium)
			Text("titleSmall", style = MaterialTheme.typography.titleSmall)
			Text("bodyLarge", style = MaterialTheme.typography.bodyLarge)
			Text("bodyMedium", style = MaterialTheme.typography.bodyMedium)
			Text("bodySmall", style = MaterialTheme.typography.bodySmall)
			Text("labelLarge", style = MaterialTheme.typography.labelLarge)
			Text("labelMedium", style = MaterialTheme.typography.labelMedium)
			Text("labelSmall", style = MaterialTheme.typography.labelSmall)
		}
	}
}


@Preview(showBackground = true)
@Composable
fun AppSettingsPreview() = DefaultPreview {
	AppSettings()
}