package com.haidoan.android.stren.feat.nutrition.diary.add_food

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haidoan.android.stren.core.model.*
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
internal class AddEditFoodEntryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle, foodRepository: FoodRepository,
    private val eatingDayRepository: EatingDayRepository
) : ViewModel() {
    private val navArgs: EditFoodEntryArgs = EditFoodEntryArgs(savedStateHandle)
    var foodAmountInGram by mutableStateOf(navArgs.foodAmount)
    private lateinit var currentFood: Food

    init {
        Timber.d("navArgs - userId ${navArgs.userId}; eatingDayId: ${navArgs.selectedDate} ; mealId: ${navArgs.mealId}; mealName: ${navArgs.mealName}; foodId: ${navArgs.foodId}; foodAmount: ${navArgs.foodAmount}")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<AddEditFoodEntryUiState> =
        MutableStateFlow(navArgs.foodId).flatMapLatest {
            currentFood = foodRepository.getFoodById(it)
            flowOf(AddEditFoodEntryUiState.LoadComplete(currentFood))
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            AddEditFoodEntryUiState.Loading
        )

    fun onChangeFoodAmount(newAmount: Float) {
        foodAmountInGram = newAmount
    }

    fun addFoodToMeal() {
        viewModelScope.launch {
            val eatingDay =
                eatingDayRepository.getEatingDayByDate(navArgs.userId, navArgs.selectedDate)
            val foodToAdd = FoodToConsume(food = currentFood, amountInGram = foodAmountInGram)
            val mealsAfterUpdate = eatingDay.meals.toMutableList()

            if (eatingDay.meals.any { it.id == navArgs.mealId }) {
                val mealToUpdate = eatingDay.meals.first { it.id == navArgs.mealId }

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
                mealsAfterUpdate.remove(mealToUpdate)
                mealsAfterUpdate.add(mealToUpdate.copy(foods = foodsAfterUpdate))
                Timber.d("mealsAfterUpdate: $mealsAfterUpdate")
            } else {
                mealsAfterUpdate.add(
                    Meal(
                        id = navArgs.mealId,
                        name = navArgs.mealName,
                        foods = listOf(foodToAdd)
                    )
                )
            }

            if (eatingDay == EatingDay.undefined) {
                val id = eatingDayRepository.addEatingDay(
                    userId = navArgs.userId,
                    eatingDay = EatingDay(date = navArgs.selectedDate, meals = mealsAfterUpdate)
                )
                Timber.d("doc id: $id")
            } else {
                eatingDayRepository.updateEatingDay(
                    userId = navArgs.userId,
                    eatingDayId = eatingDay.id,
                    meals = mealsAfterUpdate
                )
            }
        }
    }

}