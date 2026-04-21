package com.example.soyle.domain.model

data class UserProfile(
    val uid: String = "",
    val name: String = "Ученик",
    val avatarId: Int = 0,
    val totalXp: Int = 0,
    val level: Int = 1,
    val unlockedLevelsCount: Int = 1
)
