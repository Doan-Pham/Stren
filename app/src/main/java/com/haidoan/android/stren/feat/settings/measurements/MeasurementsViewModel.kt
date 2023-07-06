package com.haidoan.android.stren.feat.settings.measurements

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haidoan.android.stren.core.domain.GetUserFullLatestBiometricsRecordsUseCase
import com.haidoan.android.stren.core.domain.NutritionCalculationUseCase
import com.haidoan.android.stren.core.model.BiometricsRecord
import com.haidoan.android.stren.core.model.CommonBiometrics
import com.haidoan.android.stren.core.model.Goal
import com.haidoan.android.stren.core.repository.base.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

private const val UNDEFINED_USER_ID = "UNDEFINED_USER_ID"

@HiltViewModel
internal class MeasurementsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
    private val getUserBiometricsRecords: GetUserFullLatestBiometricsRecordsUseCase
) : ViewModel() {

    private var cachedUserId = UNDEFINED_USER_ID


    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState = savedStateHandle.getStateFlow(USER_ID_MEASUREMENTS_NAV_ARG, UNDEFINED_USER_ID)
        .flatMapLatest { userId ->
            cachedUserId = userId
            getUserBiometricsRecords(userId).map { MeasurementsUiState.LoadComplete(it) }
        }
        .stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), MeasurementsUiState.Loading
        )

    fun addBiometricsRecord(biometricsRecord: BiometricsRecord) {
        viewModelScope.launch {
            val tasks = mutableMapOf<String, Deferred<Unit>>()

            tasks["addBiometricsRecord"] = async {
                userRepository.addBiometricsRecord(
                    userId = cachedUserId,
                    biometricsRecord = biometricsRecord
                )
            }

            if (biometricsRecord.biometricsId == CommonBiometrics.HEIGHT.id ||
                biometricsRecord.biometricsId == CommonBiometrics.WEIGHT.id
            ) {
                val user = userRepository.getUser(cachedUserId)
                tasks["addGoalsTask"] = async {
                    val weightInKg =
                        if (biometricsRecord.biometricsId == CommonBiometrics.WEIGHT.id) {
                            biometricsRecord.value
                        } else {
                            user.biometricsRecords.first { it.biometricsId == CommonBiometrics.WEIGHT.id }.value
                        }

                    val heightInCm =
                        if (biometricsRecord.biometricsId == CommonBiometrics.HEIGHT.id) {
                            biometricsRecord.value
                        } else {
                            user.biometricsRecords.first { it.biometricsId == CommonBiometrics.HEIGHT.id }.value
                        }
                    val caloriesGoal =
                        NutritionCalculationUseCase.calculateCaloriesGoal(
                            weightInKg = weightInKg,
                            heightInCm = heightInCm,
                            age = user.age,
                            sex = user.sex,
                            bmrActivityFactor = user.activityLevel.bmrFactor,
                            amountToModifyBasedOnGoal = user.weightGoal.caloriesAmountToModify
                        )
                    val coreNutrientsGoals =
                        NutritionCalculationUseCase.calculateCoreNutrientGoals(calories = caloriesGoal)

                    userRepository.addGoals(
                        userId = user.id,
                        goals =
                        Goal.FoodNutrientGoal.createCoreNutrientAndCalorieGoals(
                            caloriesAmountInKcal = caloriesGoal,
                            coreNutrientAmountInGram = coreNutrientsGoals
                        )
                    )
                }
            }



            tasks.forEach {
                try {
                    it.value.await()
                } catch (e: Exception) {
                    Timber.e("saveProfile() - ${it.key} fails with exception: $e")
                }
            }

        }
    }
}