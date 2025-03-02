package basilliyc.cashnote.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.core.cartesian.Zoom
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries

@Composable
fun LineChart(
	modifier: Modifier = Modifier,
	data: List<Pair<String, Double>>,
	stepY: Double = 1000.0,
) {
	
	//Default labels not working when values updated... hmm..

//	val labelListKey = ExtraStore.Key<List<String>>()
	val modelProducer = remember { CartesianChartModelProducer() }
	LaunchedEffect(data) {
		modelProducer.runTransaction {
			lineSeries {
				series(data.map { it.second })
			}
//			extras { it[labelListKey] = data.map { it.first } }
		}
	}
	
	val labels = remember { HashMap<Int, String>() }
	labels.putAll(data.mapIndexed { index, pair -> index to pair.first })
	
	CartesianChartHost(
		modifier = modifier,
		chart = rememberCartesianChart(
			rememberLineCartesianLayer(),
			startAxis = VerticalAxis.rememberStart(
				itemPlacer = VerticalAxis.ItemPlacer.step({ stepY })
			),
			bottomAxis = HorizontalAxis.rememberBottom(
				valueFormatter = CartesianValueFormatter { context, x, _ ->
//					context.model.extraStore[labelListKey][x.toInt()]
					labels[x.toInt()]!!
				}
			),
		),
		modelProducer = modelProducer,
		zoomState = rememberVicoZoomState(
			zoomEnabled = false,
			minZoom = Zoom.Content,
			maxZoom = Zoom.Content,
		),
		scrollState = rememberVicoScrollState(scrollEnabled = false),
	)
}

@Composable
fun ColumnChart(
	modifier: Modifier = Modifier,
	data: List<Pair<String, Double>>,
	stepY: Double = 1000.0,
) {
	
	//Default labels not working when values updated... hmm..

//	val labelListKey = ExtraStore.Key<List<String>>()
	val modelProducer = remember { CartesianChartModelProducer() }
	LaunchedEffect(data) {
		modelProducer.runTransaction {
			columnSeries {
				series(data.map { it.second })
			}
//			extras { it[labelListKey] = data.map { it.first } }
		}
	}
	
	val labels = remember { HashMap<Int, String>() }
	labels.putAll(data.mapIndexed { index, pair -> index to pair.first })
	
	CartesianChartHost(
		modifier = modifier,
		chart = rememberCartesianChart(
			rememberColumnCartesianLayer(),
			startAxis = VerticalAxis.rememberStart(
				itemPlacer = VerticalAxis.ItemPlacer.step({ stepY })
			),
			bottomAxis = HorizontalAxis.rememberBottom(
				valueFormatter = CartesianValueFormatter { context, x, _ ->
//					context.model.extraStore[labelListKey][x.toInt()]
					labels[x.toInt()]!!
				}
			),
		),
		modelProducer = modelProducer,
		zoomState = rememberVicoZoomState(
			zoomEnabled = false,
			minZoom = Zoom.Content,
			maxZoom = Zoom.Content,
		),
		scrollState = rememberVicoScrollState(scrollEnabled = false),
	)
}