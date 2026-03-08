package com.example.soyle.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Таблица всех попыток произношения.
 * Хранится локально — работает офлайн, используется для графиков прогресса.
 */
@Entity(tableName = "attempts")
data class AttemptEntity(
    @PrimaryKey(autoGenerate = true)
    val id        : Long   = 0,
    val userId    : String,
    val phoneme   : String,
    val mode      : String,     // ExerciseMode.name
    val score     : Int,
    val timestamp : Long = System.currentTimeMillis()
)