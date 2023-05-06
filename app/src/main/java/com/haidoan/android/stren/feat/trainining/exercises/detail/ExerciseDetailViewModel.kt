package com.haidoan.android.stren.feat.trainining.exercises.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haidoan.android.stren.core.model.Exercise
import com.haidoan.android.stren.core.repository.ExercisesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ExerciseDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    exercisesRepository: ExercisesRepository
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentExercise: StateFlow<Exercise> =
        savedStateHandle.getStateFlow(EXERCISE_ID_ARG, "Undefined")
            .flatMapLatest { exercisesRepository.getExerciseById(it) }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                Exercise(id = "", name = "")
            )
}