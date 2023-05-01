package com.haidoan.android.stren.feat.trainining.exercises

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.haidoan.android.stren.core.repository.ExercisesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

private const val TAG = "ExercisesViewModel"

@HiltViewModel
internal class ExercisesViewModel @Inject constructor(exercisesRepository: ExercisesRepository) :
    ViewModel() {
    var searchQuery by mutableStateOf("")

    val exercises =
        exercisesRepository.getExercisesWithLimit().cachedIn(viewModelScope)

    fun searchExerciseByName(exerciseName: String) {
        Log.d(TAG, "searchExerciseByName() - [Param]exerciseName: $exerciseName")
    }
}