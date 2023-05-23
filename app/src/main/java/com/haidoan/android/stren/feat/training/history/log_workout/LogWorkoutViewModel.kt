package com.haidoan.android.stren.feat.training.history.log_workout

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haidoan.android.stren.core.model.*
import com.haidoan.android.stren.core.repository.base.ExercisesRepository
import com.haidoan.android.stren.core.repository.base.RoutinesRepository
import com.haidoan.android.stren.core.repository.base.WorkoutsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

internal const val SELECTED_EXERCISES_IDS_SAVED_STATE_KEY = "selected_exercises_ids"
internal const val NO_SELECTION_ROUTINE_ID = "NO SELECTION ROUTINE ID"
internal const val NO_SELECTION_ROUTINE_NAME = "None"

@HiltViewModel
internal class LogWorkoutViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val routinesRepository: RoutinesRepository,
    private val exercisesRepository: ExercisesRepository,
    private val workoutsRepository: WorkoutsRepository
) : ViewModel() {

    var workoutNameTextFieldValue by mutableStateOf("New workout")
    private lateinit var currentWorkoutInfo: Workout

    private val navArgs: LogWorkoutArgs = LogWorkoutArgs(savedStateHandle)
    private val _trainedExercises: MutableStateFlow<List<TrainedExercise>> =
        MutableStateFlow(listOf())

    private var _currentSelectedRoutineId = NO_SELECTION_ROUTINE_ID
    private val _routines = MutableStateFlow(listOf<Routine>())
    val routines: StateFlow<List<Routine>> = _routines

    private val _secondaryUiState = MutableStateFlow(LogWorkoutSecondaryUiState())
    val secondaryUiState: StateFlow<LogWorkoutSecondaryUiState> = _secondaryUiState

    init {
        Timber.d("init() - userId: ${navArgs.userId} - selectedDate: ${navArgs.selectedDate}")
        viewModelScope.launch {
            val allRoutines = routinesRepository.getRoutinesByUserId(navArgs.userId).toMutableList()
            allRoutines.add(
                0, Routine(
                    id = NO_SELECTION_ROUTINE_ID,
                    name = NO_SELECTION_ROUTINE_NAME,
                    trainedExercises = listOf()
                )
            )
            _routines.value = allRoutines
            // In case user just navigates to LogWorkoutScreen from RoutinesScreen to quickly
            // log workout, then selectedRoutineId won't be NO_SELECTION_ROUTINE_ID
            // at init
            selectRoutine(navArgs.selectedRoutineId)

            Timber.d("init() - selectedRoutineId: ${navArgs.selectedRoutineId}")
            Timber.d("init() - routines: ${routines.value}")
        }

        if (!navArgs.isAddingWorkout) {
            viewModelScope.launch {
                val workout = workoutsRepository.getWorkoutById(navArgs.workoutId)
                workoutNameTextFieldValue = workout.name
                _trainedExercises.value = workout.trainedExercises
                currentWorkoutInfo = workout

                Timber.d("workout: $workout")
                Timber.d(" _trainedExercises.value: ${_trainedExercises.value}")
            }
        }
    }

    fun updateBackConfirmDialogState(shouldShowDialog: Boolean) {
        _secondaryUiState.update { currentState -> currentState.copy(shouldShowBackConfirmDialog = shouldShowDialog) }
    }

    fun updateRoutineWarningDialogState(shouldShowDialog: Boolean) {
        _secondaryUiState.update { currentState ->
            currentState.copy(shouldShowRoutineWarningDialog = shouldShowDialog)
        }
    }

    fun selectRoutine(newlySelectedRoutineId: String) {
        if (newlySelectedRoutineId == _currentSelectedRoutineId) return

        if (_trainedExercises.value.isEmpty()) {
            if (newlySelectedRoutineId != NO_SELECTION_ROUTINE_ID) {
                _currentSelectedRoutineId = newlySelectedRoutineId
                _trainedExercises.value =
                    routines.value.first { it.id == _currentSelectedRoutineId }.trainedExercises
                _secondaryUiState.update { currentState ->
                    currentState.copy(selectedRoutineId = _currentSelectedRoutineId)
                }
            }
        } else {
            _secondaryUiState.update { currentState ->
                currentState.copy(
                    shouldShowRoutineWarningDialog = true,
                    onConfirmSwitchRoutine = {
                        _currentSelectedRoutineId = newlySelectedRoutineId
                        _trainedExercises.value =
                            routines.value.first { it.id == _currentSelectedRoutineId }.trainedExercises
                        _secondaryUiState.update { currentState ->
                            currentState.copy(selectedRoutineId = _currentSelectedRoutineId)
                        }
                    })
            }
        }
    }

    fun setExercisesIdsToAdd(ids: List<String>) {
        //Timber.d("setExercisesIdsToAdd - ids: $ids")
        viewModelScope.launch {
            val exercisesToAdd = exercisesRepository.getExercisesByIds(ids)
            //Timber.d("exercisesToAdd: $exercisesToAdd")

            val currentTrainedExercises = mutableListOf<TrainedExercise>()
            currentTrainedExercises.addAll(_trainedExercises.value)
            currentTrainedExercises.addAll(exercisesToAdd.map { it.asTrainedExerciseWithOneSet() })

            _trainedExercises.value = currentTrainedExercises
            Timber.d("_trainedExercises.value.size: ${_trainedExercises.value.size}")
        }
    }

    fun updateExerciseTrainingSet(
        exerciseToUpdate: TrainedExercise,
        trainingSetToUpdate: TrainingMeasurementMetrics,
        updatedTrainingSetData: TrainingMeasurementMetrics
    ) {
        Timber.d("exerciseToUpdate - unique identifier: ${exerciseToUpdate.id}; content: $exerciseToUpdate")
        Timber.d(
            "trainingSetToUpdate - unique identifier: ${
                trainingSetToUpdate.id
            }; content: $trainingSetToUpdate"
        )
        Timber.d(
            "updatedTrainingSetData - unique identifier: ${
                updatedTrainingSetData.id
            }; content: $updatedTrainingSetData"
        )

        val updatedTrainingSets = _trainedExercises.value.first { trainedExercise ->
            Timber.d("Finding exercise - id: ${trainedExercise.id}")
            trainedExercise.id == exerciseToUpdate.id
        }.trainingSets.replace(updatedTrainingSetData) { trainingSet ->
            Timber.d("Replacing training set - id: ${trainingSet.id}")
            trainingSet.id == trainingSetToUpdate.id
        }
        Timber.d("updatedTrainingSets: $updatedTrainingSets")

        val updatedExercise = _trainedExercises.value.first {
            Timber.d("Finding exercise - id: ${it.id}")
            it.id == exerciseToUpdate.id
        }.copy(trainingSets = updatedTrainingSets)
        Timber.d("updatedExercise: $updatedExercise")

        val updatedTrainedExercises = mutableListOf<TrainedExercise>()
        updatedTrainedExercises.addAll(_trainedExercises.value.replace(updatedExercise) { it.id == exerciseToUpdate.id })
        Timber.d("updatedTrainedExercises: $updatedTrainedExercises")

        _trainedExercises.value = updatedTrainedExercises
        Timber.d("_trainedExercises: ${_trainedExercises.value}")
    }

    fun addEmptyTrainingSet(
        exerciseToUpdate: TrainedExercise,
    ) {

//        Timber.d("addTrainingSet - exerciseToUpdate: unique identifier: ${exerciseToUpdate.id}; content: $exerciseToUpdate")

        val updatedTrainingSets = _trainedExercises.value.first { trainedExercise ->
            Timber.d("Finding exercise - id: ${trainedExercise.id}")
            trainedExercise.id == exerciseToUpdate.id
        }.trainingSets.toMutableList()
        updatedTrainingSets.addEmptyTrainingSet()

        val updatedExercise = _trainedExercises.value.first {
//            Timber.d("Finding exercise - id: ${it.id}")
            it.id == exerciseToUpdate.id
        }.copy(trainingSets = updatedTrainingSets)

        val updatedTrainedExercises = mutableListOf<TrainedExercise>()
        updatedTrainedExercises.addAll(_trainedExercises.value.replace(updatedExercise) { it.id == exerciseToUpdate.id })
//        Timber.d("updatedTrainedExercises: $updatedTrainedExercises")

        _trainedExercises.value = updatedTrainedExercises
//        Timber.d("_trainedExercises: ${_trainedExercises.value}")
    }

    fun deleteExercise(
        exerciseToDelete: TrainedExercise,
    ) {
        val updatedTrainedExercises = _trainedExercises.value.toMutableList()
        updatedTrainedExercises.removeIf { it.id == exerciseToDelete.id }

        Timber.d("updatedTrainedExercises: $updatedTrainedExercises")

        _trainedExercises.value = updatedTrainedExercises
        Timber.d("_trainedExercises: ${_trainedExercises.value}")
    }

    fun deleteTrainingSet(
        exerciseToUpdate: TrainedExercise,
        trainingSetToDelete: TrainingMeasurementMetrics,
    ) {
        Timber.d("exerciseToUpdate - unique identifier: ${exerciseToUpdate.id}; content: $exerciseToUpdate")
        Timber.d(
            "trainingSetToUpdate - unique identifier: ${
                trainingSetToDelete.id
            }; content: $trainingSetToDelete"
        )

        val updatedTrainingSets = _trainedExercises.value.first { trainedExercise ->
            Timber.d("Finding exercise - id: ${trainedExercise.id}")
            trainedExercise.id == exerciseToUpdate.id
        }.trainingSets.toMutableList()
        updatedTrainingSets.removeIf { it.id == trainingSetToDelete.id }

        Timber.d("updatedTrainingSets: $updatedTrainingSets")

        val updatedExercise = _trainedExercises.value.first {
            Timber.d("Finding exercise - id: ${it.id}")
            it.id == exerciseToUpdate.id
        }.copy(trainingSets = updatedTrainingSets)
        Timber.d("updatedExercise: $updatedExercise")

        val updatedTrainedExercises = mutableListOf<TrainedExercise>()
        updatedTrainedExercises.addAll(_trainedExercises.value.replace(updatedExercise) { it.id == exerciseToUpdate.id })
        Timber.d("updatedTrainedExercises: $updatedTrainedExercises")

        _trainedExercises.value = updatedTrainedExercises
        Timber.d("_trainedExercises: ${_trainedExercises.value}")
    }

    fun addEditWorkout() {
        Timber.d("addEditWorkout() - userId: ${navArgs.userId}")
        if (navArgs.isAddingWorkout) {
            viewModelScope.launch {
                workoutsRepository.addWorkout(
                    userId = navArgs.userId,
                    workout = Workout(
                        name = workoutNameTextFieldValue,
                        date = navArgs.selectedDate,
                        trainedExercises = _trainedExercises.value
                    )
                )
            }
        } else {
            viewModelScope.launch {
                workoutsRepository.updateWorkout(
                    userId = navArgs.userId,
                    workout = currentWorkoutInfo.copy(
                        name = workoutNameTextFieldValue,
                        trainedExercises = _trainedExercises.value
                    )
                )
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<LogWorkoutUiState> =
        _trainedExercises.flatMapLatest { _trainedExercises ->
            if (_trainedExercises.isEmpty()) flowOf(LogWorkoutUiState.EmptyWorkout) else flowOf(
                LogWorkoutUiState.IsLogging(navArgs.selectedDate, _trainedExercises)
            )
        }.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), LogWorkoutUiState.Loading
        )

}

private fun <T> List<T>.replace(newValue: T, block: (T) -> Boolean): List<T> {
    return map {
        if (block(it)) newValue else it
    }
}