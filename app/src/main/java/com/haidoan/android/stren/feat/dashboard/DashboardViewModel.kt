package com.haidoan.android.stren.feat.dashboard

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haidoan.android.stren.core.designsystem.component.toCharEntries
import com.haidoan.android.stren.core.designsystem.component.toCharEntryModelProducer
import com.haidoan.android.stren.core.domain.GetUserFullDataUseCase
import com.haidoan.android.stren.core.model.TrackedCategory
import com.haidoan.android.stren.core.model.TrainedExercise
import com.haidoan.android.stren.core.repository.base.EatingDayRepository
import com.haidoan.android.stren.core.repository.base.UserRepository
import com.haidoan.android.stren.core.repository.base.WorkoutsRepository
import com.haidoan.android.stren.core.service.AuthenticationService
import com.haidoan.android.stren.core.utils.DateUtils
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

private const val UNDEFINED_USER_ID = "Undefined User ID"

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
internal class DashboardViewModel @Inject constructor(
    authenticationService: AuthenticationService,
    private val eatingDayRepository: EatingDayRepository,
    private val workoutsRepository: WorkoutsRepository,
    private val userRepository: UserRepository,
    private val getUserFullDataUseCase: GetUserFullDataUseCase
) : ViewModel() {

    private var currentUserId = MutableStateFlow(UNDEFINED_USER_ID)
    private var cachedTrackedCategories = listOf<TrackedCategory>()

    private val _allTrainedExercises = MutableStateFlow(listOf<TrainedExercise>())
    val allTrainedExercises = _allTrainedExercises

    val chartEntryModelProducers = mutableStateMapOf<String, ChartEntryModelProducer>()

    val dataOutputs = currentUserId.flatMapLatest { userId ->
        if (userId == UNDEFINED_USER_ID) return@flatMapLatest flowOf(listOf())
        getUserFullDataUseCase(userId).map { user ->
            cachedTrackedCategories = user.trackedCategories

            Timber.d("getUserFullDataUseCase() - trackedCategories: $cachedTrackedCategories")

            user.trackedCategories.map { category ->
                when (category) {
                    is TrackedCategory.Calories -> {
                        eatingDayRepository.getCaloriesOfDatesStream(
                            userId = userId,
                            startDate = category.startDate,
                            endDate = category.endDate
                        ).flatMapLatest { caloriesOfDates ->
                            val chartData = caloriesOfDates.map { caloriesOfDate ->
                                Pair(
                                    caloriesOfDate.date,
                                    caloriesOfDate.calories.toFloat()
                                )
                            }
                            Timber.d("chartData: $chartData")
                            if (chartEntryModelProducers.containsKey(category.dataSourceId)) {
                                chartEntryModelProducers[category.dataSourceId]?.updateChartEntries(
                                    chartData
                                )
                            } else {
                                chartEntryModelProducers[category.dataSourceId] =
                                    chartData.toCharEntryModelProducer()
                            }

                            flowOf(
                                DataOutput.Calories(
                                    startDate = category.startDate,
                                    endDate = category.endDate,
                                    dataSourceId = category.dataSourceId,
                                    isDefaultCategory = category.isDefaultCategory
                                )
                            )
                        }.stateIn(
                            viewModelScope,
                            SharingStarted.WhileSubscribed(5000),
                            DataOutput.EmptyData
                        )
                    }
                    is TrackedCategory.ExerciseOneRepMax -> {
                        workoutsRepository.getExerciseOneRepMaxesStream(
                            userId = userId,
                            startDate = category.startDate,
                            endDate = category.endDate,
                            exerciseId = category.exerciseId
                        ).flatMapLatest { rawData ->
                            val chartData = rawData.toList()
                            Timber.d("chartData: $chartData")

                            if (chartEntryModelProducers.containsKey(category.dataSourceId)) {
                                chartEntryModelProducers[category.dataSourceId]?.updateChartEntries(
                                    chartData
                                )
                            } else {
                                chartEntryModelProducers[category.dataSourceId] =
                                    chartData.toCharEntryModelProducer()
                            }

                            flowOf(
                                DataOutput.Exercise(
                                    startDate = category.startDate,
                                    endDate = category.endDate,
                                    dataSourceId = category.dataSourceId,
                                    title = category.exerciseName,
                                    isDefaultCategory = category.isDefaultCategory,
                                )
                            )
                        }.stateIn(
                            viewModelScope,
                            SharingStarted.WhileSubscribed(5000),
                            DataOutput.EmptyData
                        )
                    }
                }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf())

    init {
        authenticationService.addAuthStateListeners(
            onUserAuthenticated = { userId ->
                currentUserId.update { userId }
                Timber.d("authStateListen - User signed in - userId: $userId")
            },
            onUserNotAuthenticated = {
                currentUserId.value = UNDEFINED_USER_ID
                Timber.d("authStateListen - User signed out")
            })

    }

    private fun ChartEntryModelProducer.updateChartEntries(
        entriesData: List<Pair<LocalDate, Float>>
    ) {
        this.setEntries(entriesData.toCharEntries())
    }

    fun updateDateRange(dataSourceId: String, startDate: LocalDate, endDate: LocalDate) {
        Timber.d("startDate: $startDate, endDate: $endDate")
        viewModelScope.launch {
            userRepository.updateTrackCategory(
                userId = currentUserId.value,
                dataSourceId = dataSourceId,
                newStartDate = startDate,
                newEndDate = endDate
            )
        }
    }

    fun refreshAllTrainedExercises() {
        viewModelScope.launch {
            _allTrainedExercises.value =
                workoutsRepository.getAllExercisesTrained(userId = currentUserId.value)

            Timber.d("_allTrainedExercises.value: ${_allTrainedExercises.value}")
        }
    }

    fun trackExerciseProgress(exerciseId: String) {
        if (!cachedTrackedCategories.any {
                it is TrackedCategory.ExerciseOneRepMax && it.exerciseId == exerciseId
            }) {
            viewModelScope.launch {
                userRepository.trackCategory(
                    userId = currentUserId.value,
                    category = TrackedCategory.ExerciseOneRepMax(
                        exerciseId = exerciseId,
                        exerciseName = _allTrainedExercises.value.first { it.exercise.id == exerciseId }.exercise.name,
                        isDefaultCategory = false
                    )
                )
            }
        }
    }

    fun stopTrackingCategory(dataSourceId: String) {
        viewModelScope.launch {
            userRepository.stopTrackingCategory(
                userId = currentUserId.value,
                dataSourceId = dataSourceId
            )
        }
    }

    sealed interface DataOutput {
        val isDefaultCategory: Boolean
        val startDate: LocalDate
        val endDate: LocalDate
        val dataSourceId: String
        val title: String

        object EmptyData : DataOutput {
            override val isDefaultCategory: Boolean
                get() = true
            override val startDate: LocalDate
                get() = DateUtils.getCurrentDate()
            override val endDate: LocalDate
                get() = DateUtils.getCurrentDate()
            override val dataSourceId: String
                get() = "EMPTY_DATA"
            override val title: String
                get() = ""
        }

        data class Calories(
            override val startDate: LocalDate, override val endDate: LocalDate,
            override val dataSourceId: String, override val title: String = "Calories",
            override val isDefaultCategory: Boolean
        ) : DataOutput

        data class Exercise(
            override val startDate: LocalDate, override val endDate: LocalDate,
            override val dataSourceId: String, override val title: String,
            override val isDefaultCategory: Boolean
        ) : DataOutput
    }
}

