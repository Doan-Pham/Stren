package com.haidoan.android.stren.core.model

data class Exercise(
    val name: String,
    val instruction: String,
    val imageUrl: String,
    val belongedCategories: List<String>,
    val trainedMuscleGroups: List<String>
)
