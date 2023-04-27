package com.haidoan.android.stren.core.model

import com.google.firebase.firestore.DocumentId

data class Exercise(
    @DocumentId
    val id: String,
    val name: String,
    val instruction: String = "",
    val imageUrl: String = "",
    val belongedCategories: List<String> = listOf(),
    val trainedMuscleGroups: List<String> = listOf()
)
