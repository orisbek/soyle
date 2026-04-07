package com.example.soyle.audio

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

// ── State ─────────────────────────────────────────────────────────────────────

sealed class SpeechState {
    data object Idle      : SpeechState()
    data object Listening : SpeechState()
    data class  Success(val text: String) : SpeechState()
    data class  Error(val message: String) : SpeechState()
}

// ── Manager ───────────────────────────────────────────────────────────────────

/**
 * Обёртка над Android SpeechRecognizer.
 * Возвращает Flow<SpeechState> — удобно собирать в ViewModel.
 *
 * Используется в PronunciationViewModel для режима
 * «произнеси слово → получи текст → сравни с эталоном».
 */
@Singleton
class SpeechRecognitionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun startListening(locale: Locale = Locale("ru", "RU")): Flow<SpeechState> =
        callbackFlow {
            if (!SpeechRecognizer.isRecognitionAvailable(context)) {
                trySend(SpeechState.Error("Распознавание речи недоступно на этом устройстве"))
                close()
                return@callbackFlow
            }

            val recognizer = SpeechRecognizer.createSpeechRecognizer(context)

            val listener = object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    trySend(SpeechState.Listening)
                }
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {}

                override fun onResults(results: Bundle?) {
                    val matches = results
                        ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val text = matches?.firstOrNull() ?: ""
                    trySend(SpeechState.Success(text))
                    close()
                }

                override fun onError(error: Int) {
                    val msg = when (error) {
                        SpeechRecognizer.ERROR_NO_MATCH        -> "Не удалось распознать речь"
                        SpeechRecognizer.ERROR_SPEECH_TIMEOUT  -> "Время ожидания истекло"
                        SpeechRecognizer.ERROR_AUDIO           -> "Ошибка аудио"
                        SpeechRecognizer.ERROR_NETWORK         -> "Ошибка сети"
                        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Нет разрешения на микрофон"
                        else -> "Ошибка распознавания ($error)"
                    }
                    trySend(SpeechState.Error(msg))
                    close()
                }

                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            }

            recognizer.setRecognitionListener(listener)

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, locale.toString())
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
            }
            recognizer.startListening(intent)

            awaitClose {
                recognizer.destroy()
            }
        }
}
