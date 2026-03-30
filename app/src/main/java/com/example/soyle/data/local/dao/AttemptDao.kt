package com.example.soyle.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AttemptDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attempt: com.example.soyle.data.local.entity.AttemptEntity)

    /** Все попытки по фонеме — Flow автообновляет график в UI */
    @Query("""
        SELECT * FROM attempts 
        WHERE userId = :userId AND phoneme = :phoneme 
        ORDER BY timestamp DESC
    """)
    fun getByPhoneme(userId: String, phoneme: String): Flow<List<com.example.soyle.data.local.entity.AttemptEntity>>

    @Query("""
        SELECT * FROM attempts
        WHERE userId = :userId
        ORDER BY timestamp DESC
    """)
    fun getAllByUser(userId: String): Flow<List<com.example.soyle.data.local.entity.AttemptEntity>>

    /** Средний score за последние N дней */
    @Query("""
        SELECT AVG(score) FROM attempts 
        WHERE userId = :userId 
          AND phoneme = :phoneme 
          AND timestamp >= :sinceTimestamp
    """)
    suspend fun avgScoreSince(
        userId         : String,
        phoneme        : String,
        sinceTimestamp : Long
    ): Float?

    /** Последние попытки для HomeScreen */
    @Query("""
        SELECT * FROM attempts 
        WHERE userId = :userId 
        ORDER BY timestamp DESC 
        LIMIT :limit
    """)
    suspend fun getRecent(userId: String, limit: Int = 10): List<com.example.soyle.data.local.entity.AttemptEntity>

    /** Количество тренировочных дней (для streak) */
    @Query("""
        SELECT COUNT(DISTINCT date(timestamp / 1000, 'unixepoch')) 
        FROM attempts 
        WHERE userId = :userId
    """)
    suspend fun getUniqueDaysCount(userId: String): Int
}
