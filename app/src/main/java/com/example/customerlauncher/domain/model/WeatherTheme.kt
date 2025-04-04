package com.example.customerlauncher.domain.model

import android.graphics.drawable.GradientDrawable

data class WeatherTheme(
    val backgroundGradient: GradientDrawable,
    val cardGradient: GradientDrawable,
    val isDarkText: Boolean
)