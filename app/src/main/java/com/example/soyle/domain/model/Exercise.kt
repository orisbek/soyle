package com.example.soyle.domain.model

data class Exercise(
    val id: String,
    val phoneme: String,
    val mode: ExerciseMode,
    val content: String,
    val difficulty: Int
)

enum class ExerciseMode {
    SOUND,
    SYLLABLE,
    WORD,
    LISTEN_CHOOSE,
    VISUALIZE,
    GAME,
    GAME_RHYTHM,
    GAME_ECHO,
    GAME_PUZZLE
}
