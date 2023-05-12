package com.haidoan.android.stren.feat.trainining.routines.add_edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haidoan.android.stren.core.model.TrainedExercise
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
