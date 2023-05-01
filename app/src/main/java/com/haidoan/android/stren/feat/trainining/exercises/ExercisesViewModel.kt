package com.haidoan.android.stren.feat.trainining.exercises

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.haidoan.android.stren.core.repository.ExercisesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

private const val TAG = "ExercisesViewModel"

@HiltViewModel
internal class ExercisesViewModel @Inject constructor(exercisesRepository: ExercisesRepository) :
    ViewModel() {
    var searchBarText = mutableStateOf("")

    private val exerciseNameToQuery = MutableStateFlow("")
    fun searchExerciseByName(exerciseName: String) {
        Log.d(TAG, "searchExerciseByName() - [Param]exerciseName: $exerciseName")
        exerciseNameToQuery.value = exerciseName
        Log.d(
            TAG,
            "searchExerciseByName() - [Param]exerciseNameToQuery.value: ${exerciseNameToQuery.value}"
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val exercises = exerciseNameToQuery.filterNotNull().distinctUntilChanged()
        .flatMapLatest { exerciseNameToQuery ->
            if (exerciseNameToQuery.isEmpty() || exerciseNameToQuery.isBlank()) {
                exercisesRepository.getExercisesWithLimit()
            } else {
                exercisesRepository.getExercisesByNameWithLimit(exerciseName = exerciseNameToQuery)
            }

        }
        .cachedIn(viewModelScope)

}