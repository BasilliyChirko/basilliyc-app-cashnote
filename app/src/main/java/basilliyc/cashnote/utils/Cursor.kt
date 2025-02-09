package basilliyc.cashnote.utils

import android.database.CharArrayBuffer
import android.database.Cursor
import androidx.core.database.*

inline fun <T> Cursor.map(block: (Cursor) -> T): List<T> {
	val list = mutableListOf<T>()
	
	while (moveToNext()) {
		list.add(block(this))
	}
	
	return list
}


fun Cursor.getInt(columnName: String): Int {
	val columnIndex = getColumnIndex(columnName)
	return getInt(columnIndex)
}

fun Cursor.getLong(columnName: String): Long {
	val columnIndex = getColumnIndex(columnName)
	return getLong(columnIndex)
}

fun Cursor.getString(columnName: String): String {
	val columnIndex = getColumnIndex(columnName)
	return getString(columnIndex)
}

fun Cursor.getShort(columnName: String): Short {
	val columnIndex = getColumnIndex(columnName)
	return getShort(columnIndex)
}

fun Cursor.getFloat(columnName: String): Float {
	val columnIndex = getColumnIndex(columnName)
	return getFloat(columnIndex)
}

fun Cursor.getDouble(columnName: String): Double {
	val columnIndex = getColumnIndex(columnName)
	return getDouble(columnIndex)
}

fun Cursor.getBlob(columnName: String): ByteArray {
	val columnIndex = getColumnIndex(columnName)
	return getBlob(columnIndex)
}

fun Cursor.isNull(columnName: String): Boolean {
	val columnIndex = getColumnIndex(columnName)
	return isNull(columnIndex)
}

fun Cursor.getType(columnName: String): Int {
	val columnIndex = getColumnIndex(columnName)
	return getType(columnIndex)
}

fun Cursor.copyStringToBuffer(columnName: String, buffer: CharArrayBuffer) {
	val columnIndex = getColumnIndex(columnName)
	copyStringToBuffer(columnIndex, buffer)
}






fun Cursor.getIntOrNull(columnName: String): Int? {
	val columnIndex = getColumnIndex(columnName)
	return getIntOrNull(columnIndex)
}

fun Cursor.getLongOrNull(columnName: String): Long? {
	val columnIndex = getColumnIndex(columnName)
	return getLongOrNull(columnIndex)
}

fun Cursor.getStringOrNull(columnName: String): String? {
	val columnIndex = getColumnIndex(columnName)
	return getStringOrNull(columnIndex)
}

fun Cursor.getShortOrNull(columnName: String): Short? {
	val columnIndex = getColumnIndex(columnName)
	return getShortOrNull(columnIndex)
}

fun Cursor.getFloatOrNull(columnName: String): Float? {
	val columnIndex = getColumnIndex(columnName)
	return getFloatOrNull(columnIndex)
}

fun Cursor.getDoubleOrNull(columnName: String): Double? {
	val columnIndex = getColumnIndex(columnName)
	return getDoubleOrNull(columnIndex)
}

fun Cursor.getBlobOrNull(columnName: String): ByteArray? {
	val columnIndex = getColumnIndex(columnName)
	return getBlobOrNull(columnIndex)
}