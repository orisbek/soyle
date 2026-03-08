package com.example.soyle.domain.model

data class PronunciationResult(
    val score          : Int,           // 0–100
    val phoneme        : String,        // "Р"
    val feedback       : String,        // "Вибрируй языком сильнее!"
    val durationMs     : Int,           // длительность фонемы в мс
    val waveformData   : List<Float>,   // волна ребёнка → для визуализации
    val referenceWave  : List<Float>,   // эталонная волна → для сравнения
    val xpEarned       : Int,           // начисленные очки опыта
    val mascotEmotion  : MascotEmotion  // какую эмоцию показать маскоту
)