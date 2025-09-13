package com.moseti.sdah.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.moseti.sdah.models.Hymn

class SingleHymnViewModel(private val hymnsList: List<Hymn>) : ViewModel() {
    // use hymnsList here

    private fun formatHymnsForDisplay(hymns: List<Hymn>): String {
        val builder = StringBuilder()
        hymns.forEach { hymn ->
            builder.append("Hymn ${hymn.number}: ${hymn.title}\n")
            builder.append("--------------------------------\n")

            hymn.verses.forEach { verse ->
                builder.append("Verse ${verse.number}\n")
                verse.lines.forEach { line ->
                    builder.append(line).append("\n")
                }
                builder.append("\n")
            }

            // Safely handle the nullable chorus
            hymn.chorus?.let { chorus ->
                builder.append("Chorus\n")
                chorus.lines.forEach { line ->
                    builder.append(line).append("\n")
                }
                builder.append("\n")
            }

            builder.append("\n================================\n\n")
        }
        return builder.toString()
    }
}

class SingleHymnViewModelFactory(
    private val hymnsList: List<Hymn>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SingleHymnViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SingleHymnViewModel(hymnsList) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
