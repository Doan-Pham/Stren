package com.haidoan.android.stren.feat.trainining.routines.add_edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haidoan.android.stren.core.model.TrainedExercise
import com.haidoan.android.stren.core.repository.RoutinesRepository
import com.haidoan.android.stren.feat.trainining.routines.AddEditRoutineArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

const val SELECTED_EXERCISES_IDS_SAVED_STATE_KEY = "selected_exercises_ids"

@HiltViewModel
internal class AddEditRoutineViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    routinesRepository: RoutinesRepository
) : ViewModel() {
    var routineNameTextFieldValue by mutableStateOf("New routine")
    private val navArgs: AddEditRoutineArgs = AddEditRoutineArgs(savedStateHandle)

    private var _exercisesIdsToAdd: MutableStateFlow<List<String>> = MutableStateFlow(listOf())

    fun setExercisesIdsToAdd(ids: List<String>) {
        Timber.d("setExercisesIdsToAdd - ids: $ids")

    }

    private val _trainedExercises: MutableList<TrainedExercise> = mutableListOf()


    val uiState: StateFlow<AddEditRoutineUiState> = if (navArgs.isAddingRoutine) {
        if (_trainedExercises.isEmpty()) flowOf(AddEditRoutineUiState.EmptyRoutine)
        else flowOf(AddEditRoutineUiState.IsAdding(_trainedExercises))
    } else {
        flowOf(AddEditRoutineUiState.Loading)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000), AddEditRoutineUiState.Loading
    )

}
