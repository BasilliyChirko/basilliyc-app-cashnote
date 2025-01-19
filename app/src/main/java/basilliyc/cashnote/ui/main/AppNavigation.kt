package basilliyc.cashnote.ui.main

import kotlinx.serialization.Serializable

sealed interface AppNavigation {
	
	@Serializable
	data object Page1 : AppNavigation
	
	@Serializable
	data class Page2(val number: Int) : AppNavigation
	
	@Serializable
	data class PageTest3(val testInput: Int) : AppNavigation
	
}

