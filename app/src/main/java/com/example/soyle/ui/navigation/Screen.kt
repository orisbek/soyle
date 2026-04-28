package com.example.soyle.ui.navigation

sealed class Screen(val route: String) {
    // ── Авторизация ────────────────────────────────────────────────────────
    data object Login    : Screen("login")
    data object Register : Screen("register")

    // ── Онбординг ──────────────────────────────────────────────────────────
    data object Onboarding     : Screen("onboarding")
    data object OnboardingGoal : Screen("onboarding/goal")
    data object OnboardingAge  : Screen("onboarding/age")
    data object OnboardingTime : Screen("onboarding/time")
    data object OnboardingDone : Screen("onboarding/done")

    // ── Главные вкладки ────────────────────────────────────────────────────
    data object Home    : Screen("home")
    data object Games   : Screen("games")
    data object Profile : Screen("profile")

    // ── Настройки ─────────────────────────────────────────────────────────
    data object Settings       : Screen("settings")
    data object ProfileEdit    : Screen("profile_edit")
    data object AboutMe        : Screen("about_me")
    data object Notifications  : Screen("notifications")

    // ── Вложенные экраны ──────────────────────────────────────────────────
    data object CheckIn : Screen("checkin")

    data object Exercise : Screen("exercise/{id}/{phoneme}") {
        fun createRoute(id: String, phoneme: String) = "exercise/$id/$phoneme"
    }

    data object GamePlay : Screen("gameplay/{type}") {
        fun createRoute(type: String) = "gameplay/$type"
    }

    data object Result : Screen("result/{score}/{phoneme}") {
        fun createRoute(score: Int, phoneme: String) = "result/$score/$phoneme"
    }
}
