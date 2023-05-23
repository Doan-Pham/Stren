package com.haidoan.android.stren.feat.nutrition.food.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haidoan.android.stren.core.repository.base.FoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
internal class FoodDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle, foodRepository: FoodRepository
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<FoodDetailUiState> =
        savedStateHandle.getStateFlow(FOOD_ID_FOOD_DETAIL_NAV_ARG, "Undefined").flatMapLatest {
            flowOf(FoodDetailUiState.LoadComplete(foodRepository.getFoodById(it)))
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            FoodDetailUiState.Loading
        )
}