package com.currency.exchanger.ui.theme

import android.app.Activity
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun ConverterTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = colors,
        shapes = ConverterShapes,
        typography = Typography(),
        content = content
    )
}

object ConverterTheme {
    val colors: ColorScheme
        @Composable
        @ReadOnlyComposable
        get() = LocalColors.current

    val typography: ConverterTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalTypography.current
}