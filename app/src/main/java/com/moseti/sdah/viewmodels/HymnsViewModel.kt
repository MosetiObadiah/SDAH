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
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


data class SearchResult(
    val hymn: Hymn,
    val matchingLine: String
)
@OptIn(FlowPreview::class)
class HymnsViewModel(context: Context) : ViewModel() {
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _searchResults = MutableStateFlow<ImmutableList<SearchResult>>(persistentListOf())
    val searchResults = _searchResults.asStateFlow()


    private val _allHymns = MutableStateFlow<ImmutableList<Hymn>>(persistentListOf())
    val allHymns = _allHymns.asStateFlow()
    private val _selectedHymnIndex = MutableStateFlow<Int?>(null)

    // Public, read-only state for the UI to observe
    val selectedHymnIndex: StateFlow<Int?> = _selectedHymnIndex.asStateFlow()

    init {
        viewModelScope.launch {
            // This is the main search logic pipeline
            _searchQuery
                // CRITICAL OPTIMIZATION: Wait for the user to stop typing for 300ms
                .debounce(300L)
                .collect { query ->
                    if (query.isBlank()) {
                        _searchResults.value = persistentListOf()
                        _isSearching.value = false
                        return@collect
                    }

                    _isSearching.value = true
                    // Perform the actual search on a background thread
                    val results = withContext(Dispatchers.Default) {
                        performSearch(query)
                    }
                    _searchResults.value = results
                    _isSearching.value = false
                }
        }
        viewModelScope.launch {
            // withContext switches to a background thread for file I/O
            val hymns = withContext(Dispatchers.IO) {
                HymnLoader.loadHymns(context)
            }
            _allHymns.value = hymns
            _isLoading.value = false
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    private fun performSearch(query: String): ImmutableList<SearchResult> {
        val results = mutableListOf<SearchResult>()
        val hymnList = _allHymns.value // Get the loaded list of hymns

        hymnList.forEach { hymn ->
            // 1. Search in the title
            if (hymn.title.contains(query, ignoreCase = true)) {
                results.add(SearchResult(hymn, hymn.title))
            }

            // 2. Search in verses
            hymn.verses.forEach { verse ->
                verse.lines.forEach { line ->
                    if (line.contains(query, ignoreCase = true)) {
                        // Avoid adding duplicates if title already matched
                        if (results.none { it.hymn.number == hymn.number }) {
                            results.add(SearchResult(hymn, "...$line..."))
                        }
                    }
                }
            }

            // 3. Search in chorus
            hymn.chorus?.lines?.forEach { line ->
                if (line.contains(query, ignoreCase = true)) {
                    if (results.none { it.hymn.number == hymn.number }) {
                        results.add(SearchResult(hymn, "...$line..."))
                    }
                }
            }
        }
        return results.toImmutableList()
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