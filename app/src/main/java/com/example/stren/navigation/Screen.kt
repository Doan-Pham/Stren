package com.example.stren.navigation

sealed class Screen(val route: String) {
    object Splash : Screen(route = "splash_screen")
    object Login : Screen(route = "login_screen")
    object Signup : Screen(route = "signup_screen")
}
