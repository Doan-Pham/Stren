package com.haidoan.android.stren.core.model

import com.google.firebase.firestore.DocumentId
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class Exercise(
    @DocumentId
    @SerialName("objectID")
    val id: String,
    val name: String,
    val instruction: List<String> = listOf(),
    @SerialName("images")
    val imageUrls: List<String> = listOf(),
    @SerialName("category")
    val belongedCategory: String = "",
    @SerialName("primaryMuscles")
    val trainedMuscleGroups: List<String> = listOf()
)

data class ExerciseQueryParameters(
    val exerciseName: String,
    val exerciseCategories: List<ExerciseCategory>,
    val muscleGroupsTrained: List<MuscleGroup>
)