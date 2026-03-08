package com.example.soyle.domain.model

data class Exercise(
    val id         : String,
    val phoneme    : String,        // "Р", "Л", "Ш"
    val mode       : ExerciseMode,
    val content    : String,        // буква / слог / слово — то что показываем ребёнку
    val difficulty : Int            // 1 = лёгкий, 2 = средний, 3 = сложный
)

enum class ExerciseMode {
    SOUND,          // одиночный звук "Р"
    SYLLABLE,       // слог "РА", "ОР"
    WORD,           // слово "РЫБА", "ТРАВА"
    LISTEN_CHOOSE,  // слушай и выбирай
    VISUALIZE,      // визуализация волны
    GAME            // игровой режим
}