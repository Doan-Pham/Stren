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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
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

    private val foodNameToQuery = MutableStateFlow("")

    init {
        Timber.d("navArgs - userId ${navArgs.userId}; eatingDayId: ${navArgs.eatingDayId} ; mealId: ${navArgs.mealId}")
    }

    fun searchFoodByName(name: String) {
        foodNameToQuery.update { name }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val pagedFoodData = foodNameToQuery.flatMapLatest {
        foodRepository.getPagedFoodData(foodNameToQuery = it)
    }.cachedIn(viewModelScope)
}
