package com.haidoan.android.stren.feat.trainining.history.log_workout

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haidoan.android.stren.core.model.*
import com.haidoan.android.stren.core.repository.ExercisesRepository
import com.haidoan.android.stren.core.repository.RoutinesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

const val SELECTED_EXERCISES_IDS_SAVED_STATE_KEY = "selected_exercises_ids"

@HiltViewModel
internal class LogWorkoutViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val routinesRepository: RoutinesRepository,
    private val exercisesRepository: ExercisesRepository
) : ViewModel() {
    var workoutNameTextFieldValue by mutableStateOf("New workout")
    private val navArgs: LogWorkoutArgs = LogWorkoutArgs(savedStateHandle)
    private val _trainedExercises: MutableStateFlow<List<TrainedExercise>> =
        MutableStateFlow(listOf())

    init {
        Timber.d("init() - userId: ${navArgs.userId} - seletecdDate: ${navArgs.selectedDate}")
//        if (!navArgs.isAddingRoutine) {
//            viewModelScope.launch {
//                val routine = routinesRepository.getRoutineById(navArgs.userId, navArgs.routineId)
//                routineNameTextFieldValue = routine.name
//                _trainedExercises.value = routine.trainedExercises
//
//                Timber.d("routine: $routine")
//                Timber.d(" _trainedExercises.value: ${_trainedExercises.value}")
//            }
//        }
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

//    fun addEditRoutine() {
//        Timber.d("addEditRoutine() - userId: ${navArgs.userId}; routineId: ${navArgs.routineId}")
//        if (navArgs.isAddingRoutine) {
//            viewModelScope.launch {
//                routinesRepository.addRoutine(
//                    userId = navArgs.userId,
//                    routine = Routine(
//                        name = routineNameTextFieldValue,
//                        trainedExercises = _trainedExercises.value
//                    )
//                )
//            }
//        } else {
//            viewModelScope.launch {
//                routinesRepository.updateRoutine(
//                    userId = navArgs.userId,
//                    routine = Routine(
//                        id = navArgs.routineId,
//                        name = routineNameTextFieldValue,
//                        trainedExercises = _trainedExercises.value
//                    )
//                )
//            }
//        }
//    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<LogWorkoutUiState> =
        _trainedExercises.flatMapLatest { _trainedExercises ->
            if (_trainedExercises.isEmpty()) flowOf(LogWorkoutUiState.EmptyWorkout) else
                flowOf(LogWorkoutUiState.IsLogging(navArgs.selectedDate, _trainedExercises))
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000), LogWorkoutUiState.Loading
        )

}

private fun <T> List<T>.replace(newValue: T, block: (T) -> Boolean): List<T> {
    return map {
        if (block(it)) newValue else it
    }
}