package com.haidoan.android.stren.core.service

import android.net.Uri

interface StorageService {
    suspend fun uploadUserCustomExerciseImage(userId: String, image: Uri): Uri
}