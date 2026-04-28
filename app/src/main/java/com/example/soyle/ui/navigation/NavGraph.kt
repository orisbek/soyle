package com.example.soyle.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.soyle.ui.screens.auth.AuthViewModel
import com.example.soyle.ui.screens.auth.LoginScreen
import com.example.soyle.ui.screens.auth.RegisterScreen
import com.example.soyle.ui.screens.checkin.CheckInScreen
import com.example.soyle.ui.screens.exercise.ExerciseScreen
import com.example.soyle.ui.screens.game.GamesScreen
import com.example.soyle.ui.screens.home.HomeScreen
import com.example.soyle.ui.screens.onboarding.OnboardingScreen
import com.example.soyle.ui.screens.profile.ProfileScreen
import com.example.soyle.ui.screens.result.ResultScreen
import com.example.soyle.ui.screens.settings.AboutMeScreen
import com.example.soyle.ui.screens.settings.NotificationsScreen
import com.example.soyle.ui.screens.settings.ProfileEditScreen
import com.example.soyle.ui.screens.settings.SettingsScreen
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
    navController: NavHostController = rememberNavController()
) {
    val authViewModel: AuthViewModel = hiltViewModel()

    // Стартовая точка определяется по состоянию авторизации
    val startDestination = remember {
        if (authViewModel.isLoggedIn) Screen.Home.route else Screen.Login.route
    }

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

            // ── Вход ──────────────────────────────────────────────────────
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onRegister = {
                        navController.navigate(Screen.Register.route)
                    }
                )
            }

            // ── Регистрация ───────────────────────────────────────────────
            composable(Screen.Register.route) {
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }

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
                    onOpenProfile  = { navController.navigate(Screen.Profile.route) }
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
                    onClose             = { navController.popBackStack() },
                    onEditProfile       = { navController.navigate(Screen.ProfileEdit.route) },
                    onOpenSettings      = { navController.navigate(Screen.Settings.route) },
                    onOpenAboutMe       = { navController.navigate(Screen.AboutMe.route) },
                    onOpenNotifications = { navController.navigate(Screen.Notifications.route) },
                    onSignOut           = {
                        authViewModel.signOut()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            // ── Редактировать профиль ─────────────────────────────────────
            composable(Screen.ProfileEdit.route) {
                ProfileEditScreen(onBack = { navController.popBackStack() })
            }

            // ── Настройки ─────────────────────────────────────────────────
            composable(Screen.Settings.route) {
                SettingsScreen(onBack = { navController.popBackStack() })
            }

            // ── О себе ────────────────────────────────────────────────────
            composable(Screen.AboutMe.route) {
                AboutMeScreen(onBack = { navController.popBackStack() })
            }

            // ── Уведомления ───────────────────────────────────────────────
            composable(Screen.Notifications.route) {
                NotificationsScreen(onBack = { navController.popBackStack() })
            }

            // ── Чек-ин ────────────────────────────────────────────────────
            composable(Screen.CheckIn.route) {
                CheckInScreen(
                    onFinish = { navController.popBackStack() },
                    onSkip   = { navController.popBackStack() }
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
            ) {
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
