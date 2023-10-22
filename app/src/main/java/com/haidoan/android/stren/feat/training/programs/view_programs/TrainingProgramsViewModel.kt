package com.haidoan.android.stren.feat.training.programs.view_programs

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haidoan.android.stren.core.designsystem.component.ConfirmationDialogState
import com.haidoan.android.stren.core.model.TrainingProgram
import com.haidoan.android.stren.core.repository.impl.TrainingProgramsRepositoryImpl
import com.haidoan.android.stren.core.service.AuthenticationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


const val UNDEFINED_USER_ID = "Undefined User ID"

@HiltViewModel
internal class TrainingProgramsViewModel @Inject constructor(
    authenticationService: AuthenticationService,
    private val trainingProgramsRepository: TrainingProgramsRepositoryImpl,
) : ViewModel() {

    /**
     * Need to initialize userId before init{} block, or the init{} block will access
     * it when it's null and causes NullPointerException
     */
    private val _dataFetchingTriggers: MutableStateFlow<DataFetchingTriggers> = MutableStateFlow(
        DataFetchingTriggers(userId = UNDEFINED_USER_ID, searchQuery = "", arbitraryTrigger = false)
    )

    // Since TrainingProgramsViewModel will fetch all the trainingPrograms right from the start, fetching it again
    // when searchQuery changes is wasteful. Instead, cache the fetching result and query directly
    // on that cached list
    var cachedUserId = ""
        private set
    private val cachedTrainingPrograms = mutableListOf<TrainingProgram>()

    // Text to show on search bar
    var searchBarText = mutableStateOf("")

    private val _secondaryUiState = MutableStateFlow(TrainingProgramsSecondaryUiState())
    val secondaryUiState: StateFlow<TrainingProgramsSecondaryUiState> = _secondaryUiState

    init {
        authenticationService.addAuthStateListeners(
            onUserAuthenticated = { userId ->
                _dataFetchingTriggers.value = _dataFetchingTriggers.value.copy(userId = userId)
                viewModelScope.launch {
                    trainingProgramsRepository.getTrainingProgramsStreamByUserId(userId).collect {
                        cachedTrainingPrograms.clear()
                        cachedTrainingPrograms.addAll(it)
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
    val uiState: StateFlow<TrainingProgramsUiState> =
        _dataFetchingTriggers.flatMapLatest { triggers ->
            val userId = triggers.userId
            val searchQuery = triggers.searchQuery

            if (userId != cachedUserId) {
                cachedUserId = userId
                if (userId != UNDEFINED_USER_ID) {
                    trainingProgramsRepository.getTrainingProgramsStreamByUserId(userId).map {
                        cachedTrainingPrograms.clear()
                        cachedTrainingPrograms.addAll(it)

                        val result = it.filter { trainingProgram ->
                            trainingProgram.name.contains(
                                searchQuery, ignoreCase = true
                            )
                        }

                        if (result.isEmpty()) TrainingProgramsUiState.LoadEmpty
                        else TrainingProgramsUiState.LoadComplete(result)
                    }
                } else {
                    flowOf(TrainingProgramsUiState.Loading)
                }
            } else {
                if (userId != UNDEFINED_USER_ID) {
                    val result = cachedTrainingPrograms.filter { trainingProgram ->
                        trainingProgram.name.contains(
                            searchQuery, ignoreCase = true
                        )
                    }
                    if (result.isEmpty()) flowOf(TrainingProgramsUiState.LoadEmpty)
                    else flowOf(TrainingProgramsUiState.LoadComplete(result))
                } else {
                    flowOf(TrainingProgramsUiState.Loading)
                }
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            TrainingProgramsUiState.Loading
        )

    fun searchTrainingProgramByName(name: String) {
        _dataFetchingTriggers.value = _dataFetchingTriggers.value.copy(searchQuery = name)
    }

    fun triggerCollection() {
        _dataFetchingTriggers.getAndUpdate { it.copy(arbitraryTrigger = !it.arbitraryTrigger) }
    }

    fun deleteTrainingProgram(trainingProgramId: String) {
        _secondaryUiState.update { currentState ->
            currentState.copy(
                shouldShowConfirmDialog = true,
                confirmDialogState = ConfirmationDialogState(
                    title = "Delete trainingProgram",
                    body = "Are you sure you want to delete this trainingProgram? This action can't be undone ",
                    onDismissDialog = {
                        _secondaryUiState.update { it.copy(shouldShowConfirmDialog = false) }
                    },
                    onConfirmClick = {
                        cachedTrainingPrograms.removeIf { it.id == trainingProgramId }
                        viewModelScope.launch {
                            trainingProgramsRepository.deleteTrainingProgram(
                                trainingProgramId = trainingProgramId
                            )

                            _dataFetchingTriggers.getAndUpdate { it.copy(arbitraryTrigger = !it.arbitraryTrigger) }
                        }
                    })
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
private data class DataFetchingTriggers(
    val userId: String,
    val searchQuery: String,
    // For some reason, when user deletes a program, new list value is emitted -> use this to manually refresh
    val arbitraryTrigger: Boolean,
)