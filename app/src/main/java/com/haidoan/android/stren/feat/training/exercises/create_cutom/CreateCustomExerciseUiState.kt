package com.haidoan.android.stren.feat.training.exercises.create_cutom

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.haidoan.android.stren.core.model.ExerciseCategory

internal data class CreateCustomExerciseUiState(
    val exerciseName: String = "",
    val exerciseCategory: ExerciseCategory = ExerciseCategory.undefined,
    val primaryTrainedMusclesIds: SnapshotStateList<String> = mutableStateListOf(),
    val secondaryTrainedMusclesIds: SnapshotStateList<String> = mutableStateListOf(),
    val exerciseImages: List<String> = listOf()
)