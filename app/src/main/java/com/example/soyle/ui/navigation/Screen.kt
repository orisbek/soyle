package com.example.soyle.ui.navigation

sealed class Screen(val route: String) {

    data object Home                : Screen("home")
    data object Onboarding          : Screen("onboarding")
    data object Progress            : Screen("progress")
    data object Profile             : Screen("profile")
    data object Game                : Screen("game")
    data object LevelSelect         : Screen("level_select")
    data object PronunciationAccuracy : Screen("pronunciation_accuracy/{phoneme}") {
        fun createRoute(phoneme: String) = "pronunciation_accuracy/$phoneme"
    }

    data object Exercise : Screen("exercise/{phoneme}/{mode}") {
        fun createRoute(phoneme: String, mode: String) =
            "exercise/$phoneme/$mode"
    }

    data object Result : Screen("result/{score}/{phoneme}") {
        fun createRoute(score: Int, phoneme: String) =
            "result/$score/$phoneme"
    }

    data object WordBuilding : Screen("word_building/{levelIndex}") {
        fun createRoute(levelIndex: Int) = "word_building/$levelIndex"
    }
}
