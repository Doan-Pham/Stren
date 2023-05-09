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
     * Need to initialize this field before init{} block, or the init{} block will access
     * it when it's null and causes NullPointerException
     */
    private val _currentUserId: MutableStateFlow<String> =
        MutableStateFlow(UNDEFINED_USER_ID)

    init {
        authenticationService.addAuthStateListeners(
            onUserAuthenticated = {
                _currentUserId.value = it
                Timber.d("authStateListen - User signed in - userId: $it")
            },
            onUserNotAuthenticated = {
                _currentUserId.value = UNDEFINED_USER_ID
                Timber.d("authStateListen - User signed out")
            })
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<TrainingHistoryUiState> =
        _currentUserId.flatMapLatest { userId ->
            if (userId != UNDEFINED_USER_ID)
                workoutsRepository.getWorkoutsByUserIdAndDate(userId, _currentDate.value).map {
                    TrainingHistoryUiState.LoadComplete(it, _currentDate.value)
                }
            else flowOf(TrainingHistoryUiState.Loading)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000), TrainingHistoryUiState.Loading
        )

    private val _currentDate: MutableStateFlow<LocalDate> =
        MutableStateFlow(
            DateUtils.getCurrentDate()
        )
}