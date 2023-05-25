package com.haidoan.android.stren.feat.nutrition.diary.add_food

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haidoan.android.stren.core.model.FoodNutrient
import com.haidoan.android.stren.core.repository.base.FoodRepository
import com.haidoan.android.stren.feat.nutrition.diary.EditFoodEntryArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class EditFoodEntryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle, foodRepository: FoodRepository
) : ViewModel() {
    private val navArgs: EditFoodEntryArgs = EditFoodEntryArgs(savedStateHandle)
    var foodAmountInGram by mutableStateOf(FoodNutrient.DEFAULT_FOOD_AMOUNT_IN_GRAM)

    init {
        Timber.d("navArgs - userId ${navArgs.userId}; eatingDayId: ${navArgs.eatingDayId} ; mealId: ${navArgs.mealId}; foodId: ${navArgs.foodId}")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<EditFoodEntryUiState> =
        savedStateHandle.getStateFlow(FOOD_ID_EDIT_FOOD_ENTRY_NAV_ARG, "Undefined").flatMapLatest {
            flowOf(EditFoodEntryUiState.LoadComplete(foodRepository.getFoodById(it)))
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            EditFoodEntryUiState.Loading
        )

    fun onChangeFoodAmount(newAmount: Float) {
        foodAmountInGram = newAmount
    }
}