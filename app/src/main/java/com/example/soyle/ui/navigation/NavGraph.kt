package com.example.soyle.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.soyle.ui.screens.exercise.ExerciseScreen
import com.example.soyle.ui.screens.game.ALL_WORD_LEVELS
import com.example.soyle.ui.screens.game.GameScreen
import com.example.soyle.ui.screens.game.LevelSelectScreen
import com.example.soyle.ui.screens.game.WordBuildingScreen
import com.example.soyle.ui.screens.home.HomeScreen
import com.example.soyle.ui.screens.onboarding.OnboardingScreen
import com.example.soyle.ui.screens.profile.ProfileScreen
import com.example.soyle.ui.screens.progress.ProgressScreen
import com.example.soyle.ui.screens.pronunciation.PronunciationAccuracyScreen
import com.example.soyle.ui.screens.result.ResultScreen

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController    = navController,
        startDestination = Screen.Home.route
    ) {

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinish = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onStartExercise = { phoneme, mode ->
                    when (mode) {
                        "GAME"          -> navController.navigate(Screen.Game.route)
                        "WORD_BUILDING" -> navController.navigate(Screen.LevelSelect.route)
                        else            -> navController.navigate(Screen.Exercise.createRoute(phoneme, mode))
                    }
                },
                onOpenProgress  = { navController.navigate(Screen.Progress.route) },
                onOpenProfile   = { navController.navigate(Screen.Profile.route) }
            )
        }

        composable(Screen.Game.route) {
            GameScreen(
                onBack   = { navController.popBackStack() },
                onFinish = { score ->
                    navController.navigate(Screen.Result.createRoute(score, "Р")) {
                        popUpTo(Screen.Game.route) { inclusive = true }
                    }
                }
            )
        }

        // Выбор уровня — 6 случайных из 50
        composable(Screen.LevelSelect.route) {
            LevelSelectScreen(
                onLevelSelected = { level ->
                    navController.navigate(Screen.WordBuilding.createRoute(level.number - 1))
                },
                onBack = { navController.popBackStack() }
            )
        }

        // Игра «Собери слово»
        composable(
            route     = Screen.WordBuilding.route,
            arguments = listOf(navArgument("levelIndex") { type = NavType.IntType })
        ) { backStack ->
            val idx   = backStack.arguments?.getInt("levelIndex") ?: 0
            val level = ALL_WORD_LEVELS.getOrElse(idx) { ALL_WORD_LEVELS.first() }
            WordBuildingScreen(
                level    = level,
                onBack   = { navController.popBackStack() },
                onFinish = { score ->
                    navController.navigate(Screen.Result.createRoute(score, "Р")) {
                        popUpTo(Screen.WordBuilding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route     = Screen.PronunciationAccuracy.route,
            arguments = listOf(navArgument("phoneme") { type = NavType.StringType })
        ) {
            PronunciationAccuracyScreen(onBack = { navController.popBackStack() })
        }

        composable(
            route = Screen.Exercise.route,
            arguments = listOf(
                navArgument("phoneme") { type = NavType.StringType },
                navArgument("mode")    { type = NavType.StringType }
            )
        ) { backStack ->
            val phoneme = backStack.arguments?.getString("phoneme") ?: "Р"
            val mode    = backStack.arguments?.getString("mode")    ?: "SOUND"
            ExerciseScreen(
                phoneme  = phoneme,
                mode     = mode,
                onResult = { score ->
                    navController.navigate(Screen.Result.createRoute(score, phoneme)) {
                        popUpTo(Screen.Exercise.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Result.route,
            arguments = listOf(
                navArgument("score")   { type = NavType.IntType    },
                navArgument("phoneme") { type = NavType.StringType }
            )
        ) { backStack ->
            val score   = backStack.arguments?.getInt("score")      ?: 0
            val phoneme = backStack.arguments?.getString("phoneme") ?: "Р"
            ResultScreen(
                score   = score,
                phoneme = phoneme,
                onRetry = {
                    navController.navigate(Screen.Exercise.createRoute(phoneme, "SOUND")) {
                        popUpTo(Screen.Result.route) { inclusive = true }
                    }
                },
                onHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                    }
                }
            )
        }

        composable(Screen.Progress.route) {
            ProgressScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Profile.route) {
            ProfileScreen(onBack = { navController.popBackStack() })
        }
    }
}
