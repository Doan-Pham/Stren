package com.haidoan.android.stren.feat.nutrition.food

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.haidoan.android.stren.core.repository.base.FoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import javax.inject.Inject

private const val EMPTY_FOOD_QUERY = ""

@HiltViewModel
class FoodViewModel @Inject constructor(foodRepository: FoodRepository) : ViewModel() {

    var searchBarText = mutableStateOf(EMPTY_FOOD_QUERY)

    private val foodNameToQuery = MutableStateFlow("")
    fun searchFoodByName(name: String) {
        foodNameToQuery.update { name }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val pagedFoodData = foodNameToQuery.flatMapLatest {
        foodRepository.getPagedFoodData(foodNameToQuery = it)
    }.cachedIn(viewModelScope)
}