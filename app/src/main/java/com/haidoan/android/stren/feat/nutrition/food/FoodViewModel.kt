package com.haidoan.android.stren.feat.nutrition.food

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.haidoan.android.stren.core.repository.base.FoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

private const val EMPTY_FOOD_QUERY = ""

@HiltViewModel
class FoodViewModel @Inject constructor(foodRepository: FoodRepository) : ViewModel() {

    var searchBarText = mutableStateOf(EMPTY_FOOD_QUERY)
    private var _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    private val foodNameToQuery = MutableStateFlow("")
    fun searchFoodByName(name: String) {
        foodNameToQuery.update { name }
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val pagedFoodData = foodNameToQuery
        .onEach { _isSearching.update { true } }
        .debounce(800L)
        .flatMapLatest {
            foodRepository.getPagedFoodData(foodNameToQuery = it, pageSize = 10)
        }
        .onEach { _isSearching.update { false } }
        .cachedIn(viewModelScope)
}