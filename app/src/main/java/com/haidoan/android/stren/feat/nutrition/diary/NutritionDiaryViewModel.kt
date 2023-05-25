package com.haidoan.android.stren.feat.nutrition.diary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haidoan.android.stren.core.model.DefaultMeal
import com.haidoan.android.stren.core.model.EatingDay
import com.haidoan.android.stren.core.model.FoodToConsume
import com.haidoan.android.stren.core.model.Meal
import com.haidoan.android.stren.core.repository.base.EatingDayRepository
import com.haidoan.android.stren.core.service.AuthenticationService
import com.haidoan.android.stren.core.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

const val UNDEFINED_USER_ID = "Undefined User ID"

@HiltViewModel
internal class NutritionDiaryViewModel @Inject constructor(
    authenticationService: AuthenticationService,
    private val eatingDayRepository: EatingDayRepository
) : ViewModel() {

    /**
     * Need to initialize userId before init{} block, or the init{} block will access
     * it when it's null and causes NullPointerException
     */
    private val _dataFetchingTriggers: MutableStateFlow<DataFetchingTriggers> = MutableStateFlow(
        DataFetchingTriggers(userId = UNDEFINED_USER_ID, selectedDate = DateUtils.getCurrentDate())
    )

    /**
     * This var caches the response from repository for use when updating
     */
    private lateinit var currentEatingDay: EatingDay

    init {
        authenticationService.addAuthStateListeners(
            onUserAuthenticated = {
                _dataFetchingTriggers.value = _dataFetchingTriggers.value.copy(userId = it)
                Timber.d("authStateListen - User signed in - userId: $it")
            },
            onUserNotAuthenticated = {
                _dataFetchingTriggers.value =
                    _dataFetchingTriggers.value.copy(userId = UNDEFINED_USER_ID)
                Timber.d("authStateListen - User signed out")
            })
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<NutritionDiaryUiState> =
        _dataFetchingTriggers.flatMapLatest { triggers ->
            val userId = triggers.userId
            val selectedDate = triggers.selectedDate

            if (userId != UNDEFINED_USER_ID) {
                combine(
                    eatingDayRepository.getEatingDayByUserIdAndDate(userId, selectedDate),
                    eatingDayRepository.getDatesUserTracked(userId)
                ) { eatingDay, datesTracked ->
                    currentEatingDay = eatingDay.addMeals(Meal.defaultMeals)
                    NutritionDiaryUiState.LoadComplete(
                        userId,
                        currentEatingDay,
                        selectedDate,
                        datesTracked
                    )
                }
            } else {
                flowOf(NutritionDiaryUiState.Loading)
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000), NutritionDiaryUiState.Loading
        )

    private fun EatingDay.addMeals(meals: List<Meal>): EatingDay {
        val mealsToAddById = meals.associateBy { it.id }
        val existingMealsById = this.meals.associateBy { it.id }
        val mergedMeals = mutableListOf<Meal>()

        //Timber.d("mealsToAddById: $mealsToAddById")
        //Timber.d("existingMealsById: $existingMealsById")

        mergedMeals.addAll(existingMealsById.values.filter { it.id !in mealsToAddById.keys })
        //Timber.d("mergedMeals - 1st addAll: ${mergedMeals.map { it.name }}")

        mergedMeals.addAll(mealsToAddById.values.filter { it.id !in existingMealsById.keys })
        //Timber.d("mergedMeals - 2nd addAll: ${mergedMeals.map { it.name }}")

        val mealsWithSameId =
            existingMealsById.values.filter { it.id in mealsToAddById.keys }.map { existingMeal ->
                val foodsToAddById =
                    mealsToAddById[existingMeal.id]?.foods?.associateBy { it.food.id }
                val existingFoodById = existingMeal.foods.associateBy { it.food.id }
                val resultFoods = mutableListOf<FoodToConsume>()

                //Timber.d("foodsToAddById: $foodsToAddById")
                //Timber.d("existingFoodById: $existingFoodById")

                resultFoods.addAll(existingFoodById.values.filter {
                    it.food.id !in (foodsToAddById?.keys ?: listOf())
                })
                //Timber.d("resultFoods - 1st addAll: $resultFoods")

                resultFoods.addAll(foodsToAddById?.values?.filter {
                    it.food.id !in existingFoodById.keys
                } ?: listOf())
                //Timber.d("resultFoods - 2nd addAll: $resultFoods")

                resultFoods.addAll(existingFoodById.values.filter {
                    it.food.id in (foodsToAddById?.keys ?: listOf())
                }.map {
                    it.copy(
                        amountInGram = foodsToAddById?.get(it.food.id)?.amountInGram ?: 0f
                    )
                })
                //Timber.d("resultFoods - 3rd addAll: $resultFoods")

                existingMeal.copy(foods = resultFoods)
            }
        //Timber.d("mealsWithSameId: mealsWithSameId")

        mergedMeals.addAll(mealsWithSameId)
        //Timber.d("mergedMeals - 3rd addAll: mergedMeals")

        val resultMeals = mutableListOf<Meal>()
        resultMeals.addAll(mergedMeals.filter {
            it.id in DefaultMeal.values().map { defaultMeal -> defaultMeal.id }
        }
            .sortedBy { DefaultMeal.from(it.id)?.indexWhenSorted })

        resultMeals.addAll(mergedMeals.filter {
            it.id !in DefaultMeal.values().map { defaultMeal -> defaultMeal.id }
        })

        Timber.d("resultMeals - $resultMeals")
        return this.copy(meals = resultMeals)
    }

    fun selectDate(date: LocalDate) {
        _dataFetchingTriggers.value = _dataFetchingTriggers.value.copy(selectedDate = date)
    }

    fun setCurrentDateToDefault() {
        selectDate(DateUtils.getCurrentDate())
    }

    fun moveToNextDay() {
        val selectedDate = _dataFetchingTriggers.value.selectedDate
        selectDate(selectedDate.plusDays(1))
    }

    fun moveToPreviousDay() {
        val selectedDate = _dataFetchingTriggers.value.selectedDate
        selectDate(selectedDate.minusDays(1))
    }
}

/**
 * Kotlin Flow's flatMapLatest() can collect a flow and flatMap it whenever it changes, but
 * it only works with 1 input flow.
 *
 * By wrapping inside this class all the different data objects that should triggers flatMapLatest()
 * when they change, developer can indirectly use flatMapLatest() with more than 1 input
 */
private data class DataFetchingTriggers(val userId: String, val selectedDate: LocalDate)