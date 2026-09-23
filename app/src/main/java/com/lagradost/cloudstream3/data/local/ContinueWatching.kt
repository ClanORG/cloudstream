package com.lagradost.cloudstream3.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "continue_watching")
data class ContinueWatching(
    @PrimaryKey val url: String,
    val title: String,
    val poster: String?,
    val apiName: String,
    val progress: Long,
    val duration: Long,
    val timestamp: Long = System.currentTimeMillis()
)
