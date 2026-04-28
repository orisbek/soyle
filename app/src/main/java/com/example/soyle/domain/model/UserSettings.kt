package com.example.soyle.domain.model

data class UserSettings(
    val displayName : String = "",
    val avatarEmoji : String = "🧑",
    val theme       : String = "dark",   // "dark" | "light"
    val language    : String = "ru",     // "ru" | "kk" | "en"
    val goal        : String = "",
    val ageGroup    : String = "",
    val notes       : String = ""
)
