package com.haidoan.android.stren.feat.training.exercises.view_exercises

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.haidoan.android.stren.core.model.ExerciseCategory
import com.haidoan.android.stren.core.model.ExerciseQueryParameters
import com.haidoan.android.stren.core.model.MuscleGroup
import com.haidoan.android.stren.core.repository.base.ExercisesRepository
import com.haidoan.android.stren.core.repository.base.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class ExercisesViewModel @Inject constructor(
    exercisesRepository: ExercisesRepository,
    userRepository: UserRepository
) :
    ViewModel() {
    var searchBarText = mutableStateOf("")
    private var _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    private val _selectedCategoriesIds: MutableStateFlow<List<String>> =
        MutableStateFlow(listOf())

    @OptIn(ExperimentalCoroutinesApi::class)
    val exerciseCategories = _selectedCategoriesIds.flatMapLatest { ids ->
        Timber.d("chosenCategoriesIds-map()- ids: $ids")
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
        Timber.d("toggleCategorySelection() - categoryId(): $categoryId")

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
        Timber.d("chosenCategoriesIds-map()- ids: $ids")
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
        Timber.d("toggleMuscleGroupSelection() - muscleGroupId(): $muscleGroupId")
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
        MutableStateFlow(ExerciseQueryParameters("", listOf(), listOf()))

    fun searchExerciseByName(exerciseName: String) {
//        Timber.d( "searchExerciseByName() - [Param]exerciseName: $exerciseName")
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
//        Timber.d( "applyFilters() - exerciseCategoriesToFilterBy: $exerciseCategoriesToFilterBy")
//        Timber.d( "applyFilters() - muscleGroupsToFilterBy: $muscleGroupsToFilterBy")
//        Timber.d( "applyFilters() - _exercisesFilterStandards: ${_exercisesFilterStandards.value}")
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val exercises = _exercisesFilterStandards
        .onEach { _isSearching.update { true } }
        .debounce(800L)
        .flatMapLatest { filterStandards ->
            Timber.d("val exercises - filterStandards: $filterStandards")
            withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
                exercisesRepository.searchExercises(filterStandards = filterStandards)
            }
        }
        .onEach { _isSearching.update { false } }
        .cachedIn(viewModelScope)

}


data class ExerciseCategorySelectionState(val category: ExerciseCategory, val isSelected: Boolean)

data class MuscleGroupSelectionState(val muscleGroup: MuscleGroup, val isSelected: Boolean)

