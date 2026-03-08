package com.example.soyle.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Кеш упражнений — чтобы приложение работало офлайн.
 * Обновляется раз в 24 часа.
 */
@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey
    val id         : String,
    val phoneme    : String,
    val mode       : String,
    val content    : String,
    val difficulty : Int,
    val cachedAt   : Long = System.currentTimeMillis()
)