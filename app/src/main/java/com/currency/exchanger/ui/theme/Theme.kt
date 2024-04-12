package com.currency.exchanger.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable

@Composable
fun ExchangeRateTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = colors,
        shapes = ExchangeRateShapes,
        typography = Typography(),
        content = content
    )
}

object ExchangeRateTheme {
    val colors: ColorScheme
        @Composable
        @ReadOnlyComposable
        get() = LocalColors.current

    val typography: ExchangeRateTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalTypography.current
}