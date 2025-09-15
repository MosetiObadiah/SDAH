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
private data class IndexEntry(val hymn: Hymn, val line: String) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as IndexEntry
        return hymn.number == other.hymn.number
    }

    override fun hashCode(): Int {
        return hymn.number.hashCode()
    }
}

@OptIn(FlowPreview::class)
class HymnsViewModel(context: Context) : ViewModel() {

    // loading states
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _allHymns = MutableStateFlow<ImmutableList<Hymn>>(persistentListOf())
    val allHymns: StateFlow<ImmutableList<Hymn>> = _allHymns

    private val _selectedHymnIndex = MutableStateFlow<Int?>(null)
    val selectedHymnIndex: StateFlow<Int?> = _selectedHymnIndex

    // search states
    private val searchIndex = mutableMapOf<String, MutableList<IndexEntry>>()
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery
    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching
    private val _searchResults = MutableStateFlow<ImmutableList<SearchResult>>(persistentListOf())
    val searchResults: StateFlow<ImmutableList<SearchResult>> = _searchResults

    init {
        // load hymns from JSON
        viewModelScope.launch {
            val hymns = withContext(Dispatchers.IO) { HymnLoader.loadHymns(context) }
            _allHymns.value = hymns

            // build the search index on a background thread after hymns are loaded
            withContext(Dispatchers.Default) { buildSearchIndex(hymns) }

            _isLoading.value = false
        }

        // set up the search pipeline to react to query changes
        viewModelScope.launch {
            _searchQuery
                // wait for the user to stop typing for 300ms before searching
                .debounce(300L)
                .collect { query ->
                    if (query.trim().length < 2) { // no single letter search
                        _searchResults.value = persistentListOf()
                        _isSearching.value = false
                        return@collect
                    }

                    _isSearching.value = true
                    // perform search on a background thread
                    val results = withContext(Dispatchers.Default) { performIndexedSearch(query) }
                    _searchResults.value = results
                    _isSearching.value = false
                }
        }
    }

    // event handlers
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun selectHymn(index: Int) {
        if (index in _allHymns.value.indices) {
            _selectedHymnIndex.value = index
        }
    }

    fun clearHymnSelection() {
        _selectedHymnIndex.value = null
    }

    // search logic
    private fun buildSearchIndex(hymns: ImmutableList<Hymn>) {
        hymns.forEach { hymn ->
            // Function to process and index a line of text
            val indexer = { line: String, entry: IndexEntry ->
                // Split on any non-alphanumeric character and process unique words
                line.lowercase().split(Regex("[^a-zA-Z0-9]+")).toSet().forEach { word ->
                    if (word.isNotBlank()) {
                        searchIndex.getOrPut(word) { mutableListOf() }.add(entry)
                    }
                }
            }

            // Index title verses chorus
            indexer(hymn.title, IndexEntry(hymn, hymn.title))
            hymn.verses.forEach { verse -> verse.lines.forEach { line -> indexer(line, IndexEntry(hymn, "...$line...")) } }
            hymn.chorus?.lines?.forEach { line -> indexer(line, IndexEntry(hymn, "...$line...")) }
        }
    }

    private fun performIndexedSearch(query: String): ImmutableList<SearchResult> {
        // clean the user's query into a list of words
        val queryWords = query.lowercase().split(Regex("[^a-zA-Z0-9]+")).filter { it.isNotBlank() }
        if (queryWords.isEmpty()) return persistentListOf()

        // get the initial set of matching hymns for the *first* word. Using a Set is key to fixing the crash.
        val initialResults = searchIndex[queryWords.first()]?.toSet() ?: return persistentListOf()

        // filter down the results by finding the intersection with each subsequent word's results.
        val finalResults = queryWords.drop(1).fold(initialResults) { currentResults, word ->
            val nextWordResults = searchIndex[word]?.toSet() ?: emptySet()
            currentResults.intersect(nextWordResults)
        }

        // convert the final unique entries into a displayable list sorted by hymn number.
        return finalResults
            .map { SearchResult(it.hymn, it.line) }
            .sortedBy { it.hymn.number }
            .toImmutableList()
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