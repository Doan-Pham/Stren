package com.haidoan.android.stren.core.model

import com.google.firebase.firestore.DocumentId

data class ExerciseCategory(
    @DocumentId
    val id: String,
    val name: String
)