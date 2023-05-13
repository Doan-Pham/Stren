package com.haidoan.android.stren.feat.trainining.routines.add_edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haidoan.android.stren.core.model.TrainedExercise
import com.haidoan.android.stren.core.model.TrainingMeasurementMetrics
import com.haidoan.android.stren.core.model.asTrainedExerciseWithOneSet
import com.haidoan.android.stren.core.repository.ExercisesRepository
import com.haidoan.android.stren.core.repository.RoutinesRepository
import com.haidoan.android.stren.feat.trainining.routines.AddEditRoutineArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

const val SELECTED_EXERCISES_IDS_SAVED_STATE_KEY = "selected_exercises_ids"

@HiltViewModel
internal class AddEditRoutineViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    routinesRepository: RoutinesRepository,
    private val exercisesRepository: ExercisesRepository
) : ViewModel() {
    var routineNameTextFieldValue by mutableStateOf("New routine")
    private val navArgs: AddEditRoutineArgs = AddEditRoutineArgs(savedStateHandle)

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

    private val _trainedExercises: MutableStateFlow<List<TrainedExercise>> =
        MutableStateFlow(listOf())

    fun updateExerciseTrainingSet(
        exerciseToUpdate: TrainedExercise,
        trainingSetToUpdate: TrainingMeasurementMetrics,
        updatedTrainingSetData: TrainingMeasurementMetrics
    ) {
        Timber.d("exerciseToUpdate - unique identifier: ${System.identityHashCode(exerciseToUpdate)}; content: $exerciseToUpdate")
        Timber.d(
            "trainingSetToUpdate - unique identifier: ${
                System.identityHashCode(
                    trainingSetToUpdate
                )
            }; content: $trainingSetToUpdate"
        )
        Timber.d(
            "updatedTrainingSetData - unique identifier: ${
                System.identityHashCode(
                    updatedTrainingSetData
                )
            }; content: $updatedTrainingSetData"
        )

        val updatedTrainedExercises = mutableListOf<TrainedExercise>()

        val updatedTrainingSets = _trainedExercises.value.first { trainedExercise ->
            Timber.d("Finding exercise - id: ${System.identityHashCode(trainedExercise)}")
            trainedExercise === exerciseToUpdate
        }.trainingSets.replace(updatedTrainingSetData) { trainingSet ->
            Timber.d("Replacing training set - id: ${System.identityHashCode(trainingSet)}")
            trainingSet === trainingSetToUpdate
        }
        Timber.d("updatedTrainingSets: $updatedTrainingSets")

        val updatedExercise = _trainedExercises.value.first {
            Timber.d("Finding exercise - id: ${System.identityHashCode(it)}")
            it === exerciseToUpdate
        }.copy(trainingSets = updatedTrainingSets)
        Timber.d("updatedExercise: $updatedExercise")

        updatedTrainedExercises.addAll(_trainedExercises.value.replace(updatedExercise) { it === exerciseToUpdate })
        Timber.d("updatedTrainedExercises: $updatedTrainedExercises")

        _trainedExercises.value = updatedTrainedExercises
        Timber.d("_trainedExercises: ${_trainedExercises.value}")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<AddEditRoutineUiState> =
        _trainedExercises.flatMapLatest { _trainedExercises ->
            if (navArgs.isAddingRoutine) {
                if (_trainedExercises.isEmpty()) flowOf(AddEditRoutineUiState.EmptyRoutine)
                else flowOf(AddEditRoutineUiState.IsAdding(_trainedExercises))
            } else {
                flowOf(AddEditRoutineUiState.Loading)
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000), AddEditRoutineUiState.Loading
        )

}

private fun <T> List<T>.replace(newValue: T, block: (T) -> Boolean): List<T> {
    return map {
        if (block(it)) newValue else it
    }
}