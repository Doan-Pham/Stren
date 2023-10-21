package com.haidoan.android.stren.feat.training.programs.add_edit

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haidoan.android.stren.core.model.Routine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
internal class AddEditTrainingProgramViewModel @Inject constructor() : ViewModel() {
    private var _programName = mutableStateOf("New program")
    val programName: State<String> = _programName

    private var _selectedDayOffset = MutableStateFlow(0)
    val selectedDayOffset: StateFlow<Int> = _selectedDayOffset.asStateFlow()

    private val routinesIdsByDayOffset = mutableMapOf<Int, Set<String>>(
        0 to setOf("persequeris"), 3 to setOf("persequeris"), 45 to setOf("persequeris")
    )

    val routinesOfSelectedDate = _selectedDayOffset.map { dayOffset ->
        routines.filter { routinesIdsByDayOffset[dayOffset]?.contains(it.id) ?: false }
    }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

    private val routines = mutableListOf<Routine>(
        Routine(
            id = "persequeris",
            name = "Pete Waller",
            trainedExercises = listOf()
        )
    )

    fun onProgramNameChange(value: String) {
        _programName.value = value
    }

    fun selectDate(dayOffset: Int) {
        _selectedDayOffset.update { dayOffset }
    }
}