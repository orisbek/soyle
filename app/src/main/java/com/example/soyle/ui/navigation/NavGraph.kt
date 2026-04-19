package com.example.soyle.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.soyle.ui.screens.auth.AuthScreen
import com.example.soyle.ui.screens.auth.AuthViewModel
import com.example.soyle.ui.screens.exercise.ExerciseScreen
import com.example.soyle.ui.screens.home.HomeScreen
import com.example.soyle.ui.screens.profile.ProfileScreen
import com.example.soyle.ui.screens.progress.ProgressScreen
import com.example.soyle.ui.screens.result.ResultScreen

@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsStateWithLifecycle()

    if (!isLoggedIn) {
        AuthScreen(authViewModel)
        return
    }

    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(
                onStartExercise = { phoneme, mode -> navController.navigate(Screen.Exercise.createRoute(phoneme, mode)) },
                onOpenProgress = { navController.navigate(Screen.Progress.route) },
                onOpenProfile = { navController.navigate(Screen.Profile.route) }
            )
        }

        composable(
            route = Screen.Exercise.route,
            arguments = listOf(navArgument("phoneme") { type = NavType.StringType }, navArgument("mode") { type = NavType.StringType })
        ) { backStack ->
            val phoneme = backStack.arguments?.getString("phoneme") ?: "Р"
            val mode = backStack.arguments?.getString("mode") ?: "SOUND"
            ExerciseScreen(
                phoneme = phoneme,
                mode = mode,
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
            arguments = listOf(navArgument("score") { type = NavType.IntType }, navArgument("phoneme") { type = NavType.StringType })
        ) { backStack ->
            val score = backStack.arguments?.getInt("score") ?: 0
            val phoneme = backStack.arguments?.getString("phoneme") ?: "Р"
            ResultScreen(
                score = score,
                phoneme = phoneme,
                onRetry = {
                    navController.navigate(Screen.Exercise.createRoute(phoneme, "SOUND")) {
                        popUpTo(Screen.Result.route) { inclusive = true }
                    }
                },
                onHome = { navController.navigate(Screen.Home.route) }
            )
        }

        composable(Screen.Progress.route) { ProgressScreen(onBack = { navController.popBackStack() }) }
        composable(Screen.Profile.route) { ProfileScreen(onBack = { navController.popBackStack() }) }
    }
}
