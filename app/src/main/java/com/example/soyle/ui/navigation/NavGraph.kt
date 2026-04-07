package com.example.soyle.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.soyle.ui.screens.exercise.ExerciseScreen
import com.example.soyle.ui.screens.game.GameScreen
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

        // ── Onboarding ────────────────────────────────────────────────────
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinish = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Home ──────────────────────────────────────────────────────────
        composable(Screen.Home.route) {
            HomeScreen(
                onStartExercise = { phoneme, mode ->
                    if (mode == "GAME") {
                        navController.navigate(Screen.Game.route)
                    } else {
                        navController.navigate(
                            Screen.Exercise.createRoute(phoneme, mode)
                        )
                    }
                },
                onOpenProgress = {
                    navController.navigate(Screen.Progress.route)
                },
                onOpenProfile = {
                    navController.navigate(Screen.Profile.route)
                }
            )
        }

        // ── Game ──────────────────────────────────────────────────────────
        composable(Screen.Game.route) {
            GameScreen(
                onBack   = { navController.popBackStack() },
                onFinish = { score ->
                    navController.navigate(
                        Screen.Result.createRoute(score, "Р")
                    ) {
                        popUpTo(Screen.Game.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Pronunciation Accuracy ────────────────────────────────────────
        composable(
            route     = Screen.PronunciationAccuracy.route,
            arguments = listOf(
                navArgument("phoneme") { type = NavType.StringType }
            )
        ) { backStack ->
            val phoneme = backStack.arguments?.getString("phoneme") ?: "Р"
            PronunciationAccuracyScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // ── Exercise ──────────────────────────────────────────────────────
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
                    navController.navigate(
                        Screen.Result.createRoute(score, phoneme)
                    ) {
                        popUpTo(Screen.Exercise.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        // ── Result ────────────────────────────────────────────────────────
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
                    navController.navigate(
                        Screen.Exercise.createRoute(phoneme, "SOUND")
                    ) {
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

        // ── Progress ──────────────────────────────────────────────────────
        composable(Screen.Progress.route) {
            ProgressScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // ── Profile ───────────────────────────────────────────────────────
        composable(Screen.Profile.route) {
            ProfileScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
