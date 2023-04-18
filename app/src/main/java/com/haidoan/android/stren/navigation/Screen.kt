package com.haidoan.android.stren.navigation

sealed class Screen(val route: String) {
    object Login : Screen(route = "login_screen")
    object Signup : Screen(route = "signup_screen")
}
