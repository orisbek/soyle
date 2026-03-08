package com.example.soyle.audio

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioRecorder @Inject constructor() {

    companion object {
        const val SAMPLE_RATE     = 16_000   // 16 kHz — нужно для Whisper/wav2vec2
        const val CHANNEL_CONFIG  = AudioFormat.CHANNEL_IN_MONO
        const val AUDIO_FORMAT    = AudioFormat.ENCODING_PCM_16BIT
        const val MAX_DURATION_MS = 5_000L   // максимум 5 секунд
    }

    private var audioRecord : AudioRecord? = null
    private var isRecording = false

    @SuppressLint("MissingPermission")  // разрешение проверяется в UI
    suspend fun record(): ByteArray = withContext(Dispatchers.IO) {
        val bufferSize = AudioRecord.getMinBufferSize(
            SAMPLE_RATE,
            CHANNEL_CONFIG,
            AUDIO_FORMAT
        ).coerceAtLeast(8192)

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE,
            CHANNEL_CONFIG,
            AUDIO_FORMAT,
            bufferSize
        )

        val recordedData = mutableListOf<Byte>()
        val buffer       = ByteArray(bufferSize)
        val startTime    = System.currentTimeMillis()

        audioRecord?.startRecording()
        isRecording = true

        while (isRecording &&
            (System.currentTimeMillis() - startTime) < MAX_DURATION_MS) {
            val bytesRead = audioRecord?.read(buffer, 0, bufferSize) ?: 0
            if (bytesRead > 0) {
                recordedData.addAll(buffer.take(bytesRead))
            }
        }

        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null

        recordedData.toByteArray()
    }

    fun stop() {
        isRecording = false
    }
}