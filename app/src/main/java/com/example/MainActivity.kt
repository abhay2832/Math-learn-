package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.MathLearnApp
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.MathLearnViewModel

class MainActivity : ComponentActivity() {
  
  // State-aware Viewmodel mapping
  private val viewModel: MathLearnViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      // Dynamic slate dark vs light mode theme provider
      MyApplicationTheme(darkTheme = viewModel.isDarkTheme) {
        MathLearnApp(viewModel = viewModel)
      }
    }
  }
}
