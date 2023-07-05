package com.haidoan.android.stren.feat.training.exercises.create_cutom

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haidoan.android.stren.core.model.Exercise
import com.haidoan.android.stren.core.model.ExerciseCategory
import com.haidoan.android.stren.core.model.MuscleGroup
import com.haidoan.android.stren.core.repository.base.ExercisesRepository
import com.haidoan.android.stren.core.service.AuthenticationService
import com.haidoan.android.stren.core.service.StorageService
import com.haidoan.android.stren.feat.training.exercises.CreateExerciseArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import javax.inject.Inject

internal const val UNDEFINED_EXERCISE_NAME_NAV_ARG = "UNDEFINED_EXERCISE_NAME_NAV_ARG"

@HiltViewModel
internal class CreateCustomExerciseViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    authenticationService: AuthenticationService,
    private val exercisesRepository: ExercisesRepository,
    private val storageService: StorageService
) : ViewModel() {
    private var userId: String = ""
    private val navArgs: CreateExerciseArgs = CreateExerciseArgs(savedStateHandle)
    var isCreateExerciseComplete by mutableStateOf(false)
    var isLoading by mutableStateOf(false)

    private val _exerciseCategories = MutableStateFlow<List<ExerciseCategory>>(listOf())
    val exerciseCategories = _exerciseCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf())

    private val _muscleGroups = MutableStateFlow<List<MuscleGroup>>(listOf())
    val muscleGroups = _muscleGroups
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf())

    private val _uiState = mutableStateOf(CreateCustomExerciseUiState())
    val uiState: State<CreateCustomExerciseUiState> = _uiState

    init {
        Timber.d("navArgs -exerciseName:  ${navArgs.exerciseName}")
        if (navArgs.exerciseName != UNDEFINED_EXERCISE_NAME_NAV_ARG) {
            _uiState.value = _uiState.value.copy(exerciseName = navArgs.exerciseName)
        }
        viewModelScope.launch {
            authenticationService.addAuthStateListeners(
                onUserAuthenticated = { userId = it },
                onUserNotAuthenticated = { userId = "" })
            exercisesRepository.getAllExerciseCategories().collect {
                _exerciseCategories.value = it
            }
            exercisesRepository.getAllMuscleGroups().collect {
                _muscleGroups.value = it
            }
        }
    }

    fun toggleMuscleGroupSelection(muscleGroupId: String) {
        val selectedPrimaryMuscleGroupsIds = _uiState.value.trainedMusclesIds
        if (selectedPrimaryMuscleGroupsIds.contains(muscleGroupId)) {
            _uiState.value.trainedMusclesIds.remove(muscleGroupId)
        } else {
            _uiState.value.trainedMusclesIds.add(muscleGroupId)
        }
    }

    fun modifyUiState(newUiState: CreateCustomExerciseUiState) {
        _uiState.value = newUiState
    }

    fun createCustomExercise() {
        isLoading = true
        viewModelScope.launch {
            supervisorScope {
                val tasks = mutableMapOf<String, Deferred<Unit>>()

                val startImage = uiState.value.startImage
                val endImage = uiState.value.endImage
                var startImageUrl = ""
                var endImageUrl = ""

                if (startImage != null) {
                    tasks["uploadStartImage"] = async {
                        startImageUrl =
                            storageService.uploadUserCustomExerciseImage(userId, startImage)
                                .toString()
                        Timber.d("startImageUrl: $startImageUrl")
                    }
                }
                if (endImage != null) {
                    tasks["uploadEndImage"] = async {
                        endImageUrl =
                            storageService.uploadUserCustomExerciseImage(userId, endImage)
                                .toString()
                        Timber.d("endImageUrl: $endImageUrl")
                    }
                }

                tasks.forEach {
                    try {
                        it.value.await()
                    } catch (e: Exception) {
                        Timber.e("createCustomExercise() - ${it.key} fails with exception: $e")
                    }
                }

                val exerciseToAdd = Exercise(
                    name = uiState.value.exerciseName,
                    imageUrls = listOf(startImageUrl, endImageUrl),
                    instructions = uiState.value.instruction.lines(),
                    belongedCategory = uiState.value.exerciseCategory.name,
                    trainedMuscleGroups =
                    muscleGroups.value
                        .filter { it.id in uiState.value.trainedMusclesIds }
                        .map { it.name },
                    userId = userId,
                    isCustomExercise = true
                )
                Timber.d("createCustomExercise() - exerciseToAdd: $exerciseToAdd")
                exercisesRepository.createCustomExercise(userId = userId, exerciseToAdd)

                // A small delay to allow the backend to sync database with
                // full-text search service
                delay(3000)
                isCreateExerciseComplete = true
            }
        }
    }
}