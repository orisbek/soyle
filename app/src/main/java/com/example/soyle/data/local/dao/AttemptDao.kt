package com.example.soyle.data.local.dao

import androidx.room.*
import com.example.soyle.data.local.entity.AttemptEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AttemptDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attempt: AttemptEntity)

    /** Все попытки пользователя — для экрана прогресса */
    @Query("SELECT * FROM attempts WHERE userId = :userId ORDER BY timestamp DESC")
    fun getAttemptsForUser(userId: String): Flow<List<AttemptEntity>>

    /** Последние N попыток по конкретной фонеме */
    @Query("""
        SELECT * FROM attempts 
        WHERE userId = :userId AND phoneme = :phoneme 
        ORDER BY timestamp DESC 
        LIMIT :limit
    """)
    suspend fun getRecentAttempts(userId: String, phoneme: String, limit: Int = 10): List<AttemptEntity>

    /** Средний балл по фонеме */
    @Query("SELECT AVG(score) FROM attempts WHERE userId = :userId AND phoneme = :phoneme")
    suspend fun getAverageScore(userId: String, phoneme: String): Float?

    /** Количество сессий (дней с хотя бы одной попыткой) */
    @Query("""
        SELECT COUNT(DISTINCT date(timestamp / 1000, 'unixepoch')) 
        FROM attempts 
        WHERE userId = :userId
    """)
    suspend fun getTotalSessions(userId: String): Int

    /** Общее количество попыток */
    @Query("SELECT COUNT(*) FROM attempts WHERE userId = :userId")
    suspend fun getTotalAttempts(userId: String): Int

    @Query("DELETE FROM attempts WHERE userId = :userId")
    suspend fun clearUser(userId: String)
}
