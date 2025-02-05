package basilliyc.cashnote.ui.activity

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import basilliyc.cashnote.backend.manager.FinancialManager
import basilliyc.cashnote.backend.preferences.AppPreferences
import basilliyc.cashnote.ui.base.BaseViewModel
import basilliyc.cashnote.utils.inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainViewModel : BaseViewModel() {
	
	private val financialManager: FinancialManager by inject()
	private val preferences: AppPreferences by inject()
	
	private val _state: MutableStateFlow<MainState>
	
	init {
		val account = preferences.accountIdOnNavigation?.let {
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
			preferences.accountIdOnNavigationAsFlow().collectLatest {
				
				if (it != _state.value.accountOnNavigation?.id) {
					_state.value = _state.value.copy(
						isNeedRestartActivity = true
					)
				}
				
//				_state.value = _state.value.copy(
//					accountOnNavigation = it?.let { accountId ->
//						financialManager.getAccountById(accountId)
//					}
//				)
			}
		}
	}
	
	val state = _state.asStateFlow()
	
	
}