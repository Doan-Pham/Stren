package com.haidoan.android.stren.feat.nutrition.food

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.haidoan.android.stren.core.repository.FoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FoodViewModel @Inject constructor(foodRepository: FoodRepository) : ViewModel() {
    val pagedFoodData =
        foodRepository.getPagedFoodData().cachedIn(viewModelScope)
}