package com.example.soyle.mascot

import com.example.soyle.domain.model.MascotEmotion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Управляет состоянием маскота — эмоцией и фразой.
 * Инжектируется в ViewModel'ы, которым нужно обновлять маскот.
 */
@Singleton
class MascotController @Inject constructor() {

    data class MascotState(
        val emotion : MascotEmotion = MascotEmotion.GREETING,
        val phrase  : String        = "Привет! 👋",
        val phoneme : String        = ""
    )

    private val _state = MutableStateFlow(MascotState())
    val state = _state.asStateFlow()

    fun showGreeting(phoneme: String = "") {
        val emotion = MascotEmotionManager.greeting()
        _state.value = MascotState(
            emotion = emotion,
            phrase  = MascotEmotionManager.phraseFor(emotion, phoneme),
            phoneme = phoneme
        )
    }

    fun showScore(score: Int, isNewRecord: Boolean = false) {
        val emotion = MascotEmotionManager.fromScore(score, isNewRecord)
        _state.value = _state.value.copy(
            emotion = emotion,
            phrase  = MascotEmotionManager.phraseFor(emotion)
        )
    }

    fun showStreakCelebration(streakDay: Int) {
        val emotion = MascotEmotionManager.fromStreakDay(streakDay)
        _state.value = _state.value.copy(
            emotion = emotion,
            phrase  = MascotEmotionManager.phraseFor(emotion)
        )
    }

    fun setEmotion(emotion: MascotEmotion) {
        _state.value = _state.value.copy(
            emotion = emotion,
            phrase  = MascotEmotionManager.phraseFor(emotion, _state.value.phoneme)
        )
    }
}