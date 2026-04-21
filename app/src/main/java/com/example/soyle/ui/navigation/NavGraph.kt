package com.example.soyle.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.soyle.ui.screens.checkin.CheckInScreen
import com.example.soyle.ui.screens.exercise.ExerciseScreen
import com.example.soyle.ui.screens.game.GamesScreen
import com.example.soyle.ui.screens.home.HomeScreen
import com.example.soyle.ui.screens.onboarding.OnboardingScreen
import com.example.soyle.ui.screens.profile.ProfileScreen
import com.example.soyle.ui.screens.result.ResultScreen
import com.example.soyle.ui.theme.SoyleBg

// ── Маршруты с нижним баром ───────────────────────────────────────────────────

private val bottomNavRoutes = setOf(
    Screen.Home.route,
    Screen.Games.route,
    Screen.Profile.route
)

// ── Главный NavGraph ──────────────────────────────────────────────────────────

@Composable
fun SoyleNavGraph(
    navController  : NavHostController = rememberNavController(),
    startDestination: String           = Screen.Onboarding.route
) {
    val navBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStack?.destination?.route

    val showBottomBar = currentRoute in bottomNavRoutes

    Scaffold(
        containerColor = SoyleBg,
        bottomBar = {
            if (showBottomBar) {
                SoyleBottomNav(
                    currentRoute = currentRoute ?: Screen.Home.route,
                    onNavigate   = { route ->
                        navController.navigate(route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState    = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController    = navController,
            startDestination = startDestination,
            modifier         = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // ── Онбординг ─────────────────────────────────────────────────
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onFinish = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }

            // ── Главная ───────────────────────────────────────────────────
            composable(Screen.Home.route) {
                HomeScreen(
                    onOpenCheckIn  = { navController.navigate(Screen.CheckIn.route) },
                    onOpenExercise = { id ->
                        navController.navigate(Screen.Exercise.createRoute(id, "Р"))
                    },
                    onOpenProfile  = {
                        navController.navigate(Screen.Profile.route)
                    }
                )
            }

            // ── Игры ──────────────────────────────────────────────────────
            composable(Screen.Games.route) {
                GamesScreen(
                    onOpenGame = { id ->
                        navController.navigate(Screen.GamePlay.createRoute(id))
                    }
                )
            }

            // ── Профиль ───────────────────────────────────────────────────
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onClose = { navController.popBackStack() }
                )
            }

            // ── Чек-ин ────────────────────────────────────────────────────
            composable(Screen.CheckIn.route) {
                CheckInScreen(
                    onFinish = {
                        navController.popBackStack()
                    },
                    onSkip = {
                        navController.popBackStack()
                    }
                )
            }

            // ── Упражнение ────────────────────────────────────────────────
            composable(
                route     = Screen.Exercise.route,
                arguments = listOf(
                    navArgument("id")      { type = NavType.StringType },
                    navArgument("phoneme") { type = NavType.StringType }
                )
            ) { back ->
                val phoneme = back.arguments?.getString("phoneme") ?: "Р"
                ExerciseScreen(
                    phoneme  = phoneme,
                    title    = "Звук «$phoneme»",
                    onBack   = { navController.popBackStack() },
                    onResult = { score ->
                        navController.navigate(
                            Screen.Result.createRoute(score, phoneme)
                        ) { popUpTo(Screen.Exercise.route) { inclusive = true } }
                    }
                )
            }

            // ── Игра ──────────────────────────────────────────────────────
            composable(
                route     = Screen.GamePlay.route,
                arguments = listOf(navArgument("type") { type = NavType.StringType })
            ) { back ->
                // val type = back.arguments?.getString("type") ?: "catch_r"
                // GamePlayScreen(type = type, onBack = { navController.popBackStack() })
                // Пока заглушка — экран упражнения
                ExerciseScreen(
                    phoneme = "Р",
                    title   = "Игра",
                    onBack  = { navController.popBackStack() }
                )
            }

            // ── Результат ─────────────────────────────────────────────────
            composable(
                route     = Screen.Result.route,
                arguments = listOf(
                    navArgument("score")   { type = NavType.IntType },
                    navArgument("phoneme") { type = NavType.StringType }
                )
            ) { back ->
                val score   = back.arguments?.getInt("score")      ?: 0
                val phoneme = back.arguments?.getString("phoneme") ?: "Р"
                ResultScreen(
                    score   = score,
                    phoneme = phoneme,
                    onHome  = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route)
                        }
                    },
                    onRetry = { navController.popBackStack() }
                )
            }
        }
    }
}