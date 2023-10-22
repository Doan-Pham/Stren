package com.haidoan.android.stren.feat.training

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haidoan.android.stren.core.model.Routine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TrainingViewModel @Inject constructor() : ViewModel() {

    private val _routinesIdsByDayOffset = mutableMapOf<Int, MutableSet<String>>()
    private val _routinesIdsByDayOffsetFlow: MutableStateFlow<Map<Int, Set<String>>> = MutableStateFlow(emptyMap())
    val routinesIdsByDayOffset = _routinesIdsByDayOffsetFlow.asStateFlow()

    private val _routines = mutableMapOf<String, Routine>()
    private val _routinesForTrainingProgramFlow: MutableStateFlow<List<Routine>> = MutableStateFlow(emptyList())

    val routinesForTrainingProgram =
        _routinesForTrainingProgramFlow.asStateFlow()

    fun addRoutineToProgram(dayOffset: Int, routine: Routine) {
        viewModelScope.launch {
            val routineId = if (routine.id in _routines.keys) {
                UUID.randomUUID().toString()
            } else {
                routine.id
            }
            _routines[routineId] = routine
            _routinesForTrainingProgramFlow.update { _routines.values.toList() }

            _routinesIdsByDayOffset[dayOffset].apply {
                val newRoutinesIds = (this ?: mutableSetOf())
                newRoutinesIds.add(routineId)
                _routinesIdsByDayOffset[dayOffset] = newRoutinesIds

                _routinesIdsByDayOffsetFlow.update {
                    _routinesIdsByDayOffset.mapValues { it.value.toSet() }.toMap()
                }
            }
        }

        Timber.d("_routinesIdsByDayOffsetFlow: ${_routinesIdsByDayOffsetFlow.value}; _routinesForTrainingProgramFlow: ${_routinesForTrainingProgramFlow.value}")
    }


    fun clearRoutinesOfTrainingProgram() {
        _routinesIdsByDayOffset.clear()
        _routinesIdsByDayOffsetFlow.value = _routinesIdsByDayOffset.toMap()
        Timber.d("clearRoutinesOfTrainingProgram is called")
    }

    fun clearRoutinesIdsByDayOffset() {
        _routines.clear()
        _routinesForTrainingProgramFlow.value = _routines.values.toList()
        Timber.d("clearRoutinesIdsByDayOffset is called")
    }
}