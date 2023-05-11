package com.haidoan.android.stren.feat.trainining.routines.add_edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haidoan.android.stren.core.model.Routine
import com.haidoan.android.stren.core.repository.RoutinesRepository
import com.haidoan.android.stren.feat.trainining.routines.AddEditRoutineArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
internal class AddEditRoutineViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    routinesRepository: RoutinesRepository
) : ViewModel() {

    private val navArgs: AddEditRoutineArgs = AddEditRoutineArgs(savedStateHandle)

    private val _routines: List<Routine> = listOf()

    val uiState: StateFlow<AddEditRoutineUiState> = if (navArgs.isAddingRoutine) {
        if (_routines.isEmpty()) flowOf(AddEditRoutineUiState.EmptyRoutine)
        else flowOf(AddEditRoutineUiState.IsAdding(_routines))
    } else {
        flowOf(AddEditRoutineUiState.Loading)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000), AddEditRoutineUiState.Loading
    )

}
