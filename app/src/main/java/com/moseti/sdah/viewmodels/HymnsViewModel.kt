package com.moseti.sdah.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.moseti.sdah.models.Hymn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HymnsViewModel(val allHymns: List<Hymn>) : ViewModel() {
    private val _selectedHymnIndex = MutableStateFlow<Int?>(null)

    // Public, read-only state for the UI to observe
    val selectedHymnIndex: StateFlow<Int?> = _selectedHymnIndex.asStateFlow()

    /**
     * Sets the currently selected hymn by its index in the list.
     */
    fun selectHymn(index: Int) {
        if (index in allHymns.indices) {
            _selectedHymnIndex.value = index
        }
    }

    /**
     * Clears the hymn selection, useful when navigating back to the list.
     */
    fun clearHymnSelection() {
        _selectedHymnIndex.value = null
    }
}

class HymnsViewModelFactory(
    private val hymnsList: List<Hymn>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HymnsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HymnsViewModel(hymnsList) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}