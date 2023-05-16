package com.haidoan.android.stren.feat.trainining.routines

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haidoan.android.stren.core.model.Routine
import com.haidoan.android.stren.core.repository.RoutinesRepository
import com.haidoan.android.stren.core.service.AuthenticationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
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
        DataFetchingTriggers(userId = UNDEFINED_USER_ID, searchQuery = "")
    )

    // Since RoutinesViewModel will fetch all the routines right from the start, fetching it again
    // when searchQuery changes is wasteful. Instead, cache the fetching result and query directly
    // on that cached list
    var cachedUserId = ""
        private set
    private var cachedRoutines = listOf<Routine>()

    // Text to show on search bar
    var searchBarText = mutableStateOf("")

    init {
        authenticationService.addAuthStateListeners(
            onUserAuthenticated = { userId ->
                _dataFetchingTriggers.value = _dataFetchingTriggers.value.copy(userId = userId)
                viewModelScope.launch {
                    routinesRepository.getRoutinesStreamByUserId(userId).collect {
                        cachedRoutines = it
                    }
                }
                Timber.d("authStateListen - User signed in - userId: $userId")
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
            val searchQuery = triggers.searchQuery

            if (userId != cachedUserId) {
                cachedUserId = userId
                if (userId != UNDEFINED_USER_ID) {
                    routinesRepository.getRoutinesStreamByUserId(userId).map {
                        cachedRoutines = it
                        val result = it.filter { routine ->
                            routine.name.contains(
                                searchQuery, ignoreCase = true
                            )
                        }
                        if (result.isEmpty()) RoutinesUiState.LoadEmpty
                        else RoutinesUiState.LoadComplete(result)
                    }
                } else {
                    flowOf(RoutinesUiState.Loading)
                }
            } else {
                if (userId != UNDEFINED_USER_ID) {
                    val result = cachedRoutines.filter { routine ->
                        routine.name.contains(
                            searchQuery, ignoreCase = true
                        )
                    }
                    if (result.isEmpty()) flowOf(RoutinesUiState.LoadEmpty)
                    else flowOf(RoutinesUiState.LoadComplete(result))
                } else {
                    flowOf(RoutinesUiState.Loading)
                }
            }

        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000), RoutinesUiState.Loading
        )

    fun searchRoutineByName(name: String) {
        _dataFetchingTriggers.value = _dataFetchingTriggers.value.copy(searchQuery = name)
    }

    /**
     * Kotlin Flow's flatMapLatest() can collect a flow and flatMap it whenever it changes, but
     * it only works with 1 input flow.
     *
     * By wrapping inside this class all the different data objects that should triggers flatMapLatest()
     * when they change, developer can indirectly use flatMapLatest() with more than 1 input
     */
    private data class DataFetchingTriggers(val userId: String, val searchQuery: String)
}