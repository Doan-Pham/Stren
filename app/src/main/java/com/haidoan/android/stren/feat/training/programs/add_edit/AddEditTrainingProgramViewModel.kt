package com.haidoan.android.stren.feat.training.programs.add_edit

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haidoan.android.stren.core.model.Routine
import com.haidoan.android.stren.core.model.TrainingProgram
import com.haidoan.android.stren.core.repository.base.RoutinesRepository
import com.haidoan.android.stren.core.repository.impl.TrainingProgramsRepositoryImpl
import com.haidoan.android.stren.core.service.AuthenticationService
import com.haidoan.android.stren.core.utils.DateUtils.getCurrentDate
import com.haidoan.android.stren.feat.training.programs.navigation.AddEditTrainingProgramArgs
import com.haidoan.android.stren.feat.training.programs.navigation.DEFAULT_PROGRAM_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

private const val DEFAULT_SELECTED_DAY_OFFSET = 0
internal const val DEFAULT_NUM_OF_DAYS_PER_WEEK = 7
internal const val DEFAULT_NUM_OF_WEEKS_PER_GROUP = 4
internal const val MIN_NUM_OF_WEEKS = 1
internal const val MAX_NUM_OF_WEEKS = 12

@HiltViewModel
internal class AddEditTrainingProgramViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val authenticationService: AuthenticationService,
    private val routinesRepository: RoutinesRepository,
    private val trainingProgramsRepositoryImpl: TrainingProgramsRepositoryImpl,
) : ViewModel() {
    private val navArgs = AddEditTrainingProgramArgs(savedStateHandle)
    val isEditing = navArgs.programId != DEFAULT_PROGRAM_ID
    private lateinit var cachedProgram: TrainingProgram

    private val disposables = mutableListOf<() -> Unit>()

    private val _programTotalNumOfWeeks = MutableStateFlow(MIN_NUM_OF_WEEKS)
    val programTotalNumOfWeeks = _programTotalNumOfWeeks.asStateFlow()

    private val _programStartDate = MutableStateFlow(getCurrentDate())
    val programStartDate = _programStartDate.asStateFlow()

    private var _programName = mutableStateOf("New program")
    val programName: State<String> = _programName

    private var _selectedDayOffset = MutableStateFlow(DEFAULT_SELECTED_DAY_OFFSET)
    val selectedDayOffset: StateFlow<Int> = _selectedDayOffset.asStateFlow()

    private var _routinesIdsByDayOffset = MutableStateFlow<Map<Int, Set<String>>>(emptyMap())
    val routinesIdsByDayOffset = MutableSharedFlow<Map<Int, Set<String>>>()

    private val _routines = MutableStateFlow<List<Routine>>(emptyList())
    val routines = _routines.asStateFlow()

    val dayOffsetsWithWorkouts =
        _routinesIdsByDayOffset.map { routinesIds -> routinesIds.filter { it.value.isNotEmpty() }.keys.toSet() }


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
        viewModelScope.launch {
            val userId = authenticationService.getCurrentUserId()

            routinesRepository.getRoutinesStreamByUserId(userId).collect { routines ->
                Timber.d("routines: $routines")
                _routines.update { routines }
            }
        }

        Timber.d("AddEditTrainingProgram - programId: ${navArgs.programId}")
        if (isEditing) {
            viewModelScope.launch {
                cachedProgram = trainingProgramsRepositoryImpl.getTrainingProgramById(
                    userId = navArgs.userId,
                    navArgs.programId
                )

                cachedProgram.apply {
                    _programName.value = name
                    _programStartDate.value = startDate
                    _routinesIdsByDayOffset.value =
                        routinesByDayOffset.mapValues { (_, value) -> value.map { it.extraId }.toSet() }
                    routinesIdsByDayOffset.emit(_routinesIdsByDayOffset.value)
                    _programTotalNumOfWeeks.value = totalNumOfDay / DEFAULT_NUM_OF_DAYS_PER_WEEK
                }
            }
        }
    }

    fun onProgramNameChange(value: String) {
        _programName.value = value
    }

    fun onProgramTotalNumOfWeeksChange(value: Int) {
        _programTotalNumOfWeeks.value = value
    }

    fun onProgramStartDateChange(value: LocalDate) {
        _programStartDate.value = value
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

    fun addEditTrainingProgram() {
        viewModelScope.launch {
            val totalNumOfDays = _programTotalNumOfWeeks.value * DEFAULT_NUM_OF_DAYS_PER_WEEK
            Timber.d("__routines.value: ${_routines.value}")
            val routinesByDayOffset =
                _routinesIdsByDayOffset.value
                    .filter { it.value.isNotEmpty() }
                    .mapValues { routinesIdsByDayOffset ->
                        routinesIdsByDayOffset.value.mapNotNull { routineId ->
                            _routines.value.firstOrNull { it.id == routineId }
                        }
                    }

            if (isEditing) {
                trainingProgramsRepositoryImpl.updateTrainingProgram(
                    userId = authenticationService.getCurrentUserId(),
                    trainingProgram = cachedProgram.copy(
                        name = _programName.value,
                        totalNumOfDay = totalNumOfDays,
                        startDate = _programStartDate.value,
                        endDate = _programStartDate.value.plusDays(totalNumOfDays.toLong()),
                        numOfDaysPerWeek = DEFAULT_NUM_OF_DAYS_PER_WEEK,
                        routinesByDayOffset = routinesByDayOffset
                    )
                )
            } else {
                trainingProgramsRepositoryImpl.addTrainingProgram(
                    userId = authenticationService.getCurrentUserId(),
                    trainingProgram = TrainingProgram(
                        name = _programName.value,
                        totalNumOfDay = totalNumOfDays,
                        startDate = _programStartDate.value,
                        endDate = _programStartDate.value.plusDays(totalNumOfDays.toLong()),
                        numOfDaysPerWeek = DEFAULT_NUM_OF_DAYS_PER_WEEK,
                        routinesByDayOffset = routinesByDayOffset
                    )
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposables.forEach { it() }
        disposables.clear()
    }

}