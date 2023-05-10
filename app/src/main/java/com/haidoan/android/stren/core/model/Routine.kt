package com.haidoan.android.stren.core.model

import com.google.firebase.firestore.DocumentId

data class Routine(
    @DocumentId
    val id: String,
    val name: String,
    val note: String,
    val trainedExercises: List<TrainedExercise>
)
