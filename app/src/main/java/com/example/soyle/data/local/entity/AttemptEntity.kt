package com.example.soyle.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Таблица попыток произношения в локальной БД (Room).
 * Каждая запись = одна попытка ребёнка произнести звук/слово.
 */
@Entity(tableName = "attempts")
data class AttemptEntity(
    @PrimaryKey(autoGenerate = true)
    val id        : Long   = 0,
    val userId    : String,
    val phoneme   : String,          // "Р", "Л", "Ш"
    val mode      : String,          // ExerciseMode.name
    val score     : Int,             // 0–100
    val timestamp : Long = System.currentTimeMillis()
)
