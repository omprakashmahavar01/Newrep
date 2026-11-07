package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.ui.screens.SimpleAppScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.util.SpeechManager

class MainActivity : ComponentActivity() {

  private lateinit var speechManager: SpeechManager

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    speechManager = SpeechManager(this)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        SimpleAppScreen(speechManager = speechManager)
      }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    speechManager.shutdown()
  }
}
