package com.example.soyle.audio

import android.util.Base64
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioProcessor @Inject constructor() {

    /**
     * PCM байты → Base64 строка для отправки на сервер
     */
    fun toBase64(audioBytes: ByteArray): String =
        Base64.encodeToString(audioBytes, Base64.NO_WRAP)

    /**
     * Нормализация амплитуды — громкость выравнивается к целевому уровню.
     * Помогает если ребёнок говорит тихо.
     */
    fun normalize(audioBytes: ByteArray, targetAmplitude: Float = 0.8f): ByteArray {
        if (audioBytes.isEmpty()) return audioBytes

        // Конвертируем байты в Short (PCM 16bit = 2 байта на сэмпл)
        val samples = ShortArray(audioBytes.size / 2) { i ->
            ((audioBytes[i * 2 + 1].toInt() shl 8) or
                    (audioBytes[i * 2].toInt() and 0xFF)).toShort()
        }

        // Находим максимальную амплитуду
        val maxAmplitude = samples.maxOfOrNull { kotlin.math.abs(it.toFloat()) } ?: 1f
        if (maxAmplitude == 0f) return audioBytes

        // Масштабируем все сэмплы
        val scale = (targetAmplitude * Short.MAX_VALUE) / maxAmplitude
        val normalized = ShortArray(samples.size) { i ->
            (samples[i] * scale).toInt()
                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                .toShort()
        }

        // Обратно в ByteArray
        return ByteArray(normalized.size * 2) { i ->
            if (i % 2 == 0) (normalized[i / 2].toInt() and 0xFF).toByte()
            else             (normalized[i / 2].toInt() shr 8).toByte()
        }
    }

    /**
     * Проверка — не тишина ли записана.
     * Если ребёнок не говорил — не отправляем на сервер.
     */
    fun isSilent(audioBytes: ByteArray, threshold: Float = 0.01f): Boolean {
        if (audioBytes.size < 2) return true

        val samples = ShortArray(audioBytes.size / 2) { i ->
            ((audioBytes[i * 2 + 1].toInt() shl 8) or
                    (audioBytes[i * 2].toInt() and 0xFF)).toShort()
        }

        val rms = kotlin.math.sqrt(
            samples.map { it.toFloat() * it.toFloat() }.average().toFloat()
        )

        return rms / Short.MAX_VALUE < threshold
    }
}