package com.haidoan.android.stren.feat.trainining.exercises

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.haidoan.android.stren.core.model.ExerciseCategory
import com.haidoan.android.stren.core.repository.ExercisesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

private const val TAG = "ExercisesViewModel"

@HiltViewModel
internal class ExercisesViewModel @Inject constructor(exercisesRepository: ExercisesRepository) :
    ViewModel() {
    var searchBarText = mutableStateOf("")

    private val _chosenCategoriesIds: MutableStateFlow<List<String>> =
        MutableStateFlow(listOf())

    @OptIn(ExperimentalCoroutinesApi::class)
    val exerciseCategories = _chosenCategoriesIds.flatMapLatest { ids ->
        Log.d(TAG, "chosenCategoriesIds-map()- ids: $ids")
        exercisesRepository.getAllExerciseCategories()
            .map { categories ->
                categories.map {
                    ExerciseCategoryWrapper(
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
        newValue.addAll(_chosenCategoriesIds.value)

        if (_chosenCategoriesIds.value.contains(categoryId)) {
            newValue.remove(categoryId)
        } else {
            newValue.add(categoryId)
        }
        _chosenCategoriesIds.value = newValue.toList()
    }

    private val exerciseNameToQuery = MutableStateFlow("")
    fun searchExerciseByName(exerciseName: String) {
        Log.d(TAG, "searchExerciseByName() - [Param]exerciseName: $exerciseName")
        exerciseNameToQuery.value = exerciseName
        Log.d(
            TAG,
            "searchExerciseByName() - [Param]exerciseNameToQuery.value: ${exerciseNameToQuery.value}"
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val exercises = exerciseNameToQuery
        .flatMapLatest { exerciseNameToQuery ->
            if (exerciseNameToQuery.isEmpty() || exerciseNameToQuery.isBlank()) {
                exercisesRepository.getExercisesWithLimit()
            } else {
                exercisesRepository.getExercisesByNameWithLimit(exerciseName = exerciseNameToQuery)
            }

        }
        .cachedIn(viewModelScope)

}

data class ExerciseCategoryWrapper(val category: ExerciseCategory, val isChosen: Boolean)