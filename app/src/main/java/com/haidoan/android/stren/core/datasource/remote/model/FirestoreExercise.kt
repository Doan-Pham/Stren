package com.haidoan.android.stren.core.datasource.remote.model

import com.google.firebase.firestore.DocumentSnapshot
import com.haidoan.android.stren.core.model.Exercise
import timber.log.Timber

internal fun DocumentSnapshot.toExercise(): Exercise {
    Timber.d("document: $this")
    @Suppress("UNCHECKED_CAST")
    return Exercise(
        id = this.id,
        name = this.getString("name") ?: "",
        instructions = this.get("instructions") as List<String>,
        imageUrls = this.get("images") as List<String>,
        belongedCategory = this.getString("category") ?: "",
        trainedMuscleGroups = this.get("primaryMuscles") as List<String>,
        isCustomExercise = (this.get("isCustomExercise") ?: false) as Boolean,
        userId = (this.get("userId") ?: "") as String,
    )
}

internal fun Exercise.toFirestoreObject(): Map<String, Any> {
    val result = mutableMapOf<String, Any>()
    result["instructions"] = this.instructions
    result["images"] = this.imageUrls
    result["name"] = this.name
    result["category"] = this.belongedCategory
    result["primaryMuscles"] = this.trainedMuscleGroups
    result["isCustomExercise"] = this.isCustomExercise
    result["userId"] = this.userId
    Timber.d("Exercise.toFirestoreObject() - result: $result")
    return result
}