package com.haidoan.android.stren.feat.training.history.start_workout

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haidoan.android.stren.core.designsystem.component.ConfirmationDialogState
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

    /**
     * Since [StartWorkoutViewModel] simply holds uiState for [StartWorkoutScreen] and doesn't hold any business logic (Which is hoisted up in [WorkoutInProgressViewModel] for use across different screens), this method simply does some checks before delegating the actual routine selection to [onSelectRoutine] param
     */
    fun selectRoutineDropdownOption(selectedOptionRoutineId: String, onSelectRoutine: () -> Unit) {
        if (_trainedExercises.value.isEmpty()) {
            if (selectedOptionRoutineId != NO_SELECTION_ROUTINE_ID) onSelectRoutine()
        } else {
            _secondaryUiState.update { currentState ->
                currentState.copy(
                    shouldShowConfirmDialog = true,
                    confirmDialogState = ConfirmationDialogState(
                        title = "Switch to another routine",
                        body = "Once you you switch to another routine, the exercises you've added will be lost. Are you sure you want to continue?",
                        onDismissDialog = {
                            _secondaryUiState.update { it.copy(shouldShowConfirmDialog = false) }
                        },
                        onConfirmClick = onSelectRoutine
                    )
                )
            }
        }
    }

    /**
     * Since [StartWorkoutViewModel] simply holds uiState for [StartWorkoutScreen] and doesn't hold any business logic (Which is hoisted up in [WorkoutInProgressViewModel] for use across different screens), this method simply does some checks before delegating the actual business logic to [onCancelWorkout] param
     */

    fun cancelWorkout(onCancelWorkout: () -> Unit) {
        _secondaryUiState.update { currentState ->
            currentState.copy(
                shouldShowConfirmDialog = true,
                confirmDialogState = ConfirmationDialogState(
                    title = "Cancel workout",
                    body = "Are you sure you want to cancel this workout? This action can't be undone ",
                    onDismissDialog = {
                        _secondaryUiState.update { it.copy(shouldShowConfirmDialog = false) }
                    },
                    onConfirmClick = onCancelWorkout
                )
            )
        }
    }

    /**
     * Since [StartWorkoutViewModel] simply holds uiState for [StartWorkoutScreen] and doesn't hold any business logic (Which is hoisted up in [WorkoutInProgressViewModel] for use across different screens), this method simply does some checks before delegating the actual logic selection to [onToggleTrainingSetCompleteState] param
     */
    fun toggleTrainingSetCompleteState(
        trainingSet: TrainingMeasurementMetrics,
        onToggleTrainingSetCompleteState: () -> Unit
    ) {
        if (trainingSet.isEmpty()) {
            _secondaryUiState.update { currentState ->
                currentState.copy()
            }
        } else {
            onToggleTrainingSetCompleteState()
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
