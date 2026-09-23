package com.lagradost.cloudstream3.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lagradost.cloudstream3.data.api.SearchResponse
import com.lagradost.cloudstream3.data.api.providers.DemoProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    private val providers = listOf(DemoProvider())
    
    private val _searchResults = MutableStateFlow<List<SearchResponse>>(emptyList())
    val searchResults: StateFlow<List<SearchResponse>> = _searchResults
    
    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    fun search(query: String) {
        if (query.isBlank()) return
        
        viewModelScope.launch {
            _isSearching.value = true
            val results = mutableListOf<SearchResponse>()
            providers.forEach { provider ->
                results.addAll(provider.search(query))
            }
            _searchResults.value = results
            _isSearching.value = false
        }
    }
}
