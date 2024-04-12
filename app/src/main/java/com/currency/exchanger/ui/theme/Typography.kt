package com.currency.exchanger.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

data class ExchangeRateTypography(
    val title: TextStyle = TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = 64.sp,
        letterSpacing = (-0.01).em,
        lineHeight = 76.sp
    ),
    val headlineMedium: TextStyle = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
    ),
)

val LocalTypography = staticCompositionLocalOf {
    ExchangeRateTypography()
}