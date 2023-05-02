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

    private val _chosenCategoriesIds: MutableStateFlow<MutableList<String>> =
        MutableStateFlow(mutableListOf())

    @OptIn(ExperimentalCoroutinesApi::class)
    val exerciseCategories = _chosenCategoriesIds.flatMapLatest { ids ->
        Log.d(TAG, "chosenCategoriesIds-map()- ids: $ids")
        exercisesRepository.getAllExerciseCategories()
            .map { categories ->
                Log.d(TAG, "chosenCategoriesIds-map()- ids: $ids")
                categories.map {
                    Log.d(
                        TAG, "chosenCategoriesIds-map()- ids: $ids"
                    )
                    ExerciseCategoryWrapper(
                        it,
                        ids.contains(it.id)
                    )
                }
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf())

    fun toggleCategorySelection(categoryId: String) {
        Log.d(TAG, "toggleCategorySelection() - categoryId(): $categoryId")
        if (_chosenCategoriesIds.value.contains(categoryId)) {
            Log.d(TAG, "toggleCategorySelection() - Unselecting")
            _chosenCategoriesIds.update {
                it.apply { remove(categoryId) }
            }
            Log.d(TAG, "toggleCategorySelection() - After Unselect: ${_chosenCategoriesIds.value}")
        } else {
            Log.d(TAG, "toggleCategorySelection() - Selecting")
            _chosenCategoriesIds.update {
                it.apply { add(categoryId) }
            }
            Log.d(TAG, "toggleCategorySelection() - After Select: ${_chosenCategoriesIds.value}")
        }
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