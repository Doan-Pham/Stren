package com.example.stren.core.service

interface AuthenticationService {
    suspend fun authenticate(email: String, password: String)
}
