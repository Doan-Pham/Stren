package com.haidoan.android.stren.feat.training.history


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haidoan.android.stren.core.designsystem.component.ConfirmationDialogState
import com.haidoan.android.stren.core.designsystem.component.SingleSelectionDialogState
import com.haidoan.android.stren.core.repository.base.WorkoutsRepository
import com.haidoan.android.stren.core.service.AuthenticationService
import com.haidoan.android.stren.core.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
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
    private val _secondaryUiState = MutableStateFlow(TrainingHistorySecondaryUiState())
    val secondaryUiState: StateFlow<TrainingHistorySecondaryUiState> = _secondaryUiState

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<TrainingHistoryUiState> =
        _dataFetchingTriggers.flatMapLatest { triggers ->
            val userId = triggers.userId
            val selectedDate = triggers.selectedDate

            if (userId != UNDEFINED_USER_ID) {
                combine(
                    workoutsRepository.getWorkoutsStreamByUserIdAndDate(userId, selectedDate),
                    workoutsRepository.getDatesThatHaveWorkoutByUserId(userId)
                ) { workouts, datesThatHaveWorkouts ->
                    TrainingHistoryUiState.LoadComplete(
                        userId,
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

    fun deleteWorkout(workoutId: String) {
        _secondaryUiState.update { currentState ->
            currentState.copy(
                shouldShowConfirmDialog = true,
                confirmDialogState = ConfirmationDialogState(
                    title = "Delete workout",
                    body = "Are you sure you want to delete this workout? This action can't be undone ",
                    onDismissDialog = {
                        _secondaryUiState.update { it.copy(shouldShowConfirmDialog = false) }
                    },
                    onConfirmClick = {
                        viewModelScope.launch { workoutsRepository.deleteWorkout(workoutId) }
                    }
                )
            )
        }
    }

    fun showWorkoutOptions(options: List<Pair<String, () -> Unit>>) {
        _secondaryUiState.update { currentState ->
            currentState.copy(
                shouldShowSingleSelectionDialog = true,
                workoutOptionDialogState = SingleSelectionDialogState(
                    title = "Workout option",
                    onDismissDialog = {
                        _secondaryUiState.update { it.copy(shouldShowSingleSelectionDialog = false) }
                    },
                    options = options.map { it.first },
                    onConfirmClick = {
                        options[it].second()
                    }
                )
            )
        }
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