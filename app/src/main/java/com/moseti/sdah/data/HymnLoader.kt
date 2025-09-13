package com.moseti.sdah.data

import android.content.Context
import com.moseti.sdah.R
import com.moseti.sdah.models.Hymn
import kotlinx.serialization.json.Json
import java.io.IOException

object HymnLoader {

    // A lenient Json parser instance
    private val json = Json { ignoreUnknownKeys = true }

    fun loadHymns(context: Context): List<Hymn> {
        try {
            // 1. Open the raw resource
            val inputStream = context.resources.openRawResource(R.raw.hymns)

            // 2. Read the file content as a single string
            val jsonString = inputStream.bufferedReader().use { it.readText() }

            // 3. Decode the JSON string into a List of Hymn objects
            return json.decodeFromString<List<Hymn>>(jsonString)

        } catch (e: IOException) {
            // Handle exceptions (e.g., file not found)
            e.printStackTrace()
            return emptyList()
        } catch (e: Exception) {
            // Handle JSON parsing exceptions
            e.printStackTrace()
            return emptyList()
        }
    }
}