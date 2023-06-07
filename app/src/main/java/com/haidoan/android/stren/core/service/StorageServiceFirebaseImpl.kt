package com.haidoan.android.stren.core.service

import android.net.Uri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.haidoan.android.stren.core.utils.DateUtils
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StorageServiceFirebaseImpl @Inject constructor() : StorageService {
    private val storageRef = Firebase.storage.reference
    private val userCustomExerciseRef = storageRef.child("User/")

    override suspend fun uploadUserCustomExerciseImage(userId: String, image: Uri): Uri {
        val imageFileName = "${DateUtils.getCurrentTimeEpochSecond()}.png"
        val newFileRef = userCustomExerciseRef.child("$userId/CustomExercise/$imageFileName")
        newFileRef.putFile(image).await()
        return newFileRef.downloadUrl.await()
    }
}