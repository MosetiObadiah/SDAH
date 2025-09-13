package com.moseti.sdah.data

import android.content.Context
import com.moseti.sdah.R
import com.moseti.sdah.models.Hymn
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.serialization.json.Json
import java.io.IOException

object HymnLoader {

    // Json parser instance
    private val json = Json { ignoreUnknownKeys = true }

    fun loadHymns(context: Context): ImmutableList<Hymn> {
        try {
            // Open the raw resource
            val inputStream = context.resources.openRawResource(R.raw.hymns)

            // Read the file content as a single string
            val jsonString = inputStream.bufferedReader().use { it.readText() }

            val hymnList: List<Hymn> = json.decodeFromString<List<Hymn>>(jsonString)
            return hymnList.toImmutableList()

        } catch (e: IOException) {
            //Todo Handle exceptions, file not found
            e.printStackTrace()
            return persistentListOf()
        } catch (e: Exception) {
            // Todo handle JSON parsing exceptions
            e.printStackTrace()
            return persistentListOf()
        }
    }
}