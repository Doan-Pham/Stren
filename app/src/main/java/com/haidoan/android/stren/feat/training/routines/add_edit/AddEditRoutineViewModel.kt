package com.haidoan.android.stren.feat.training.routines.add_edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haidoan.android.stren.core.model.*
import com.haidoan.android.stren.core.repository.base.ExercisesRepository
import com.haidoan.android.stren.core.repository.base.RoutinesRepository
import com.haidoan.android.stren.core.service.AuthenticationService
import com.haidoan.android.stren.feat.training.routines.AddEditRoutineArgs
import com.haidoan.android.stren.feat.training.routines.NavigationPurpose
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
    private val authenticationService: AuthenticationService,
    private val routinesRepository: RoutinesRepository,
    private val exercisesRepository: ExercisesRepository,
) : ViewModel() {
    var routineNameTextFieldValue by mutableStateOf("New routine")
    private val navArgs: AddEditRoutineArgs = AddEditRoutineArgs(savedStateHandle)
    private val _trainedExercises: MutableStateFlow<List<TrainedExercise>> =
        MutableStateFlow(listOf())

    private val _addRoutineToProgramEvent = MutableStateFlow<Pair<Int, Routine>?>(null)
    val addRoutineToProgramEvent = _addRoutineToProgramEvent.asStateFlow()

    private val _needToGetProgramRoutine = MutableStateFlow<String?>(null)
    val needToGetProgramRoutine = _needToGetProgramRoutine.asStateFlow()

    private val _editRoutineOfProgramEvent = MutableStateFlow<Routine?>(null)
    val editRoutineOfProgramEvent = _editRoutineOfProgramEvent.asStateFlow()

    private val _navigateBackEvent = MutableStateFlow<Unit?>(null)
    val navigateBackEvent = _navigateBackEvent.asStateFlow()


    init {
        if (navArgs.navigationPurpose == NavigationPurpose.EDIT_ROUTINE) {
            viewModelScope.launch {
                val userId = authenticationService.getCurrentUserId()
                val routine = routinesRepository.getRoutineById(userId, navArgs.routineId)
                routineNameTextFieldValue = routine.name
                _trainedExercises.value = routine.trainedExercises

                Timber.d("routine: $routine")
                Timber.d(" _trainedExercises.value: ${_trainedExercises.value}")
            }
        } else if (navArgs.navigationPurpose == NavigationPurpose.EDIT_ROUTINE_OF_PROGRAM) {
            viewModelScope.launch {
                _needToGetProgramRoutine.emit(navArgs.routineId)
            }
        }
    }

    fun setProgramRoutineToEdit(routine: Routine) {
        routineNameTextFieldValue = routine.name
        _trainedExercises.value = routine.trainedExercises

        Timber.d("routine: ${routine.name}")
        Timber.d(" _trainedExercises.value.size: ${_trainedExercises.value.size}")
    }

    fun setExercisesIdsToAdd(ids: List<String>) {
        //Timber.d("setExercisesIdsToAdd - ids: $ids")
        viewModelScope.launch {

            // Since [exercisesRepository.getExercisesByIds] returns exercises in a different order from the original, this helps correct the exercises order
            val correctOrderOfExercise = ids.indexMap()
            val exercisesToAdd =
                exercisesRepository.getExercisesByIds(ids)
                    .sortedBy { correctOrderOfExercise[it.id] }
            Timber.d("exercisesToAdd: $exercisesToAdd")

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
        updatedTrainingSetData: TrainingMeasurementMetrics,
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

    fun addEditRoutine() {
        viewModelScope.launch {
            val userId = authenticationService.getCurrentUserId()

            Timber.d("addEditRoutine() userId:$userId routineId: ${navArgs.routineId}")

            when (navArgs.navigationPurpose) {
                NavigationPurpose.ADD_ROUTINE -> {
                    routinesRepository.addRoutine(
                        userId = userId,
                        routine = Routine(
                            name = routineNameTextFieldValue,
                            trainedExercises = _trainedExercises.value
                        )
                    )
                    _navigateBackEvent.update { Unit }
                }

                NavigationPurpose.EDIT_ROUTINE -> {
                    routinesRepository.updateRoutine(
                        userId = userId,
                        routine = Routine(
                            id = navArgs.routineId,
                            name = routineNameTextFieldValue,
                            trainedExercises = _trainedExercises.value
                        )
                    )
                    _navigateBackEvent.update { Unit }
                }

                NavigationPurpose.ADD_ROUTINE_TO_PROGRAM -> {
                    Timber.d("Add routine to program")
                    _addRoutineToProgramEvent.update {
                        Pair(
                            navArgs.dayOffset, Routine(
                                name = routineNameTextFieldValue,
                                trainedExercises = _trainedExercises.value
                            )
                        )
                    }
                }

                NavigationPurpose.EDIT_ROUTINE_OF_PROGRAM -> {
                    _editRoutineOfProgramEvent.update {
                        Routine(
                            id = navArgs.routineId,
                            name = routineNameTextFieldValue,
                            trainedExercises = _trainedExercises.value
                        )
                    }
                }
            }
        }
    }

    fun onAddRoutineToProgram() {
        _addRoutineToProgramEvent.update { null }
    }

    fun onEditRoutineOfProgram() {
        _editRoutineOfProgramEvent.update { null }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<AddEditRoutineUiState> =
        _trainedExercises.flatMapLatest { _trainedExercises ->
            if (_trainedExercises.isEmpty()) flowOf(AddEditRoutineUiState.EmptyRoutine) else
                flowOf(AddEditRoutineUiState.IsEditing(_trainedExercises))
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000), AddEditRoutineUiState.Loading
        )

}

/**
 * Returns a map
 */
private fun <T> Iterable<T>.indexMap(): Map<T, Int> {
    val map = mutableMapOf<T, Int>()
    this.forEachIndexed { i, v ->
        map[v] = i
    }
    return map
}

private fun <T> List<T>.replace(newValue: T, block: (T) -> Boolean): List<T> {
    return map {
        if (block(it)) newValue else it
    }
}