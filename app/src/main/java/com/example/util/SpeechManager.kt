package com.example.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class SpeechManager(context: Context) : TextToSpeech.OnInitListener {

  private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
  private var isInitialized = false

  override fun onInit(status: Int) {
    if (status == TextToSpeech.SUCCESS) {
      val hindiLocale = Locale("hi", "IN")
      val result = tts?.setLanguage(hindiLocale)
      if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
        // Fallback to default locale
        tts?.setLanguage(Locale.getDefault())
      }
      tts?.setPitch(1.0f)
      tts?.setSpeechRate(0.95f) // Natural speaking speed
      isInitialized = true
    } else {
      Log.e("SpeechManager", "TextToSpeech initialization failed")
    }
  }

  fun speak(text: String) {
    if (!isInitialized || text.isBlank()) return
    tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "RozanaVoiceFeedback")
  }

  fun stop() {
    tts?.stop()
  }

  fun shutdown() {
    try {
      tts?.stop()
      tts?.shutdown()
    } catch (e: Exception) {
      Log.e("SpeechManager", "Error shutting down TTS", e)
    }
    tts = null
    isInitialized = false
  }
}
