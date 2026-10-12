package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppScreen
import com.example.ui.DashboardScreen
import com.example.ui.ScannerScreen
import com.example.ui.SummaryScreen
import com.example.ui.VoucherViewModel
import com.example.ui.theme.BankSlate900
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = BankSlate900
        ) {
          SecureBankApp()
        }
      }
    }
  }
}

@Composable
fun SecureBankApp(
  viewModel: VoucherViewModel = viewModel()
) {
  val currentScreen by viewModel.currentScreen.collectAsState()

  AnimatedContent(
    targetState = currentScreen,
    transitionSpec = {
      if (targetState.ordinal > initialState.ordinal) {
        (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
          slideOutHorizontally { width -> -width } + fadeOut()
        )
      } else {
        (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
          slideOutHorizontally { width -> width } + fadeOut()
        )
      }
    },
    label = "screen_transition"
  ) { screen ->
    when (screen) {
      AppScreen.DASHBOARD -> DashboardScreen(viewModel = viewModel)
      AppScreen.SCANNER -> ScannerScreen(viewModel = viewModel)
      AppScreen.SUMMARY -> SummaryScreen(viewModel = viewModel)
    }
  }
}
