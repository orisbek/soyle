package com.example.soyle.domain.model

data class UserProgress(
    val userId        : String,
    val phonemeScores : Map<String, Float>, // "Р" → 72.5f, "Л" → 58.0f
    val totalSessions : Int,
    val currentStreak : Int,                // текущая серия дней
    val longestStreak : Int,                // рекорд серии
    val totalXp       : Int,
    val level         : Int,                // уровень (каждые 500 XP)
    val achievements  : List<Achievement>
)

data class Achievement(
    val id          : String,
    val title       : String,           // "Мастер звука Р"
    val description : String,
    val isUnlocked  : Boolean,
    val unlockedAt  : Long? = null      // timestamp
)