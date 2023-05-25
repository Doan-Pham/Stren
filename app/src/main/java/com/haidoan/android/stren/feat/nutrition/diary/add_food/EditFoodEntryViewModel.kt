package com.haidoan.android.stren.feat.nutrition.diary.add_food

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haidoan.android.stren.core.model.Food
import com.haidoan.android.stren.core.model.FoodNutrient
import com.haidoan.android.stren.core.model.FoodToConsume
import com.haidoan.android.stren.core.repository.base.EatingDayRepository
import com.haidoan.android.stren.core.repository.base.FoodRepository
import com.haidoan.android.stren.core.utils.ListUtils.replaceWith
import com.haidoan.android.stren.feat.nutrition.diary.EditFoodEntryArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class EditFoodEntryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle, foodRepository: FoodRepository,
    private val eatingDayRepository: EatingDayRepository
) : ViewModel() {
    var foodAmountInGram by mutableStateOf(FoodNutrient.DEFAULT_FOOD_AMOUNT_IN_GRAM)

    private val navArgs: EditFoodEntryArgs = EditFoodEntryArgs(savedStateHandle)
    private lateinit var currentFood: Food

    init {
        Timber.d("navArgs - userId ${navArgs.userId}; eatingDayId: ${navArgs.eatingDayId} ; mealId: ${navArgs.mealId}; mealName: ${navArgs.mealName}; foodId: ${navArgs.foodId}")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<EditFoodEntryUiState> =
        MutableStateFlow(navArgs.foodId).flatMapLatest {
            currentFood = foodRepository.getFoodById(it)
            flowOf(EditFoodEntryUiState.LoadComplete(currentFood))
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            EditFoodEntryUiState.Loading
        )

    fun onChangeFoodAmount(newAmount: Float) {
        foodAmountInGram = newAmount
    }

    fun addFoodToMeal() {
        viewModelScope.launch {
            val eatingDay =
                eatingDayRepository.getEatingDayById(navArgs.userId, navArgs.eatingDayId)

            if (eatingDay.meals.any { it.id == navArgs.mealId }) {
                val mealToUpdate = eatingDay.meals.first { it.id == navArgs.mealId }
                val foodToAdd = FoodToConsume(food = currentFood, amountInGram = foodAmountInGram)

                val foodsAfterUpdate: List<FoodToConsume> =
                    if (mealToUpdate.foods.any { it.food.id == foodToAdd.food.id }) {
                        mealToUpdate.foods.replaceWith(foodToAdd) { foodToConsume ->
                            foodToConsume.food.id == currentFood.id
                        }
                    } else {
                        val mealsAfterAddFood = mealToUpdate.foods.toMutableList()
                        mealsAfterAddFood.add(foodToAdd)
                        mealsAfterAddFood
                    }

                Timber.d("foodsAfterUpdate: $foodsAfterUpdate")

                val mealsAfterUpdate = eatingDay.meals.toMutableList()
                mealsAfterUpdate.remove(mealToUpdate)
                mealsAfterUpdate.add(mealToUpdate.copy(foods = foodsAfterUpdate))

                Timber.d("mealsAfterUpdate: $mealsAfterUpdate")

                eatingDayRepository.updateEatingDay(
                    userId = navArgs.userId,
                    eatingDayId = navArgs.eatingDayId,
                    meals = mealsAfterUpdate
                )
            } else {

            }
        }
    }

}