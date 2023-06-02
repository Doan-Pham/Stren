package com.haidoan.android.stren.feat.onboarding

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haidoan.android.stren.core.domain.ActivityLevel
import com.haidoan.android.stren.core.domain.NutritionCalculationUseCase
import com.haidoan.android.stren.core.domain.NutritionCalculationUseCase.Sex
import com.haidoan.android.stren.core.domain.NutritionCalculationUseCase.calculateCaloriesGoal
import com.haidoan.android.stren.core.domain.NutritionCalculationUseCase.calculateCoreNutrientGoals
import com.haidoan.android.stren.core.domain.WeightGoal
import com.haidoan.android.stren.core.model.BiometricsRecord
import com.haidoan.android.stren.core.model.Goal
import com.haidoan.android.stren.core.repository.base.UserRepository
import com.haidoan.android.stren.core.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class OnboardingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository
) :
    ViewModel() {
    var isOnboardingComplete by mutableStateOf(false)
    var userId: String = checkNotNull(savedStateHandle[USER_ID_ONBOARDING_NAV_ARG])
    var uiState by mutableStateOf(OnboardingUiState())
    fun completeOnboarding() {
        viewModelScope.launch {
            // A supervisorScope makes sure that children coroutines's exception won't
            // crash the parent coroutine down ("tasks" coroutines won't crash
            // the parent made with "viewModelScope.launch{}"
            // Reference: https://www.youtube.com/watch?v=w0kfnydnFWI&ab_channel=JetBrains
            supervisorScope {
                val tasks = mutableMapOf<String, Deferred<Unit>>()

                tasks["modifyUserProfileTask"] = async {
                    userRepository.modifyUserProfile(
                        userId,
                        uiState.displayName,
                        uiState.age,
                        uiState.sex.name
                    )
                }
                tasks["addBiometricsTask"] = async {
                    userRepository.addBiometricsRecord(
                        userId = userId,
                        biometricsRecords = listOf(
                            BiometricsRecord.createWeightBiometrics(
                                uiState.weight,
                                DateUtils.getCurrentDate()
                            ),
                            BiometricsRecord.createHeightBiometrics(
                                uiState.height,
                                DateUtils.getCurrentDate()
                            ),
                        )
                    )
                }
                tasks["addGoalsTask"] = async {
                    val caloriesGoal =
                        calculateCaloriesGoal(
                            weightInKg = uiState.weight,
                            heightInCm = uiState.height,
                            age = uiState.age,
                            sex = uiState.sex,
                            bmrActivityFactor = uiState.selectedActivityLevel.bmrFactor,
                            amountToModifyBasedOnGoal = uiState.selectedWeightGoal.caloriesAmountToModify
                        )
                    val coreNutrientsGoals = calculateCoreNutrientGoals(calories = caloriesGoal)

                    Timber.d("completeOnboarding() - caloriesGoal: $caloriesGoal")
                    Timber.d("completeOnboarding() - coreNutrientsGoals: $coreNutrientsGoals")
                    userRepository.addGoals(
                        userId = userId,
                        goals =
                        Goal.FoodNutrientGoal.createCoreNutrientAndCalorieGoals(
                            caloriesAmountInKcal = caloriesGoal,
                            coreNutrientAmountInGram = coreNutrientsGoals
                        )
                    )
                }
                tasks["markUserAsOnboardingCompleteTask"] = async {
                    userRepository.completeOnboarding(userId)
                }

                tasks.forEach {
                    try {
                        it.value.await()
                    } catch (e: Exception) {
                        Timber.e("completeOnboarding() - ${it.key} fails with exception: $e")
                    }
                }
                isOnboardingComplete = true
            }
        }
    }
}

internal data class OnboardingUiState(
    val displayName: String = "",
    val age: Long = 20,
    val sex: Sex = Sex.MALE,
    val weight: Float = 68F,
    val height: Float = 170F,
    val sexes: List<Sex> = Sex.values().toList(),
    val activityLevels: List<ActivityLevel> = NutritionCalculationUseCase.activityLevels,
    val selectedActivityLevel: ActivityLevel = activityLevels.first(),
    val weightGoals: List<WeightGoal> = NutritionCalculationUseCase.weightGoals,
    val selectedWeightGoal: WeightGoal = weightGoals.first()
)
