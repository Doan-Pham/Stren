package com.haidoan.android.stren.feat.training.exercises.create_cutom

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.haidoan.android.stren.core.model.ExerciseCategory

internal data class CreateCustomExerciseUiState(
    val exerciseName: String = "",
    val instruction: String = "",
    val exerciseCategory: ExerciseCategory = ExerciseCategory.undefined,
    val trainedMusclesIds: SnapshotStateList<String> = mutableStateListOf(),
    val startImage: Uri? = null,
    val endImage: Uri? = null
)