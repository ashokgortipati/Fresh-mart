package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
  primary = FreshGreenContainer,
  onPrimary = FreshGreenOnContainer,
  primaryContainer = FreshGreenPrimary,
  onPrimaryContainer = FreshGreenOnPrimary,
  secondary = OceanBlueContainer,
  onSecondary = OceanBlueOnContainer,
  secondaryContainer = OceanBlueSecondary,
  onSecondaryContainer = OceanBlueOnSecondary,
  tertiary = CoralContainer,
  onTertiary = CoralOnContainer,
  background = BackgroundDark,
  surface = SurfaceDark,
  surfaceVariant = SurfaceVariantDark,
  onBackground = OnBackgroundDark,
  onSurface = OnSurfaceDark,
  onSurfaceVariant = OnSurfaceVariantDark,
  outline = OutlineDark
)

private val LightColorScheme = lightColorScheme(
  primary = FreshGreenPrimary,
  onPrimary = FreshGreenOnPrimary,
  primaryContainer = FreshGreenContainer,
  onPrimaryContainer = FreshGreenOnContainer,
  secondary = OceanBlueSecondary,
  onSecondary = OceanBlueOnSecondary,
  secondaryContainer = OceanBlueContainer,
  onSecondaryContainer = OceanBlueOnContainer,
  tertiary = CoralTertiary,
  onTertiary = CoralOnTertiary,
  tertiaryContainer = CoralContainer,
  onTertiaryContainer = CoralOnContainer,
  background = BackgroundLight,
  surface = SurfaceLight,
  surfaceVariant = SurfaceVariantLight,
  onBackground = OnBackgroundLight,
  onSurface = OnSurfaceLight,
  onSurfaceVariant = OnSurfaceVariantLight,
  outline = OutlineLight
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Keep consistent FreshMart brand palette
  content: @Composable () -> Unit,
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}

