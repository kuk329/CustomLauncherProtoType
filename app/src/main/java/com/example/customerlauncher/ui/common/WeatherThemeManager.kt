package com.example.customerlauncher.ui.common

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import com.example.customerlauncher.domain.model.WeatherTheme

object WeatherThemeManager {
    fun getThemeForWeather(code: Int): WeatherTheme {
        val (bgStart, bgEnd, cardStart, cardEnd, darkText) = when (code) {
            in 200..299 -> listOf("#512DA8", "#000000", "#D1C4E9", "#B39DDB", true)
            in 300..399 -> listOf("#90CAF9", "#ECEFF1", "#FFFFFF", "#E3F2FD", false)
            in 500..599 -> listOf("#1A237E", "#455A64", "#E3F2FD", "#FFFFFF", false)
            in 600..699 -> listOf("#FFFFFF", "#E3F2FD", "#F5F5F5", "#EEEEEE", false)
            in 700..799 -> listOf("#FFF176", "#E53935", "#FFF3E0", "#FFCCBC", true)
            800 -> listOf("#FF9800", "#FFF59D", "#FFB74D", "#FFFDE7", true)
            in 801..804 -> listOf("#B0BEC5", "#ECEFF1", "#FFFFFF", "#F5F5F5", true)
            else -> listOf("#263238", "#000000", "#EEEEEE", "#E0E0E0", true)
        }

        return WeatherTheme(
            backgroundGradient = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(Color.parseColor(bgStart.toString()), Color.parseColor(bgEnd.toString()))
            ),
            cardGradient = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(Color.parseColor(cardStart.toString()), Color.parseColor(cardEnd.toString()))
            ).apply {
                    cornerRadius=32f
            },
            isDarkText = darkText as Boolean
        )
    }
}