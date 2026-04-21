package com.example.soyle.data.local.dao

import androidx.room.*
import com.example.soyle.data.local.entity.LevelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LevelDao {
    @Query("SELECT * FROM levels")
    fun getAllLevels(): Flow<List<LevelEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLevel(level: LevelEntity)

    @Query("UPDATE levels SET isCompleted = 1 WHERE levelNumber = :levelNumber")
    suspend fun completeLevel(levelNumber: Int)

    @Query("UPDATE levels SET isUnlocked = 1 WHERE levelNumber = :levelNumber")
    suspend fun unlockLevel(levelNumber: Int)

    @Query("SELECT COUNT(*) FROM levels")
    suspend fun getLevelCount(): Int
}
