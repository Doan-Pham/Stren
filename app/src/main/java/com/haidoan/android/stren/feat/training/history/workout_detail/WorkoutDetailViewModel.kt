package com.haidoan.android.stren.feat.training.history.workout_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haidoan.android.stren.core.repository.base.WorkoutsRepository
import com.haidoan.android.stren.feat.training.history.WorkoutDetailArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class WorkoutDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val workoutsRepository: WorkoutsRepository
) : ViewModel() {
    val navArgs: WorkoutDetailArgs = WorkoutDetailArgs(savedStateHandle)

    private val _uiState: MutableStateFlow<WorkoutDetailUiState> =
        MutableStateFlow(WorkoutDetailUiState.Loading)
    val uiState: StateFlow<WorkoutDetailUiState> = _uiState.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), WorkoutDetailUiState.Loading
    )

    init {
        Timber.d("init() - userId: ${navArgs.userId} - workoutId: ${navArgs.workoutId}")
        viewModelScope.launch {
            val currentWorkout = workoutsRepository.getWorkoutById(navArgs.workoutId)
            _uiState.update { WorkoutDetailUiState.LoadComplete(currentWorkout) }
        }
    }
}
