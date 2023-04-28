package com.haidoan.android.stren.feat.trainining.exercises

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haidoan.android.stren.core.repository.ExercisesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
internal class ExercisesViewModel @Inject constructor(exercisesRepository: ExercisesRepository) :
    ViewModel() {
    val uiState: StateFlow<ExercisesUiState> =
        exercisesRepository.getAllExercisesStream()
            .map { ExercisesUiState.LoadComplete(it) }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                ExercisesUiState.Loading
            )
}