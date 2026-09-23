package com.lagradost.cloudstream3.data.repository

import com.lagradost.cloudstream3.data.local.ContinueWatching
import com.lagradost.cloudstream3.data.local.ContinueWatchingDao
import kotlinx.coroutines.flow.Flow

class AppRepository(private val continueWatchingDao: ContinueWatchingDao) {
    val continueWatchingItems: Flow<List<ContinueWatching>> = continueWatchingDao.getAll()

    suspend fun insertContinueWatching(item: ContinueWatching) {
        continueWatchingDao.insert(item)
    }

    suspend fun removeContinueWatching(url: String) {
        continueWatchingDao.deleteByUrl(url)
    }
}
