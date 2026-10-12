package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
  primary = BankBluePrimary,
  onPrimary = Color.White,
  primaryContainer = BankBlueLight,
  onPrimaryContainer = BankBlueDark,
  secondary = BankBlueAccent,
  onSecondary = Color.White,
  secondaryContainer = BankBlueContainer,
  onSecondaryContainer = BankBluePrimary,
  tertiary = BankEmerald,
  onTertiary = Color.White,
  tertiaryContainer = BankEmeraldContainer,
  onTertiaryContainer = BankEmerald,
  background = BankSlate50,
  onBackground = BankSlate900,
  surface = Color.White,
  onSurface = BankSlate900,
  surfaceVariant = BankSlate100,
  onSurfaceVariant = BankSlate600,
  outline = BankSlate300,
  error = BankRed,
  onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
  primary = BankBlueAccent,
  onPrimary = Color.White,
  primaryContainer = BankBlueDark,
  onPrimaryContainer = BankBlueLight,
  secondary = BankIndigo,
  onSecondary = Color.White,
  background = BankSlate900,
  onBackground = Color.White,
  surface = BankSlate800,
  onSurface = Color.White,
  surfaceVariant = BankSlate700,
  onSurfaceVariant = BankSlate200,
  outline = BankSlate600,
  error = BankRed,
  onError = Color.White
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as? Activity)?.window
      if (window != null) {
        window.statusBarColor = BankBlueDark.toArgb()
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
      }
    }
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
