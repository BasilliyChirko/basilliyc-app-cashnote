package basilliyc.cashnote.utils

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import okhttp3.logging.HttpLoggingInterceptor
import java.lang.StringBuilder


class FullPrintHttpLogging : HttpLoggingInterceptor.Logger {
	
	private val logcat = Logcat("HTTP")
	private val gson = GsonBuilder().disableHtmlEscaping().serializeNulls().setPrettyPrinting().create()
	private val buffer = ArrayList<String>()
	
	override fun log(message: String) {
		if (message.startsWith("{") || message.startsWith("[")) {
			try {
				val jsonString = gson.toJson(JsonParser.parseString(message))
				addToBuffer(jsonString, isJson = true)
				return
			} catch (ignore: JsonSyntaxException) {
			}
		}
		
		addToBuffer(message)
	}
	
	private fun addToBuffer(message: String, isJson: Boolean = false) {
		if (message.isBlank()) return
		if (!isJson) {
			buffer.add(message)
			return
		}
		buffer.add(message)
	}
	
	fun flush() {
		val builder = StringBuilder()
		
		buffer.forEach {
			if (builder.length + it.length > Logcat.LOG_MAX_LENGTH) {
				logcat.info(builder.toString())
				builder.clear()
			}
			builder.appendLine(it)
		}
		
		if (builder.isNotEmpty()) {
			logcat.info(builder.toString())
		}
		
		builder.clear()
		buffer.clear()
	}
	
	
	
}