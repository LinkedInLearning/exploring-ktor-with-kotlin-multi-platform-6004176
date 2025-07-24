package com.kmp.explore

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Details : Screen("details") // example additional screen
}