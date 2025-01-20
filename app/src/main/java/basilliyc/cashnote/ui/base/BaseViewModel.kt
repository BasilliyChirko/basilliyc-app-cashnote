package basilliyc.cashnote.ui.base

import androidx.lifecycle.ViewModel
import basilliyc.cashnote.utils.Logcat

abstract class BaseViewModel : ViewModel() {
	
	val logcat = Logcat(this)
	
}