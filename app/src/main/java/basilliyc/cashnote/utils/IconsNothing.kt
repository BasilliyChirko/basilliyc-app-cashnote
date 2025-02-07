package basilliyc.cashnote.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

public val Icons.Nothing: ImageVector
	get() {
		if (nothing != null) {
			return nothing!!
		}
		nothing = materialIcon(name = "Nothing") {
			materialPath {}
		}
		return nothing!!
	}

private var nothing: ImageVector? = null
