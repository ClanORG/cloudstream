package com.lagradost.cloudstream3.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ContinueWatchingDao {
    @Query("SELECT * FROM continue_watching ORDER BY timestamp DESC")
    fun getAll(): Flow<List<ContinueWatching>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ContinueWatching)

    @Query("DELETE FROM continue_watching WHERE url = :url")
    suspend fun deleteByUrl(url: String)
}
