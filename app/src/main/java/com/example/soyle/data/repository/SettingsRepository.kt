package com.example.soyle.data.repository

import com.example.soyle.domain.model.UserSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val firestore : FirebaseFirestore,
    private val auth      : FirebaseAuth
) {
    private val uid get() = auth.currentUser?.uid

    /** Поток настроек текущего пользователя из Firestore */
    fun settingsFlow(): Flow<UserSettings> = callbackFlow {
        val id = uid ?: run { trySend(UserSettings()); close(); return@callbackFlow }

        val listener = firestore.collection("users").document(id)
            .addSnapshotListener { snap, err ->
                if (err != null) { close(err); return@addSnapshotListener }
                val d = snap?.data ?: run { trySend(UserSettings()); return@addSnapshotListener }
                trySend(
                    UserSettings(
                        displayName  = d["displayName"] as? String ?: d["name"] as? String ?: "",
                        avatarEmoji  = d["avatarEmoji"] as? String ?: "🧑",
                        theme        = d["theme"]    as? String ?: "dark",
                        language     = d["language"] as? String ?: "ru",
                        goal         = d["goal"]     as? String ?: "",
                        ageGroup     = d["ageGroup"] as? String ?: "",
                        notes        = d["notes"]    as? String ?: ""
                    )
                )
            }
        awaitClose { listener.remove() }
    }

    /** Обновить имя и аватар */
    suspend fun updateProfile(displayName: String, avatarEmoji: String): Result<Unit> = runCatching {
        val id = uid ?: throw Exception("Не авторизован")
        firestore.collection("users").document(id)
            .update(mapOf("displayName" to displayName, "avatarEmoji" to avatarEmoji))
            .await()
    }

    /** Обновить тему */
    suspend fun updateTheme(theme: String): Result<Unit> = runCatching {
        val id = uid ?: throw Exception("Не авторизован")
        firestore.collection("users").document(id).update("theme", theme).await()
    }

    /** Обновить язык */
    suspend fun updateLanguage(language: String): Result<Unit> = runCatching {
        val id = uid ?: throw Exception("Не авторизован")
        firestore.collection("users").document(id).update("language", language).await()
    }

    /** Обновить блок «О себе» */
    suspend fun updateAboutMe(goal: String, ageGroup: String, notes: String): Result<Unit> = runCatching {
        val id = uid ?: throw Exception("Не авторизован")
        firestore.collection("users").document(id)
            .update(mapOf("goal" to goal, "ageGroup" to ageGroup, "notes" to notes))
            .await()
    }

    /** Получить время уведомлений (одноразово) */
    suspend fun getNotificationTimes(): Result<NotificationTimes> = runCatching {
        val id   = uid ?: throw Exception("Не авторизован")
        val snap = firestore.collection("users").document(id).get().await()
        NotificationTimes(
            morningEnabled  = snap.getBoolean("morningEnabled")  ?: true,
            morningHour     = (snap.getLong("morningHour")?.toInt())   ?: 8,
            morningMinute   = (snap.getLong("morningMinute")?.toInt()) ?: 0,
            eveningEnabled  = snap.getBoolean("eveningEnabled")  ?: true,
            eveningHour     = (snap.getLong("eveningHour")?.toInt())   ?: 20,
            eveningMinute   = (snap.getLong("eveningMinute")?.toInt()) ?: 0
        )
    }

    /** Сохранить время уведомлений */
    suspend fun updateNotificationTimes(times: NotificationTimes): Result<Unit> = runCatching {
        val id = uid ?: throw Exception("Не авторизован")
        firestore.collection("users").document(id).update(
            mapOf(
                "morningEnabled" to times.morningEnabled,
                "morningHour"    to times.morningHour,
                "morningMinute"  to times.morningMinute,
                "eveningEnabled" to times.eveningEnabled,
                "eveningHour"    to times.eveningHour,
                "eveningMinute"  to times.eveningMinute
            )
        ).await()
    }
}

data class NotificationTimes(
    val morningEnabled : Boolean = true,
    val morningHour    : Int     = 8,
    val morningMinute  : Int     = 0,
    val eveningEnabled : Boolean = true,
    val eveningHour    : Int     = 20,
    val eveningMinute  : Int     = 0
)
