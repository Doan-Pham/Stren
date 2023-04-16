package com.example.stren.core.service


interface AuthenticationService {
    fun addAuthStateListeners(onUserAuthenticated: () -> Unit, onUserNotAuthenticated: () -> Unit)
    suspend fun authenticate(email: String, password: String)
}
