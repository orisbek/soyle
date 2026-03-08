package com.example.soyle.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.soyle.data.local.entity.ExerciseEntity

@Dao
interface ExerciseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(exercises: List<ExerciseEntity>)

    @Query("SELECT * FROM exercises WHERE phoneme = :phoneme AND mode = :mode")
    suspend fun getByPhonemeAndMode(phoneme: String, mode: String): List<ExerciseEntity>

    @Query("SELECT * FROM exercises")
    suspend fun getAll(): List<ExerciseEntity>

    /** Удалить кеш старше 24 часов */
    @Query("DELETE FROM exercises WHERE cachedAt < :threshold")
    suspend fun clearOldCache(threshold: Long)

    @Query("SELECT COUNT(*) FROM exercises")
    suspend fun count(): Int
}