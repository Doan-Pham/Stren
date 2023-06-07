package com.haidoan.android.stren.feat.training.exercises.create_cutom

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haidoan.android.stren.core.model.ExerciseCategory
import com.haidoan.android.stren.core.model.MuscleGroup
import com.haidoan.android.stren.core.repository.base.ExercisesRepository
import com.haidoan.android.stren.feat.training.exercises.CreateExerciseArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

internal const val UNDEFINED_EXERCISE_NAME_NAV_ARG = "UNDEFINED_EXERCISE_NAME_NAV_ARG"

@HiltViewModel
internal class CreateCustomExerciseViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    exercisesRepository: ExercisesRepository
) : ViewModel() {
    private val navArgs: CreateExerciseArgs = CreateExerciseArgs(savedStateHandle)

    private val _exerciseCategories =
        MutableStateFlow<List<ExerciseCategory>>(listOf())

    val exerciseCategories =
        _exerciseCategories.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf())

    private val _muscleGroups =
        MutableStateFlow<List<MuscleGroup>>(listOf())

    val muscleGroups =
        _muscleGroups.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf())

    fun togglePrimaryMuscleGroupSelection(muscleGroupId: String) {
        val selectedPrimaryMuscleGroupsIds = _uiState.value.primaryTrainedMusclesIds
        if (selectedPrimaryMuscleGroupsIds.contains(muscleGroupId)) {
            _uiState.value.primaryTrainedMusclesIds.remove(muscleGroupId)
        } else {
            _uiState.value.primaryTrainedMusclesIds.add(muscleGroupId)
        }
    }

    fun toggleSecondaryMuscleGroupSelection(muscleGroupId: String) {
        val selectedSecondaryMuscleGroupsIds = _uiState.value.secondaryTrainedMusclesIds
        if (selectedSecondaryMuscleGroupsIds.contains(muscleGroupId)) {
            _uiState.value.secondaryTrainedMusclesIds.remove(muscleGroupId)
        } else {
            _uiState.value.secondaryTrainedMusclesIds.add(muscleGroupId)
        }
    }

    fun modifyUiState(newUiState: CreateCustomExerciseUiState) {
        _uiState.value = newUiState
    }

    init {
        Timber.d("navArgs -exerciseName:  ${navArgs.exerciseName}")
        viewModelScope.launch {
            exercisesRepository.getAllExerciseCategories().collect {
                _exerciseCategories.value = it
            }
            exercisesRepository.getAllMuscleGroups().collect {
                _muscleGroups.value = it
            }
        }
    }

    private val _uiState = mutableStateOf(CreateCustomExerciseUiState())
    val uiState: State<CreateCustomExerciseUiState> = _uiState
}