package com.example.soyle.domain.model

enum class MascotEmotion {
    HAPPY,        // score ≥ 85  — прыгает, радуется
    GOOD,         // score 65–84 — улыбается
    NEUTRAL,      // score 45–64 — нейтральный
    SUPPORTIVE,   // score < 45  — подбадривает
    CELEBRATING,  // новый рекорд / достижение
    GREETING      // первый вход дня
}