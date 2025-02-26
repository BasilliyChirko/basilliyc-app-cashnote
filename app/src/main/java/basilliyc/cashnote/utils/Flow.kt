package basilliyc.cashnote.utils

import co.touchlab.stately.concurrency.AtomicBoolean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicReference

private class ZippedFlowInstance<T>(
	val isValueSet: AtomicBoolean = AtomicBoolean(false),
	val value: AtomicReference<T> = AtomicReference(),
	val flow: Flow<T>,
) {
	fun set(value: T) {
		this.value.set(value)
		isValueSet.value = true
	}
	
	fun get(): T {
		return value.get()
	}
	
	suspend fun collect(also: () -> Unit) {
		flow.collectLatest {
			set(it)
			also()
		}
	}
}

fun <T1, R> CoroutineScope.flowZip(
	flow1: Flow<T1>,
	transform: (T1) -> R,
): Flow<R> {
	
	val flows = ArrayList<ZippedFlowInstance<*>>()
	val zippedFlow1 = ZippedFlowInstance(flow = flow1).also { flows.add(it) }
	val resultFlow = MutableSharedFlow<R>()
	
	fun together() {
		if (flows.all { it.isValueSet.value }) {
			val result = transform(
				zippedFlow1.get(),
			)
			launch { resultFlow.emit(result) }
		}
	}
	
	flows.forEach {
		launch { it.collect { together() } }
	}
	
	return resultFlow
}

fun <T1, T2, R> CoroutineScope.flowZip(
	flow1: Flow<T1>,
	flow2: Flow<T2>,
	transform: (T1, T2) -> R,
): Flow<R> {
	
	val flows = ArrayList<ZippedFlowInstance<*>>()
	val zippedFlow1 = ZippedFlowInstance(flow = flow1).also { flows.add(it) }
	val zippedFlow2 = ZippedFlowInstance(flow = flow2).also { flows.add(it) }
	val resultFlow = MutableSharedFlow<R>()
	
	fun together() {
		if (flows.all { it.isValueSet.value }) {
			val result = transform(
				zippedFlow1.get(),
				zippedFlow2.get(),
			)
			launch { resultFlow.emit(result) }
		}
	}
	
	flows.forEach {
		launch { it.collect { together() } }
	}
	
	return resultFlow
}

fun <T1, T2, T3, R> CoroutineScope.flowZip(
	flow1: Flow<T1>,
	flow2: Flow<T2>,
	flow3: Flow<T3>,
	transform: (T1, T2, T3) -> R,
): Flow<R> {
	
	val flows = ArrayList<ZippedFlowInstance<*>>()
	val zippedFlow1 = ZippedFlowInstance(flow = flow1).also { flows.add(it) }
	val zippedFlow2 = ZippedFlowInstance(flow = flow2).also { flows.add(it) }
	val zippedFlow3 = ZippedFlowInstance(flow = flow3).also { flows.add(it) }
	val resultFlow = MutableSharedFlow<R>()
	
	fun together() {
		if (flows.all { it.isValueSet.value }) {
			val result = transform(
				zippedFlow1.get(),
				zippedFlow2.get(),
				zippedFlow3.get(),
			)
			launch { resultFlow.emit(result) }
		}
	}
	
	flows.forEach {
		launch { it.collect { together() } }
	}
	
	return resultFlow
}

fun <T1, T2, T3, T4, R> CoroutineScope.flowZip(
	flow1: Flow<T1>,
	flow2: Flow<T2>,
	flow3: Flow<T3>,
	flow4: Flow<T4>,
	transform: (T1, T2, T3, T4) -> R,
): Flow<R> {
	
	val flows = ArrayList<ZippedFlowInstance<*>>()
	val zippedFlow1 = ZippedFlowInstance(flow = flow1).also { flows.add(it) }
	val zippedFlow2 = ZippedFlowInstance(flow = flow2).also { flows.add(it) }
	val zippedFlow3 = ZippedFlowInstance(flow = flow3).also { flows.add(it) }
	val zippedFlow4 = ZippedFlowInstance(flow = flow4).also { flows.add(it) }
	val resultFlow = MutableSharedFlow<R>()
	
	fun together() {
		if (flows.all { it.isValueSet.value }) {
			val result = transform(
				zippedFlow1.get(),
				zippedFlow2.get(),
				zippedFlow3.get(),
				zippedFlow4.get(),
			)
			launch { resultFlow.emit(result) }
		}
	}
	
	flows.forEach {
		launch { it.collect { together() } }
	}
	
	return resultFlow
}

fun <T1, T2, T3, T4, T5, R> CoroutineScope.flowZip(
	flow1: Flow<T1>,
	flow2: Flow<T2>,
	flow3: Flow<T3>,
	flow4: Flow<T4>,
	flow5: Flow<T5>,
	transform: (T1, T2, T3, T4, T5) -> R,
): Flow<R> {
	
	val flows = ArrayList<ZippedFlowInstance<*>>()
	val zippedFlow1 = ZippedFlowInstance(flow = flow1).also { flows.add(it) }
	val zippedFlow2 = ZippedFlowInstance(flow = flow2).also { flows.add(it) }
	val zippedFlow3 = ZippedFlowInstance(flow = flow3).also { flows.add(it) }
	val zippedFlow4 = ZippedFlowInstance(flow = flow4).also { flows.add(it) }
	val zippedFlow5 = ZippedFlowInstance(flow = flow5).also { flows.add(it) }
	val resultFlow = MutableSharedFlow<R>()
	
	fun together() {
		if (flows.all { it.isValueSet.value }) {
			val result = transform(
				zippedFlow1.get(),
				zippedFlow2.get(),
				zippedFlow3.get(),
				zippedFlow4.get(),
				zippedFlow5.get(),
			)
			launch { resultFlow.emit(result) }
		}
	}
	
	flows.forEach {
		launch { it.collect { together() } }
	}
	
	return resultFlow
}

fun <T1, T2, T3, T4, T5, T6, R> CoroutineScope.flowZip(
	flow1: Flow<T1>,
	flow2: Flow<T2>,
	flow3: Flow<T3>,
	flow4: Flow<T4>,
	flow5: Flow<T5>,
	flow6: Flow<T6>,
	transform: (T1, T2, T3, T4, T5, T6) -> R,
): Flow<R> {
	
	val flows = ArrayList<ZippedFlowInstance<*>>()
	val zippedFlow1 = ZippedFlowInstance(flow = flow1).also { flows.add(it) }
	val zippedFlow2 = ZippedFlowInstance(flow = flow2).also { flows.add(it) }
	val zippedFlow3 = ZippedFlowInstance(flow = flow3).also { flows.add(it) }
	val zippedFlow4 = ZippedFlowInstance(flow = flow4).also { flows.add(it) }
	val zippedFlow5 = ZippedFlowInstance(flow = flow5).also { flows.add(it) }
	val zippedFlow6 = ZippedFlowInstance(flow = flow6).also { flows.add(it) }
	val resultFlow = MutableSharedFlow<R>()
	
	fun together() {
		if (flows.all { it.isValueSet.value }) {
			val result = transform(
				zippedFlow1.get(),
				zippedFlow2.get(),
				zippedFlow3.get(),
				zippedFlow4.get(),
				zippedFlow5.get(),
				zippedFlow6.get(),
			)
			launch { resultFlow.emit(result) }
		}
	}
	
	flows.forEach {
		launch { it.collect { together() } }
	}
	
	return resultFlow
}


