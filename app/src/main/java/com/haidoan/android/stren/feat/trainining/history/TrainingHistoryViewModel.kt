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
import java.time.LocalDate
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
        DataFetchingTriggers(userId = UNDEFINED_USER_ID, currentDate = DateUtils.getCurrentDate())
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
            val currentDate = triggers.currentDate

            if (userId != UNDEFINED_USER_ID) {
                workoutsRepository.getWorkoutsByUserIdAndDate(userId, currentDate).map {
                    TrainingHistoryUiState.LoadComplete(it, currentDate)
                }
            } else {
                flowOf(TrainingHistoryUiState.Loading)
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000), TrainingHistoryUiState.Loading
        )

    fun setCurrentDate(date: LocalDate) {
        _dataFetchingTriggers.value = _dataFetchingTriggers.value.copy(currentDate = date)
    }
}

/**
 * Kotlin Flow's flatMapLatest() can collect a flow and flatMap it whenever it changes, but
 * it only works with 1 input flow.
 *
 * By wrapping inside this class all the different data objects that should triggers flatMapLatest()
 * when they change, developer can indirectly use flatMapLatest() with more than 1 input
 */
private data class DataFetchingTriggers(val userId: String, val currentDate: LocalDate)