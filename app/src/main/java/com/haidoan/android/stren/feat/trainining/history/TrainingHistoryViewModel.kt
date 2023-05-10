package com.haidoan.android.stren.feat.trainining.history


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haidoan.android.stren.core.repository.WorkoutsRepository
import com.haidoan.android.stren.core.service.AuthenticationService
import com.haidoan.android.stren.core.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import timber.log.Timber
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.util.*
import javax.inject.Inject

const val UNDEFINED_USER_ID = "Undefined User ID"

@HiltViewModel
internal class TrainingHistoryViewModel @Inject constructor(
    authenticationService: AuthenticationService,
    private val workoutsRepository: WorkoutsRepository
) : ViewModel() {

    /**
     * Need to initialize userId before init{} block, or the init{} block will access
     * it when it's null and causes NullPointerException
     */
    private val _dataFetchingTriggers: MutableStateFlow<DataFetchingTriggers> = MutableStateFlow(
        DataFetchingTriggers(userId = UNDEFINED_USER_ID, selectedDate = DateUtils.getCurrentDate())
    )

    init {
        authenticationService.addAuthStateListeners(
            onUserAuthenticated = {
                _dataFetchingTriggers.value = _dataFetchingTriggers.value.copy(userId = it)
                Timber.d("authStateListen - User signed in - userId: $it")
            },
            onUserNotAuthenticated = {
                _dataFetchingTriggers.value =
                    _dataFetchingTriggers.value.copy(userId = UNDEFINED_USER_ID)
                Timber.d("authStateListen - User signed out")
            })
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<TrainingHistoryUiState> =
        _dataFetchingTriggers.flatMapLatest { triggers ->
            val userId = triggers.userId
            val selectedDate = triggers.selectedDate

            if (userId != UNDEFINED_USER_ID) {
                combine(
                    workoutsRepository.getWorkoutsByUserIdAndDate(userId, selectedDate),
                    workoutsRepository.getDatesThatHaveWorkoutByUserId(userId)
                ) { workouts, datesThatHaveWorkouts ->
                    TrainingHistoryUiState.LoadComplete(
                        workouts,
                        selectedDate,
                        datesThatHaveWorkouts
                    )
                }
            } else {
                flowOf(TrainingHistoryUiState.Loading)
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000), TrainingHistoryUiState.Loading
        )

    fun selectDate(date: LocalDate) {
        _dataFetchingTriggers.value = _dataFetchingTriggers.value.copy(selectedDate = date)
    }

    fun setCurrentDateToDefault() {
        selectDate(DateUtils.getCurrentDate())
    }

    fun moveToNextWeek() {
        val selectedDate = _dataFetchingTriggers.value.selectedDate
        selectDate(selectedDate.with(TemporalAdjusters.next(DayOfWeek.MONDAY)))
    }

    fun moveToPreviousWeek() {
        val selectedDate = _dataFetchingTriggers.value.selectedDate
        selectDate(selectedDate.with(TemporalAdjusters.previous(DayOfWeek.MONDAY)))
    }
}

/**
 * Kotlin Flow's flatMapLatest() can collect a flow and flatMap it whenever it changes, but
 * it only works with 1 input flow.
 *
 * By wrapping inside this class all the different data objects that should triggers flatMapLatest()
 * when they change, developer can indirectly use flatMapLatest() with more than 1 input
 */
private data class DataFetchingTriggers(val userId: String, val selectedDate: LocalDate)