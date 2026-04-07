package com.example.soyle.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.soyle.data.local.dao.AttemptDao
import com.example.soyle.data.local.entity.AttemptEntity

@Database(
    entities  = [AttemptEntity::class],
    version   = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun attemptDao(): AttemptDao
}
