package com.haidoan.android.stren.core.model

import com.google.firebase.firestore.DocumentId

data class Routine(
    @DocumentId
    val id: String = "Undefined Routine ID",
    val name: String,
    val trainedExercises: List<TrainedExercise>
)
