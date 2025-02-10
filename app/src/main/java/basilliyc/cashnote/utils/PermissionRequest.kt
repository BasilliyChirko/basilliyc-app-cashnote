package basilliyc.cashnote.utils

import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import basilliyc.cashnote.ui.base.BaseViewModel.ActivityRequestLauncher

class PermissionRequest(private val permissions: List<String>) {
	
	constructor(vararg permissions: String) : this(permissions.toList())
	
	data class Result(
		val granted: Set<String>,
		val denied: Set<String>,
		val rationale: Set<String>,
	) {
		enum class State {
			Granted, Rationale, Denied
		}
		
		val state: State
			get() = when {
				denied.isNotEmpty() -> State.Denied
				rationale.isNotEmpty() -> State.Rationale
				else -> State.Granted
			}
		
		fun isGranted(): Boolean = state == State.Granted
		fun isDenied(): Boolean = state == State.Denied
	}
	
	fun check(activity: AppCompatActivity): Result {
		val granted = HashSet<String>()
		val denied = HashSet<String>()
		val rationale = HashSet<String>()
		
		permissions.forEach { permission ->
			when {
				ContextCompat.checkSelfPermission(
					activity,
					permission
				) == PackageManager.PERMISSION_GRANTED -> granted.add(permission)
				
				ActivityCompat.shouldShowRequestPermissionRationale(
					activity,
					permission
				) -> rationale.add(permission)
				
				else -> denied.add(permission)
			}
		}
		
		return Result(
			granted = granted,
			denied = denied,
			rationale = rationale,
		)
	}
	
	val requestPermissionSuspendLauncher = ActivityRequestLauncher(
		ActivityResultContracts.RequestMultiplePermissions(),
		null,
		permissions.toTypedArray(),
		null
	)
	
	suspend fun request(): Boolean {
		return requestPermissionSuspendLauncher.launch().all { it.value }
	}
	
	suspend fun checkAndRequest(activity: AppCompatActivity): Result {
		val firstResult = check(activity)
		
		val denied = HashSet(firstResult.denied)
		val granted = HashSet(firstResult.granted)
		if (denied.isNotEmpty()) {
			val permissionResponseMap = requestPermissionSuspendLauncher.launch()
			permissionResponseMap.forEach { (key, value) ->
				if (value) {
					denied.remove(key)
					granted.add(key)
				}
			}
		}
		
		return firstResult.copy(
			denied = denied,
			granted = granted,
		)
	}
	
}