package com.example.soyle.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    val currentUser: FirebaseUser? get() = auth.currentUser
    val currentUserId: String? get() = auth.currentUser?.uid

    suspend fun register(email: String, password: String, name: String): Result<Unit> = runCatching {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val uid = result.user?.uid ?: throw Exception("Ошибка создания аккаунта")
        firestore.collection("users").document(uid).set(
            hashMapOf(
                "name" to name,
                "email" to email,
                "totalXp" to 0,
                "level" to 1,
                "currentStreak" to 0,
                "longestStreak" to 0,
                "totalSessions" to 0,
                "lastSessionDate" to "",
                "phonemeScores" to emptyMap<String, Any>(),
                "createdAt" to System.currentTimeMillis()
            )
        ).await()
    }

    suspend fun login(email: String, password: String): Result<Unit> = runCatching {
        auth.signInWithEmailAndPassword(email, password).await()
        Unit
    }

    fun signOut() = auth.signOut()
}
