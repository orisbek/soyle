package com.example.soyle.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.soyle.data.local.dao.AttemptDao
import com.example.soyle.data.local.dao.ExerciseDao
import com.example.soyle.data.local.entity.AttemptEntity
import com.example.soyle.data.local.entity.ExerciseEntity

@Database(
    entities     = [AttemptEntity::class, ExerciseEntity::class],
    version      = 1,
    exportSchema = false
)
abstract class SoyleDatabase : RoomDatabase() {
    abstract fun attemptDao(): AttemptDao
    abstract fun exerciseDao(): ExerciseDao
}