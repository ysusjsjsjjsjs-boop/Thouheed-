package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = HvacBluePrimaryDark,
    onPrimary = Color(0xFF00344F),
    primaryContainer = HvacBlueContainerDark,
    onPrimaryContainer = Color(0xFFCEEAFF),
    secondary = HvacThermalOrangeDark,
    onSecondary = Color(0xFF531C00),
    secondaryContainer = HvacThermalContainerDark,
    onSecondaryContainer = Color(0xFFFFDBCF),
    tertiary = HvacEcoTealDark,
    onTertiary = Color(0xFF003833),
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondaryDark,
  )

private val LightColorScheme =
  lightColorScheme(
    primary = HvacBluePrimary,
    onPrimary = Color.White,
    primaryContainer = HvacBlueContainer,
    onPrimaryContainer = Color(0xFF001E2E),
    secondary = HvacThermalOrange,
    onSecondary = Color.White,
    secondaryContainer = HvacThermalContainer,
    onSecondaryContainer = Color(0xFF3B1000),
    tertiary = HvacEcoTeal,
    onTertiary = Color.White,
    background = BackgroundLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextSecondaryLight,
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Use our brand colors for cohesive HVAC feel
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

