package com.moseti.sdah.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.moseti.sdah.data.HymnLoader
import com.moseti.sdah.models.Hymn
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HymnsViewModel(context: Context) : ViewModel() { // <-- Change constructor
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _allHymns = MutableStateFlow<ImmutableList<Hymn>>(persistentListOf())
    val allHymns = _allHymns.asStateFlow()
    private val _selectedHymnIndex = MutableStateFlow<Int?>(null)

    // Public, read-only state for the UI to observe
    val selectedHymnIndex: StateFlow<Int?> = _selectedHymnIndex.asStateFlow()

    init {
        viewModelScope.launch {
            // withContext switches to a background thread for file I/O
            val hymns = withContext(Dispatchers.IO) {
                HymnLoader.loadHymns(context)
            }
            _allHymns.value = hymns
            _isLoading.value = false
        }
    }

    /**
     * Sets the currently selected hymn by its index in the list.
     */
    fun selectHymn(index: Int) {
        if (index in _allHymns.value.indices) {
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

object HymnsViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(HymnsViewModel::class.java)) {
            val context = extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]!!.applicationContext
            @Suppress("UNCHECKED_CAST")
            return HymnsViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}