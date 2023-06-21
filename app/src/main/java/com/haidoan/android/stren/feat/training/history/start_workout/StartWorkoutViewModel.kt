package com.haidoan.android.stren.feat.training.history.start_workout

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haidoan.android.stren.core.model.*
import com.haidoan.android.stren.feat.training.history.StartWorkoutArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

internal const val NO_SELECTION_ROUTINE_ID = "NO SELECTION ROUTINE ID"
internal const val NO_SELECTION_ROUTINE_NAME = "None"

@HiltViewModel
internal class StartWorkoutViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    var workoutNameTextFieldValue by mutableStateOf("New workout")

    val navArgs: StartWorkoutArgs = StartWorkoutArgs(savedStateHandle)
    private val _trainedExercises: MutableStateFlow<List<TrainedExercise>> =
        MutableStateFlow(listOf())

    private val _secondaryUiState = MutableStateFlow(StartWorkoutSecondaryUiState())
    val secondaryUiState: StateFlow<StartWorkoutSecondaryUiState> = _secondaryUiState

    fun updateBackConfirmDialogState(shouldShowDialog: Boolean) {
        _secondaryUiState.update { currentState -> currentState.copy(shouldShowBackConfirmDialog = shouldShowDialog) }
    }

    fun updateRoutineWarningDialogState(shouldShowDialog: Boolean) {
        _secondaryUiState.update { currentState ->
            currentState.copy(shouldShowRoutineWarningDialog = shouldShowDialog)
        }
    }

    /**
     * Since [StartWorkoutViewModel] simply holds uiState for [StartWorkoutScreen] and doesn't hold any business logic (Which is hoisted up in [WorkoutInProgressViewModel] for use across different screens), this method simply does some checks before delegating the actual routine selection to [onSelectRoutine] param
     */
    fun selectRoutineDropdownOption(selectedOptionRoutineId: String, onSelectRoutine: () -> Unit) {
        if (_trainedExercises.value.isEmpty()) {
            if (selectedOptionRoutineId != NO_SELECTION_ROUTINE_ID) {
                onSelectRoutine()
            }
        } else {
            _secondaryUiState.update { currentState ->
                currentState.copy(
                    shouldShowRoutineWarningDialog = true,
                    onConfirmSwitchRoutine = {
                        onSelectRoutine()
                    })
            }
        }
    }

    fun setTrainedExercises(trainedExercises: List<TrainedExercise>) {
        _trainedExercises.update { trainedExercises }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<StartWorkoutUiState> =
        _trainedExercises.flatMapLatest { _trainedExercises ->
            if (_trainedExercises.isEmpty()) flowOf(StartWorkoutUiState.EmptyWorkout) else flowOf(
                StartWorkoutUiState.IsLogging(navArgs.selectedDate, _trainedExercises)
            )
        }.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), StartWorkoutUiState.Loading
        )

}