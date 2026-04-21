package com.example.soyle.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "levels")
data class LevelEntity(
    @PrimaryKey val levelNumber: Int,
    val isUnlocked: Boolean = false,
    val isCompleted: Boolean = false
)
