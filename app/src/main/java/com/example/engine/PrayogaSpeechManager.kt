package com.example.engine

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.util.Locale

class PrayogaSpeechManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    var onSpeechStarted: (() -> Unit)? = null
    var onSpeechDone: (() -> Unit)? = null

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.w("PrayogaSpeechManager", "Locale US not supported for TTS")
            } else {
                isInitialized = true
                tts?.setSpeechRate(1.0f)
                tts?.setPitch(1.0f)
                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        onSpeechStarted?.invoke()
                    }

                    override fun onDone(utteranceId: String?) {
                        onSpeechDone?.invoke()
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        onSpeechDone?.invoke()
                    }
                })
            }
        }
    }

    fun speak(text: String, pitch: Float = 1.0f, rate: Float = 1.0f) {
        if (!isInitialized || tts == null) {
            Log.w("PrayogaSpeechManager", "TTS not initialized, attempting re-init")
            tts = TextToSpeech(context.applicationContext, this)
            return
        }

        try {
            tts?.setPitch(pitch)
            tts?.setSpeechRate(rate)
            val utteranceId = "prayoga_${System.currentTimeMillis()}"
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
        } catch (e: Exception) {
            Log.e("PrayogaSpeechManager", "Error speaking text: ${e.message}")
        }
    }

    fun stop() {
        try {
            tts?.stop()
        } catch (e: Exception) {
            Log.e("PrayogaSpeechManager", "Error stopping TTS: ${e.message}")
        }
    }

    fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (e: Exception) {
            Log.e("PrayogaSpeechManager", "Error shutting down TTS: ${e.message}")
        }
    }
}
