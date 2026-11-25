package com.unmute.app.speech

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer

class SpeechCaptioner(
    context: Context,
    private val onPartial: (String) -> Unit,
    private val onSentence: (String) -> Unit,
    private val onStatus: (String) -> Unit = {},
) {
    private val recognizer = SpeechRecognizer.createSpeechRecognizer(context)
    private val handler = Handler(Looper.getMainLooper())
    private var active = false

    private val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "id-ID")
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
    }

    private val listener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) = onStatus("Mendengarkan...")

        override fun onPartialResults(partialResults: Bundle) {
            partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                ?.firstOrNull()
                ?.takeIf { it.isNotBlank() }
                ?.let(onPartial)
        }

        override fun onResults(results: Bundle) {
            results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                ?.firstOrNull()
                ?.takeIf { it.isNotBlank() }
                ?.let(onSentence)
            restart()
        }

        override fun onError(error: Int) {
            when (error) {
                SpeechRecognizer.ERROR_NO_MATCH,
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> restart()

                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> {
                    active = false
                    onStatus("Izin mikrofon belum diberikan")
                }

                SpeechRecognizer.ERROR_NETWORK,
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> {
                    onStatus("Internet bermasalah, mencoba lagi...")
                    restart(delayMs = 1000)
                }

                else -> {
                    onStatus("Error $error, mencoba lagi...")
                    recognizer.cancel()
                    restart(delayMs = 500)
                }
            }
        }

        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() {}
        override fun onEvent(eventType: Int, params: Bundle?) {}
    }

    init {
        recognizer.setRecognitionListener(listener)
    }

    fun start() {
        active = true
        recognizer.startListening(intent)
    }

    fun stop() {
        active = false
        handler.removeCallbacksAndMessages(null)
        recognizer.stopListening()
        onStatus("Berhenti")
    }

    fun release() {
        active = false
        handler.removeCallbacksAndMessages(null)
        recognizer.destroy()
    }

    private fun restart(delayMs: Long = 150) {
        if (!active) return
        handler.postDelayed({ if (active) recognizer.startListening(intent) }, delayMs)
    }
}
