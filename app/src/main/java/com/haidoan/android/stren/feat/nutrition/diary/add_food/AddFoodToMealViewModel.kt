package com.haidoan.android.stren.feat.nutrition.diary.add_food

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.haidoan.android.stren.core.repository.base.FoodRepository
import com.haidoan.android.stren.feat.nutrition.diary.AddFoodToMealArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject


private const val EMPTY_FOOD_QUERY = ""

@HiltViewModel
class AddFoodToMealViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    foodRepository: FoodRepository
) : ViewModel() {
    internal val navArgs: AddFoodToMealArgs = AddFoodToMealArgs(savedStateHandle)
    var searchBarText = mutableStateOf(EMPTY_FOOD_QUERY)
    private var _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    private val foodNameToQuery = MutableStateFlow("")

    init {
        Timber.d("navArgs - userId ${navArgs.userId}; eatingDayId: ${navArgs.selectedDate} ; mealId: ${navArgs.mealId}; mealName: ${navArgs.mealName}\"")
    }

    fun searchFoodByName(name: String) {
        foodNameToQuery.update { name }
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val pagedFoodData = foodNameToQuery
        .onEach { _isSearching.update { true } }
        .debounce(800L)
        .flatMapLatest {
            foodRepository.getPagedFoodData(foodNameToQuery = it)
        }
        .onEach { _isSearching.update { false } }
        .cachedIn(viewModelScope)
}
