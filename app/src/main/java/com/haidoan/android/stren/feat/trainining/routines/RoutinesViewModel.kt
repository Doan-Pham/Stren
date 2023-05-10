package com.haidoan.android.stren.feat.trainining.routines

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haidoan.android.stren.core.repository.RoutinesRepository
import com.haidoan.android.stren.core.service.AuthenticationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

const val UNDEFINED_USER_ID = "Undefined User ID"

@HiltViewModel
internal class RoutinesViewModel @Inject constructor(
    authenticationService: AuthenticationService,
    routinesRepository: RoutinesRepository
) : ViewModel() {

    /**
     * Need to initialize userId before init{} block, or the init{} block will access
     * it when it's null and causes NullPointerException
     */
    private val _dataFetchingTriggers: MutableStateFlow<DataFetchingTriggers> = MutableStateFlow(
        DataFetchingTriggers(userId = UNDEFINED_USER_ID)
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
    val uiState: StateFlow<RoutinesUiState> =
        _dataFetchingTriggers.flatMapLatest { triggers ->
            val userId = triggers.userId
            if (userId != UNDEFINED_USER_ID) {
                routinesRepository.getRoutinesByUserId(userId).map {
                    if (it.isEmpty()) RoutinesUiState.LoadEmpty
                    else RoutinesUiState.LoadComplete(it)
                }
            } else {
                flowOf(RoutinesUiState.Loading)
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000), RoutinesUiState.Loading
        )

    /**
     * Kotlin Flow's flatMapLatest() can collect a flow and flatMap it whenever it changes, but
     * it only works with 1 input flow.
     *
     * By wrapping inside this class all the different data objects that should triggers flatMapLatest()
     * when they change, developer can indirectly use flatMapLatest() with more than 1 input
     */
    private data class DataFetchingTriggers(val userId: String)
}