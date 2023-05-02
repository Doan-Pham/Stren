package com.haidoan.android.stren.core.model

import com.google.firebase.firestore.DocumentId

//Without default values, Firestore's toObject() won't work
data class ExerciseCategory(
    @DocumentId
    val id: String = "UNDEFINED_ID",
    val name: String = "UNDEFINED_NAME"
)