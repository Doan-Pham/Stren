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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

private const val DEFAULT_SELECTED_DAY_OFFSET = 0
internal const val DEFAULT_NUM_OF_DAYS_PER_WEEK = 7
internal const val DEFAULT_NUM_OF_WEEKS_PER_GROUP = 4
internal const val MIN_NUM_OF_WEEKS = 1
internal const val MAX_NUM_OF_WEEKS = 12

@HiltViewModel
internal class AddEditTrainingProgramViewModel @Inject constructor() : ViewModel() {

    private val disposables = mutableListOf<() -> Unit>()

    private val _programTotalNumOfWeeks = MutableStateFlow(MIN_NUM_OF_WEEKS)
    val programTotalNumOfWeeks = _programTotalNumOfWeeks.asStateFlow()

    private var _programName = mutableStateOf("New program")
    val programName: State<String> = _programName

    private var _selectedDayOffset = MutableStateFlow(DEFAULT_SELECTED_DAY_OFFSET)
    val selectedDayOffset: StateFlow<Int> = _selectedDayOffset.asStateFlow()

    private var _routinesIdsByDayOffset = MutableStateFlow<Map<Int, Set<String>>>(emptyMap())
    private val _routines = MutableStateFlow<List<Routine>>(emptyList())

    val routinesOfSelectedDate = combine(
        _selectedDayOffset,
        _routinesIdsByDayOffset,
        _routines,
    ) { dayOffset, routinesIdsByDayOffset, routines ->
        Timber.d("dayOffset: $dayOffset, routinesIdsByDayOffset: $routinesIdsByDayOffset, routines: $routines")

        val routinesId = routinesIdsByDayOffset[dayOffset]
        routines.filter { routinesId?.contains(it.id) ?: false }

    }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

    init {
        viewModelScope.launch {
            _programTotalNumOfWeeks.collect {
                _selectedDayOffset.value = DEFAULT_SELECTED_DAY_OFFSET
            }
        }
    }

    fun onProgramNameChange(value: String) {
        _programName.value = value
    }

    fun onProgramTotalNumOfWeeksChange(value: Int) {
        _programTotalNumOfWeeks.value = value
    }

    fun selectDate(dayOffset: Int) {
        _selectedDayOffset.update { dayOffset }
    }

    fun updateRoutinesIdsByDayOffset(routinesIdsByDayOffset: Map<Int, Set<String>>) {
        _routinesIdsByDayOffset.update { routinesIdsByDayOffset }
    }

    fun updateRoutines(routines: List<Routine>) {
        _routines.update { routines }
    }

    fun addDisposable(disposable: () -> Unit) {
        disposables.add(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        disposables.forEach { it() }
        disposables.clear()
    }
}