package basilliyc.cashnote.ui.main

import androidx.lifecycle.viewModelScope
import basilliyc.cashnote.ui.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainViewModel : BaseViewModel() {
	
	private val _state: MutableStateFlow<MainState>
	
	init {
		val account = preferences.accountIdOnNavigation.value?.let {
			runBlocking {
				financialManager.getAccountById(it)
			}
		}
		
		_state = MutableStateFlow(
			MainState(
				accountOnNavigation = account,
			)
		)
		
		viewModelScope.launch {
			preferences.accountIdOnNavigation.flow.collectLatest { id ->
				val id = id?.let { financialManager.getAccountById(id)?.id }
				
				if (id != _state.value.accountOnNavigation?.id) {
					_state.value = _state.value.copy(
						isNeedRestartActivity = true
					)
				}
			}
		}
	}
	
	val state = _state.asStateFlow()
	
	
}