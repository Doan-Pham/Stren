package com.haidoan.android.stren.feat.settings.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haidoan.android.stren.core.domain.NutritionCalculationUseCase
import com.haidoan.android.stren.core.model.CommonBiometrics
import com.haidoan.android.stren.core.model.Goal
import com.haidoan.android.stren.core.model.User
import com.haidoan.android.stren.core.repository.base.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import timber.log.Timber
import javax.inject.Inject

private const val UNDEFINED_USER_ID = "UNDEFINED_USER_ID"

@HiltViewModel
internal class ProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _currentUserId =
        savedStateHandle.getStateFlow(USER_ID_PROFILE_NAV_ARG, UNDEFINED_USER_ID)

    private val _uiState: MutableStateFlow<ProfileUiState> =
        MutableStateFlow(ProfileUiState.Loading)
    val uiState = _uiState.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), ProfileUiState.Loading
    )

    init {
        viewModelScope.launch {
            _currentUserId.collect { userId ->
                Timber.d("flatMapLatest() - userId: $userId")
                if (userId == UNDEFINED_USER_ID) {
                    _uiState.update { ProfileUiState.Loading }
                } else {
                    val user = userRepository.getUser(userId)
                    Timber.d("flatMapLatest() - user: $user")
                    _uiState.update { ProfileUiState.LoadComplete(user) }
                }
            }
        }
    }

    fun modifyUiState(user: User) {
        val newUser = user.copy()
        _uiState.update { ProfileUiState.LoadComplete(newUser) }
    }

    fun saveProfile() {
        viewModelScope.launch {
            supervisorScope {
                val tasks = mutableMapOf<String, Deferred<Unit>>()

                val user = (uiState.value as ProfileUiState.LoadComplete).currentUser

                tasks["modifyUserProfileTask"] = async {
                    userRepository.modifyUserProfile(
                        userId = user.id,
                        displayName = user.displayName,
                        age = user.age,
                        sex = user.sex,
                        activityLevel = user.activityLevel,
                        weightGoal = user.weightGoal
                    )
                }

                tasks["addGoalsTask"] = async {
                    val caloriesGoal =
                        NutritionCalculationUseCase.calculateCaloriesGoal(
                            weightInKg = user.biometricsRecords.first { it.biometricsId == CommonBiometrics.WEIGHT.id }.value,
                            heightInCm = user.biometricsRecords.first { it.biometricsId == CommonBiometrics.HEIGHT.id }.value,
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
}