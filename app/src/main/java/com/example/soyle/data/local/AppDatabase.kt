package com.example.soyle.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.soyle.data.local.dao.AttemptDao
import com.example.soyle.data.local.dao.LevelDao
import com.example.soyle.data.local.entity.AttemptEntity
import com.example.soyle.data.local.entity.LevelEntity

@Database(
    entities  = [AttemptEntity::class, LevelEntity::class],
    version   = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun attemptDao(): AttemptDao
    abstract fun levelDao(): LevelDao
}
