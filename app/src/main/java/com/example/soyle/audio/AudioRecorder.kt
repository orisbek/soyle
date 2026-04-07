package com.example.soyle.audio

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Записывает аудио с микрофона в формате PCM (16kHz, 16bit, mono).
 * Именно такой формат ожидает серверный AI-модуль.
 */
@Singleton
class AudioRecorder @Inject constructor() {

    companion object {
        const val SAMPLE_RATE    = 16_000   // 16 kHz
        const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        const val AUDIO_FORMAT   = AudioFormat.ENCODING_PCM_16BIT
        const val RECORD_DURATION_MS = 3_000L  // 3 секунды записи
    }

    @Volatile private var audioRecord: AudioRecord? = null
    @Volatile private var isRecording = false

    /**
     * Запускает запись на RECORD_DURATION_MS мс и возвращает сырые байты PCM.
     */
    @SuppressLint("MissingPermission")
    suspend fun record(): ByteArray = withContext(Dispatchers.IO) {
        val bufferSize = AudioRecord.getMinBufferSize(
            SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT
        ).coerceAtLeast(4096)

        val recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE,
            CHANNEL_CONFIG,
            AUDIO_FORMAT,
            bufferSize
        )

        audioRecord = recorder
        val chunks = mutableListOf<ByteArray>()

        try {
            recorder.startRecording()
            isRecording = true

            val startTime = System.currentTimeMillis()
            val buffer = ByteArray(bufferSize)

            while (isRecording &&
                System.currentTimeMillis() - startTime < RECORD_DURATION_MS) {
                val read = recorder.read(buffer, 0, buffer.size)
                if (read > 0) chunks.add(buffer.copyOf(read))
            }
        } finally {
            recorder.stop()
            recorder.release()
            audioRecord = null
            isRecording = false
        }

        // Склеиваем все чанки в один массив
        val totalSize = chunks.sumOf { it.size }
        val result = ByteArray(totalSize)
        var offset = 0
        chunks.forEach { chunk ->
            chunk.copyInto(result, offset)
            offset += chunk.size
        }
        result
    }

    /** Принудительная остановка записи (например, кнопка «Стоп»). */
    fun stop() {
        isRecording = false
    }
}
