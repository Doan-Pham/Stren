package com.haidoan.android.stren.app

import android.os.CountDownTimer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haidoan.android.stren.core.model.*
import com.haidoan.android.stren.core.repository.base.ExercisesRepository
import com.haidoan.android.stren.core.repository.base.RoutinesRepository
import com.haidoan.android.stren.core.repository.base.WorkoutsRepository
import com.haidoan.android.stren.core.utils.DateUtils
import com.haidoan.android.stren.core.utils.ListUtils.replaceWith
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.util.*
import javax.inject.Inject

private const val NO_SELECTION_ROUTINE_ID = "NO SELECTION ROUTINE ID"
private const val NO_SELECTION_ROUTINE_NAME = "None"
private const val UNDEFINED_USER_ID = "UNDEFINED_USER_ID"

// TODO: The ways this class is implemented is similar to [LogWorkoutViewModel] which leads to code duplication
/**
 * This class keeps track of the workout currently in progress (training sets, timers). Since this info must persist through any individual screens' termination from the backstack so that user can access it from many different screens and through notification, this class is put in the "app" package
 */

@HiltViewModel
class WorkoutInProgressViewModel @Inject constructor(
    private val routinesRepository: RoutinesRepository,
    private val exercisesRepository: ExercisesRepository,
    private val workoutsRepository: WorkoutsRepository
) : ViewModel() {

    private val initArgs = MutableStateFlow(WorkoutInProgressInitArgs())
    var cachedUserId = ""
        private set

    var isInitialized by mutableStateOf(false)
        private set

    var workoutNameTextFieldValue by mutableStateOf("New workout")
    private var restTimer: CountDownTimer? = null

    var currentSelectedRoutineId by mutableStateOf(NO_SELECTION_ROUTINE_ID)
        private set

    @OptIn(ExperimentalCoroutinesApi::class)
    val routines: StateFlow<List<Routine>> = initArgs.mapLatest { initArgs ->
        val result = routinesRepository.getRoutinesByUserId(initArgs.userId).toMutableList()
        result.add(
            0, Routine(
                id = NO_SELECTION_ROUTINE_ID,
                name = NO_SELECTION_ROUTINE_NAME,
                trainedExercises = listOf()
            )
        )
        Timber.d("routines: $result")
        return@mapLatest result
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        listOf()
    )

    private val _trainedExercises: MutableStateFlow<List<TrainedExercise>> =
        MutableStateFlow(listOf())

    val trainedExercises: StateFlow<List<TrainedExercise>> =
        _trainedExercises.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            listOf()
        )

    private val _uiState = MutableStateFlow(WorkoutInProgressUiState())
    val uiState: StateFlow<WorkoutInProgressUiState> = _uiState
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            WorkoutInProgressUiState()
        )

    init {
        Timber.d("init() - userId: ${initArgs.value.userId} - selectedDate: ${initArgs.value.selectedDate}")

        // In case user just navigates to StartWorkoutScreen from RoutinesScreen to quickly
        // log workout, then selectedRoutineId won't be NO_SELECTION_ROUTINE_ID
        // at init
        selectRoutine(initArgs.value.selectedRoutineId)
        Timber.d("init() - selectedRoutineId: ${initArgs.value.selectedRoutineId}")
    }

    /**
     * [WorkoutInProgressViewModel]'s init args differ from screens' nav args in that [WorkoutInProgressViewModel] is not tied to any screen, so it can't use savedStateHandle to fetch such arguments. Therefore, this method is implemented to manually set such args
     */
    fun setInitArgs(initArgs: WorkoutInProgressInitArgs) {
        cachedUserId = initArgs.userId
        this.isInitialized = true
        this.initArgs.update { initArgs }
    }

    fun startWorkingOut() {
        Timber.d("startWorkingOut()")
        _uiState.update { it.copy(isTraineeWorkingOut = true) }
    }

    fun finishWorkout() {
        Timber.d("finishWorkout() - userId: ${initArgs.value.userId}")
        viewModelScope.launch {
            workoutsRepository.addWorkout(
                userId = initArgs.value.userId,
                workout = Workout(
                    name = workoutNameTextFieldValue,
                    date = initArgs.value.selectedDate,
                    trainedExercises = _trainedExercises.value
                )
            )
        }
        restTimer?.cancel()
        _uiState.update {
            it.copy(
                isTraineeWorkingOut = false,
                isTraineeResting = false,
                isTraineeFinishResting = false
            )
        }
        _trainedExercises.value = listOf()
        currentSelectedRoutineId = NO_SELECTION_ROUTINE_ID
        isInitialized = false
        workoutNameTextFieldValue = "New Workout"
    }

    fun cancelWorkout() {
        restTimer?.cancel()
        _uiState.update {
            it.copy(
                isTraineeWorkingOut = false,
                isTraineeResting = false,
                isTraineeFinishResting = false
            )
        }
        _trainedExercises.value = listOf()
        currentSelectedRoutineId = NO_SELECTION_ROUTINE_ID
        isInitialized = false
        workoutNameTextFieldValue = "New Workout"
    }

    fun selectRoutine(newlySelectedRoutineId: String) {
        if (newlySelectedRoutineId == currentSelectedRoutineId) return
        currentSelectedRoutineId = newlySelectedRoutineId

        Timber.d(
            "selectRoutine() - newlySelectedRoutineId: $newlySelectedRoutineId ;_currentSelectedRoutineId: $currentSelectedRoutineId"
        )
        _trainedExercises.value =
            routines.value.firstOrNull { it.id == currentSelectedRoutineId }?.trainedExercises
                ?: listOf()
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

    fun deleteExercise(
        exerciseToDelete: TrainedExercise,
    ) {
        val updatedTrainedExercises = _trainedExercises.value.toMutableList()
        updatedTrainedExercises.removeIf { it.id == exerciseToDelete.id }

        Timber.d("updatedTrainedExercises: $updatedTrainedExercises")

        _trainedExercises.value = updatedTrainedExercises
        Timber.d("_trainedExercises: ${_trainedExercises.value}")
    }

    private fun startRestTimer(
        remainingRestDurationInSeconds: Long = _uiState.value.totalRestDurationInSeconds,
        durationIncrementAmountInSeconds: Long = _uiState.value.durationIncrementAmountInSeconds,
        durationDecrementAmountInSeconds: Long = _uiState.value.durationDecrementAmountInSeconds,
    ) {
        restTimer?.cancel()
        _uiState.update {
            it.copy(
                isTraineeResting = true,
                remainingRestDurationInSeconds = remainingRestDurationInSeconds,
                durationIncrementAmountInSeconds = durationIncrementAmountInSeconds,
                durationDecrementAmountInSeconds = durationDecrementAmountInSeconds
            )
        }
        restTimer =
            object : CountDownTimer(remainingRestDurationInSeconds * 1000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    _uiState.update { it.copy(remainingRestDurationInSeconds = _uiState.value.remainingRestDurationInSeconds - 1) }
                }

                override fun onFinish() {
                    _uiState.update {
                        it.copy(
                            isTraineeFinishResting = true,
                            isTraineeResting = false,
                            remainingRestDurationInSeconds = _uiState.value.totalRestDurationInSeconds
                        )
                    }
                }
            }.start()
    }

    fun finishRestTimer() {
        _uiState.update {
            it.copy(
                isTraineeFinishResting = false,
            )
        }
    }

    fun incrementRestTimer() {
        val remainingRestDuration =
            _uiState.value.remainingRestDurationInSeconds + _uiState.value.durationIncrementAmountInSeconds
        _uiState.update { it.copy(remainingRestDurationInSeconds = remainingRestDuration) }
        startRestTimer(
            remainingRestDurationInSeconds = remainingRestDuration
        )
    }

    fun decrementRestTimer() {
        val remainingRestDuration =
            _uiState.value.remainingRestDurationInSeconds - _uiState.value.durationDecrementAmountInSeconds
        if (remainingRestDuration < 1) {
            restTimer?.onFinish()
        } else {
            _uiState.update { it.copy(remainingRestDurationInSeconds = remainingRestDuration) }
            startRestTimer(remainingRestDurationInSeconds = remainingRestDuration)
        }
    }

    fun skipRestTimer() {
        _uiState.update {
            it.copy(
                isTraineeFinishResting = true,
                isTraineeResting = false,
                remainingRestDurationInSeconds = _uiState.value.totalRestDurationInSeconds
            )
        }
        restTimer?.onFinish()
        restTimer?.cancel()
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
        }.trainingSets.replaceWith(updatedTrainingSetData) { trainingSet ->
            Timber.d("Replacing training set - id: ${trainingSet.id}")
            trainingSet.id == trainingSetToUpdate.id
        }
        Timber.d("updatedTrainingSets: $updatedTrainingSets")

        val updatedExercise = _trainedExercises.value.first {
            Timber.d("Finding exercise - id: ${it.id}")
            it.id == exerciseToUpdate.id
        }.copy(id = exerciseToUpdate.id, trainingSets = updatedTrainingSets)
        Timber.d("updatedExercise: $updatedExercise")

        val updatedTrainedExercises = mutableListOf<TrainedExercise>()
        updatedTrainedExercises.addAll(_trainedExercises.value.replaceWith(updatedExercise) { it.id == exerciseToUpdate.id })
        Timber.d("updatedTrainedExercises: $updatedTrainedExercises")

        _trainedExercises.value = updatedTrainedExercises
        Timber.d("_trainedExercises: ${_trainedExercises.value}")
    }

    fun toggleTrainingSetCompleteState(
        exerciseToUpdate: TrainedExercise,
        trainingSetToUpdate: TrainingMeasurementMetrics,
        isTrainingSetComplete: Boolean
    ) {
        Timber.d("exerciseToUpdate - unique identifier: ${exerciseToUpdate.id}; content: $exerciseToUpdate")
        Timber.d(
            "trainingSetToUpdate - unique identifier: ${
                trainingSetToUpdate.id
            }; content: $trainingSetToUpdate"
        )

        val updatedTrainingSets = _trainedExercises.value.first { trainedExercise ->
            Timber.d("Finding exercise - id: ${trainedExercise.id}")
            trainedExercise.id == exerciseToUpdate.id
        }.trainingSets.replaceWith(trainingSetToUpdate.withCompleteState(isTrainingSetComplete)) { trainingSet ->
            Timber.d("Replacing training set - id: ${trainingSet.id}")
            trainingSet.id == trainingSetToUpdate.id
        }
        Timber.d("updatedTrainingSets: $updatedTrainingSets")

        val updatedExercise = _trainedExercises.value.first {
            Timber.d("Finding exercise - id: ${it.id}")
            it.id == exerciseToUpdate.id
        }.copy(id = exerciseToUpdate.id, trainingSets = updatedTrainingSets)
        Timber.d("updatedExercise: $updatedExercise")

        val updatedTrainedExercises = mutableListOf<TrainedExercise>()
        updatedTrainedExercises.addAll(_trainedExercises.value.replaceWith(updatedExercise) { it.id == exerciseToUpdate.id })
        Timber.d("updatedTrainedExercises: $updatedTrainedExercises")

        _trainedExercises.value = updatedTrainedExercises
        Timber.d("_trainedExercises: ${_trainedExercises.value}")

        if (isTrainingSetComplete) startRestTimer()
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
        }.copy(id = exerciseToUpdate.id, trainingSets = updatedTrainingSets)

        val updatedTrainedExercises = mutableListOf<TrainedExercise>()
        updatedTrainedExercises.addAll(_trainedExercises.value.replaceWith(updatedExercise) { it.id == exerciseToUpdate.id })
//        Timber.d("updatedTrainedExercises: $updatedTrainedExercises")

        _trainedExercises.value = updatedTrainedExercises
//        Timber.d("_trainedExercises: ${_trainedExercises.value}")
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
        }.copy(id = exerciseToUpdate.id, trainingSets = updatedTrainingSets)
        Timber.d("updatedExercise: $updatedExercise")

        val updatedTrainedExercises = mutableListOf<TrainedExercise>()
        updatedTrainedExercises.addAll(_trainedExercises.value.replaceWith(updatedExercise) { it.id == exerciseToUpdate.id })
        Timber.d("updatedTrainedExercises: $updatedTrainedExercises")

        _trainedExercises.value = updatedTrainedExercises
        Timber.d("_trainedExercises: ${_trainedExercises.value}")
    }

    override fun onCleared() {
        super.onCleared()
        restTimer?.cancel()
    }
}

/**
 * Since [WorkoutInProgressViewModel] is not scoped to any particular screen, it can't have nav args. However, it stills needs some init arguments to work
 */
data class WorkoutInProgressInitArgs(
    val userId: String = UNDEFINED_USER_ID,
    val selectedDate: LocalDate = DateUtils.getCurrentDate(),
    val selectedRoutineId: String = NO_SELECTION_ROUTINE_ID
)

data class WorkoutInProgressUiState(
    val isTraineeWorkingOut: Boolean = false,
    val isTraineeResting: Boolean = false,
    val isTraineeFinishResting: Boolean = false,
    val durationDecrementAmountInSeconds: Long = 15L,
    val durationIncrementAmountInSeconds: Long = 15L,
    val totalRestDurationInSeconds: Long = 60L,
    val remainingRestDurationInSeconds: Long = 60L,
)