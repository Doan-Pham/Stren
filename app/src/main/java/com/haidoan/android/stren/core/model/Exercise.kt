package com.haidoan.android.stren.core.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class Exercise(
    @DocumentId
    @SerialName("objectID")
    val id: String = "Undefined",
    val name: String,
    val instructions: List<String> = listOf(),

    @SerialName("images")
    @PropertyName("images")
    val imageUrls: List<String> = listOf(),

    @SerialName("category")
    @PropertyName("category")
    val belongedCategory: String = "",

    @SerialName("primaryMuscles")
    @PropertyName("primaryMuscles")
    val trainedMuscleGroups: List<String> = listOf()
)

data class ExerciseQueryParameters(
    val exerciseName: String,
    val exerciseCategories: List<ExerciseCategory>,
    val muscleGroupsTrained: List<MuscleGroup>
)