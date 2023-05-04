package com.haidoan.android.stren.feat.trainining.exercises

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
import timber.log.Timber
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
        Timber.d(TAG, "chosenCategoriesIds-map()- ids: $ids")
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
        Timber.d(TAG, "toggleCategorySelection() - categoryId(): $categoryId")

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
        Timber.d(TAG, "chosenCategoriesIds-map()- ids: $ids")
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
        Timber.d(TAG, "toggleMuscleGroupSelection() - muscleGroupId(): $muscleGroupId")
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

    fun searchExerciseByName(exerciseName: String) {
//        Timber.d(TAG, "searchExerciseByName() - [Param]exerciseName: $exerciseName")
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

        // Need to reset exerciseCategories and muscleGroups selection state
        // before applying filters, since some updates to the selection state
        // may not be fast enough
        val selectedCategoriesIds = _selectedCategoriesIds.value
        val exerciseCategoriesAfterReset =
            exerciseCategories.value.map {
                ExerciseCategorySelectionState(
                    it.category,
                    selectedCategoriesIds.contains(it.category.id)
                )
            }

        val selectedMuscleGroupsIds = _selectedMuscleGroupsIds.value

        val muscleGroupsAfterReset =
            muscleGroups.value.map {
                MuscleGroupSelectionState(
                    it.muscleGroup,
                    selectedMuscleGroupsIds.contains(it.muscleGroup.id)
                )
            }

        // If no filter is selected, application should show all exercises,
        // as if ALL filters are selected
        val exerciseCategoriesToFilterBy =
            if (exerciseCategoriesAfterReset.any { it.isSelected }) {
                exerciseCategoriesAfterReset.filter { it.isSelected }
            } else {
                exerciseCategoriesAfterReset
            }.map { it.category }

        val muscleGroupsToFilterBy =
            if (muscleGroupsAfterReset.any { it.isSelected }) {
                muscleGroupsAfterReset.filter { it.isSelected }
            } else {
                muscleGroupsAfterReset
            }.map { it.muscleGroup }

        _exercisesFilterStandards.value =
            _exercisesFilterStandards.value.copy(
                exerciseCategories = exerciseCategoriesToFilterBy,
                muscleGroupsTrained = muscleGroupsToFilterBy
            )
//
//        Timber.d(TAG, "applyFilters() - exerciseCategoriesToFilterBy: $exerciseCategoriesToFilterBy")
//        Timber.d(TAG, "applyFilters() - muscleGroupsToFilterBy: $muscleGroupsToFilterBy")
//        Timber.d(TAG, "applyFilters() - _exercisesFilterStandards: ${_exercisesFilterStandards.value}")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val exercises = _exercisesFilterStandards
        .flatMapLatest { filterStandards ->
            Timber.d(TAG, "val exercises - filterStandards: $filterStandards")
            withContext(viewModelScope.coroutineContext) {
                exercisesRepository.filterExercises(filterStandards = filterStandards)
            }

        }
        .cachedIn(viewModelScope)
}


data class ExerciseCategorySelectionState(val category: ExerciseCategory, val isSelected: Boolean)

data class MuscleGroupSelectionState(val muscleGroup: MuscleGroup, val isSelected: Boolean)

