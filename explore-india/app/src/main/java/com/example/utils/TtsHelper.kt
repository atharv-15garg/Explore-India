package com.example.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class TtsHelper(context: Context) : TextToSpeech.OnInitListener {
  private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
  private var isInitialized = false

  override fun onInit(status: Int) {
    if (status == TextToSpeech.SUCCESS) {
      val result = tts?.setLanguage(Locale("hi", "IN"))
      if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
        // Fallback to English
        tts?.setLanguage(Locale.US)
      }
      isInitialized = true
    } else {
      Log.e("TtsHelper", "TTS Initialization failed")
    }
  }

  fun speak(text: String, isHindi: Boolean = true) {
    if (!isInitialized || text.isBlank()) return

    if (isHindi) {
      val hiLocale = Locale("hi", "IN")
      val canSpeakHindi = tts?.isLanguageAvailable(hiLocale)
      if (canSpeakHindi == TextToSpeech.LANG_AVAILABLE || canSpeakHindi == TextToSpeech.LANG_COUNTRY_AVAILABLE) {
        tts?.language = hiLocale
      } else {
        tts?.language = Locale.US
      }
    } else {
      tts?.language = Locale.US
    }

    tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "explore_india_tts_${System.currentTimeMillis()}")
  }

  fun shutdown() {
    tts?.stop()
    tts?.shutdown()
    tts = null
  }
}
