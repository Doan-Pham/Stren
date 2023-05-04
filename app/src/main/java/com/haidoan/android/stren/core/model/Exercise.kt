package com.haidoan.android.stren.core.model

import com.google.firebase.firestore.DocumentId

data class Exercise(
    @DocumentId
    val id: String,
    val name: String,
    val instruction: List<String> = listOf(),
    val imageUrls: List<String> = listOf(),
    val belongedCategory: String = "",
    val trainedMuscleGroups: List<String> = listOf()
)

data class ExerciseFilterStandards(
    val exerciseName: String,
    val exerciseCategories: List<ExerciseCategory>,
    val muscleGroupsTrained: List<MuscleGroup>
)