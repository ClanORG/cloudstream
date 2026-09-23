package com.lagradost.cloudstream3.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lagradost.cloudstream3.data.local.ContinueWatching
import com.lagradost.cloudstream3.data.repository.AppRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: AppRepository) : ViewModel() {
    val continueWatching: StateFlow<List<ContinueWatching>> = repository.continueWatchingItems
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addDummyItem() {
        viewModelScope.launch {
            repository.insertContinueWatching(
                ContinueWatching(
                    url = "https://example.com/video",
                    title = "Awesome Movie",
                    poster = "https://via.placeholder.com/150",
                    apiName = "DemoAPI",
                    progress = 500,
                    duration = 1000
                )
            )
        }
    }
}

class HomeViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
