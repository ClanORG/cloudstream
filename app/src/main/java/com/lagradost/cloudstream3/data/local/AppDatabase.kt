package com.lagradost.cloudstream3.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(entities = [ContinueWatching::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun continueWatchingDao(): ContinueWatchingDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cloudstream_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
