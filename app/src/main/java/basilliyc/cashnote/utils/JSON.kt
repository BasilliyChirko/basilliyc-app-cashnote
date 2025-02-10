package basilliyc.cashnote.utils

import org.json.JSONArray
import org.json.JSONObject

fun JSONArray.toListJsonObject(): List<JSONObject> {
	val list = mutableListOf<JSONObject>()
	for (i in 0 until this.length()) {
		list.add(this.getJSONObject(i))
	}
	return list
}

fun JSONObject.getListJsonObject(name : String): List<JSONObject> {
	return this.getJSONArray(name).toListJsonObject()
}

fun JSONObject.getStringOrNull(name: String): String? {
	return if (this.isNull(name)) {
		null
	} else {
		getString(name)
	}
}

fun JSONObject.getIntOrNull(name: String): Int? {
	return if (this.isNull(name)) {
		null
	} else {
		getInt(name)
	}
}

fun JSONObject.getLongOrNull(name: String): Long? {
	return if (this.isNull(name)) {
		null
	} else {
		getLong(name)
	}
}

fun JSONObject.getDoubleOrNull(name: String): Double? {
	return if (this.isNull(name)) {
		null
	} else {
		getDouble(name)
	}
}

fun JSONObject.getBooleanOrNull(name: String): Boolean? {
	return if (this.isNull(name)) {
		null
	} else {
		getBoolean(name)
	}
}

fun JSONObject.getJsonObjectOrNull(name: String): JSONObject? {
	return if (this.isNull(name)) {
		null
	} else {
		getJSONObject(name)
	}
}

fun JSONObject.getJsonArrayOrNull(name: String): JSONArray? {
	return if (this.isNull(name)) {
		null
	} else {
		getJSONArray(name)
	}
}

fun JSONObject.getListJsonObjectOrNull(name: String): List<JSONObject>? {
	return if (this.isNull(name)) {
		null
	} else {
		getListJsonObject(name)
	}
}

