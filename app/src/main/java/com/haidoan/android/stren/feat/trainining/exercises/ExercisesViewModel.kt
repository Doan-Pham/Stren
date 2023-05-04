package com.haidoan.android.stren.feat.trainining.exercises

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.haidoan.android.stren.core.model.ExerciseCategory
import com.haidoan.android.stren.core.model.ExerciseFilterStandards
import com.haidoan.android.stren.core.model.MuscleGroup
import com.haidoan.android.stren.core.repository.ExercisesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

private const val TAG = "ExercisesViewModel"

@HiltViewModel
internal class ExercisesViewModel @Inject constructor(exercisesRepository: ExercisesRepository) :
    ViewModel() {
    var searchBarText = mutableStateOf("")

    private val _selectedCategoriesIds: MutableStateFlow<List<String>> =
        MutableStateFlow(listOf())

    @OptIn(ExperimentalCoroutinesApi::class)
    val exerciseCategories = _selectedCategoriesIds.flatMapLatest { ids ->
        Log.d(TAG, "chosenCategoriesIds-map()- ids: $ids")
        exercisesRepository.getAllExerciseCategories()
            .map { categories ->
                categories.map {
                    ExerciseCategorySelectionState(
                        it,
                        ids.contains(it.id)
                    )
                }
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf())

    fun toggleCategorySelection(categoryId: String) {
        Log.d(TAG, "toggleCategorySelection() - categoryId(): $categoryId")

        // MutableStateFlow works by comparing old and new value with equals() and emit
        // based on that. So MUTATING the object wrapped in StateFlow DOESN'T UPDATE
        // STATEFLOW since it's still the SAME OBJECT.
        // Need to create NEW object and assign it
        val newValue = mutableListOf<String>()
        newValue.addAll(_selectedCategoriesIds.value)

        if (_selectedCategoriesIds.value.contains(categoryId)) {
            newValue.remove(categoryId)
        } else {
            newValue.add(categoryId)
        }
        _selectedCategoriesIds.value = newValue.toList()
    }


    private val _selectedMuscleGroupsIds: MutableStateFlow<List<String>> =
        MutableStateFlow(listOf())

    @OptIn(ExperimentalCoroutinesApi::class)
    val muscleGroups = _selectedMuscleGroupsIds.flatMapLatest { ids ->
        Log.d(TAG, "chosenCategoriesIds-map()- ids: $ids")
        exercisesRepository.getAllMuscleGroups()
            .map { categories ->
                categories.map {
                    MuscleGroupSelectionState(
                        it,
                        ids.contains(it.id)
                    )
                }
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf())

    fun toggleMuscleGroupSelection(muscleGroupId: String) {
        Log.d(TAG, "toggleMuscleGroupSelection() - muscleGroupId(): $muscleGroupId")
        val newValue = mutableListOf<String>()
        newValue.addAll(_selectedMuscleGroupsIds.value)

        if (_selectedMuscleGroupsIds.value.contains(muscleGroupId)) {
            newValue.remove(muscleGroupId)
        } else {
            newValue.add(muscleGroupId)
        }
        _selectedMuscleGroupsIds.value = newValue.toList()
    }

    private val _exercisesFilterStandards =
        MutableStateFlow(ExerciseFilterStandards("", listOf(), listOf()))

    private val _exerciseNameToQuery = MutableStateFlow("")
    fun searchExerciseByName(exerciseName: String) {
//        Log.d(TAG, "searchExerciseByName() - [Param]exerciseName: $exerciseName")
        _exercisesFilterStandards.value =
            _exercisesFilterStandards.value.copy(exerciseName = exerciseName)

    }

    fun resetFilters() {
        viewModelScope.launch {
            _selectedCategoriesIds.value = listOf()
            _selectedMuscleGroupsIds.value = listOf()
            applyFilters()
        }
    }

    fun applyFilters() {
        // If no filter is selected, application should show all exercises,
        // as if ALL filters are selected
        val exerciseCategoriesToFilterBy =
            if (exerciseCategories.value.any { it.isSelected }) {
                exerciseCategories.value.filter { it.isSelected }
            } else {
                exerciseCategories.value
            }.map { it.category }

        val muscleGroupsToFilterBy =
            if (muscleGroups.value.any { it.isSelected }) {
                muscleGroups.value.filter { it.isSelected }
            } else {
                muscleGroups.value
            }.map { it.muscleGroup }

        _exercisesFilterStandards.value =
            _exercisesFilterStandards.value.copy(
                exerciseCategories = exerciseCategoriesToFilterBy,
                muscleGroupsTrained = muscleGroupsToFilterBy
            )

//        Log.d(TAG, "applyFilters() - exerciseCategoriesToFilterBy: $exerciseCategoriesToFilterBy")
//        Log.d(TAG, "applyFilters() - muscleGroupsToFilterBy: $muscleGroupsToFilterBy")
//        Log.d(TAG, "applyFilters() - _exercisesFilterStandards: ${_exercisesFilterStandards.value}")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val exercises = _exercisesFilterStandards
        .flatMapLatest { filterStandards ->
            Log.d(TAG, "val exercises - filterStandards: $filterStandards")
            withContext(viewModelScope.coroutineContext) {
                exercisesRepository.filterExercises(filterStandards = filterStandards)
            }

        }
        .cachedIn(viewModelScope)
}


data class ExerciseCategorySelectionState(val category: ExerciseCategory, val isSelected: Boolean)

data class MuscleGroupSelectionState(val muscleGroup: MuscleGroup, val isSelected: Boolean)

