package com.haidoan.android.stren.feat.dashboard

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
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

    private var cachedUserId = UNDEFINED_USER_ID
    private val _allTrainedExercises = MutableStateFlow(listOf<TrainedExercise>())
    val allTrainedExercises = _allTrainedExercises


    /**
     * Need to initialize userId before init{} block, or the init{} block will access
     * it when it's null and causes NullPointerException
     */
    private val _dataFetchingTriggers: SnapshotStateList<MutableStateFlow<DataFetchingTrigger>> =
        mutableStateListOf(
//            MutableStateFlow(
//                DataFetchingTrigger.CaloriesFetchingTrigger(
//                    userId = UNDEFINED_USER_ID,
//                    startDate = DateUtils.getCurrentDate(),
//                    endDate = DateUtils.getCurrentDate(),
//                )
//            )
        )


    val chartEntryModelProducers =
        mutableStateMapOf("DATA_SOURCE_ID_CALORIES" to listOf(DateUtils.getCurrentDate() to 0f).toCharEntryModelProducer())


    val dataOutputs
        @Composable
        get() = _dataFetchingTriggers.map {
            Timber.d("_dataFetchingTriggers.map() - StateFlowTrigger: $it")
            Timber.d("_dataFetchingTriggers.map() - Trigger: ${it.value}")
            it.flatMapLatest { trigger ->
                Timber.d("StateFlowTrigger.flatMapLatest() - StateFlowTrigger: $it - content: $trigger")
                if (trigger.userId == UNDEFINED_USER_ID) return@flatMapLatest flowOf(
                    DataOutput.EmptyData
                )
                when (trigger) {
                    is DataFetchingTrigger.CaloriesFetchingTrigger -> {
                        eatingDayRepository.getCaloriesOfDatesStream(
                            userId = trigger.userId,
                            startDate = trigger.startDate,
                            endDate = trigger.endDate
                        ).map { caloriesOfDates ->
                            val chartData = caloriesOfDates.map { caloriesOfDate ->
                                Pair(
                                    caloriesOfDate.date,
                                    caloriesOfDate.calories.toFloat()
                                )
                            }
                            Timber.d("chartData: $chartData")
                            if (chartEntryModelProducers.containsKey(trigger.dataSourceId)) {
                                chartEntryModelProducers[trigger.dataSourceId]?.updateChartEntries(
                                    chartData
                                )
                            } else {
                                chartEntryModelProducers[trigger.dataSourceId] =
                                    chartData.toList().toCharEntryModelProducer()
                            }

                            DataOutput.Calories(
                                startDate = trigger.startDate,
                                endDate = trigger.endDate,
                                dataSourceId = trigger.dataSourceId
                            )
                        }
                    }
                    is DataFetchingTrigger.ExerciseFetchingTrigger -> {
                        workoutsRepository.getExerciseOneRepMaxesStream(
                            userId = trigger.userId,
                            startDate = trigger.startDate,
                            endDate = trigger.endDate,
                            exerciseId = trigger.exerciseId
                        ).map { rawData ->
                            val chartData = rawData.toList()
                            Timber.d("chartData: $chartData")
                            if (chartEntryModelProducers.containsKey(trigger.dataSourceId)) {
                                chartEntryModelProducers[trigger.dataSourceId]?.updateChartEntries(
                                    chartData
                                )
                            } else {
                                chartEntryModelProducers[trigger.dataSourceId] =
                                    chartData.toList().toCharEntryModelProducer()
                            }

                            DataOutput.Exercise(
                                startDate = trigger.startDate,
                                endDate = trigger.endDate,
                                dataSourceId = trigger.dataSourceId,
                                title = _allTrainedExercises.value
                                    .find { trainedExercise ->
                                        trainedExercise.exercise.id == trigger.exerciseId
                                    }
                                    ?.exercise?.name ?: "Unknown Exercise"
                            )
                        }
                    }
                }
            }.stateIn(
                viewModelScope, SharingStarted.WhileSubscribed(5000), DataOutput.EmptyData
            )
        }

    init {
        authenticationService.addAuthStateListeners(
            onUserAuthenticated = { userId ->
                cachedUserId = userId
                _dataFetchingTriggers.forEach {
                    it.value = it.value.withUserId(userId)
                }
                viewModelScope.launch {
                    getUserFullDataUseCase(userId).collect { user ->
                        for (category in user.trackedCategories) {
                            if (!_dataFetchingTriggers
                                    .any { it.value.dataSourceId == category.dataSourceId }
                            ) {
                                _dataFetchingTriggers.add(
                                    MutableStateFlow(
                                        category.toDataFetchingTrigger()
                                    )
                                )
                            }
                        }
                    }
                }

                Timber.d("authStateListen - User signed in - userId: $userId")
                Timber.d("_dataFetchingTriggers - $_dataFetchingTriggers")
            },
            onUserNotAuthenticated = {
                cachedUserId = UNDEFINED_USER_ID
                _dataFetchingTriggers.forEach {
                    it.value = it.value.withUserId(UNDEFINED_USER_ID)
                }
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
        _dataFetchingTriggers.first { it.value.dataSourceId == dataSourceId }.value =
            _dataFetchingTriggers.first { it.value.dataSourceId == dataSourceId }.value.withStartDate(
                startDate
            ).withEndDate(endDate)
        Timber.d("updateDateRange() - _dataFetchingTriggers: $_dataFetchingTriggers")
    }

    fun refreshAllTrainedExercises() {
        viewModelScope.launch {
            _allTrainedExercises.value =
                workoutsRepository.getAllExercisesTrained(userId = cachedUserId)

            Timber.d("_allTrainedExercises.value: ${_allTrainedExercises.value}")
        }
    }

    fun trackExerciseProgress(exerciseId: String) {
        if (!_dataFetchingTriggers.any {
                it.value is DataFetchingTrigger.ExerciseFetchingTrigger &&
                        (it.value as DataFetchingTrigger.ExerciseFetchingTrigger).exerciseId == exerciseId
            }) {
//            _dataFetchingTriggers.add(
//                MutableStateFlow(
//                    DataFetchingTrigger.ExerciseFetchingTrigger(
//                        userId = cachedUserId,
//                        startDate = DateUtils.getCurrentDate().minusMonths(1),
//                        endDate = DateUtils.getCurrentDate(),
//                        exerciseId = exerciseId
//                    )
//                )
//            )
            viewModelScope.launch {
                userRepository.trackCategory(
                    userId = cachedUserId, category = TrackedCategory.ExerciseOneRepMax(
                        exerciseId = exerciseId,
                        exerciseName = _allTrainedExercises.value.first { it.exercise.id == exerciseId }.exercise.name
                    )
                )
            }

        }

    }

    private fun TrackedCategory.toDataFetchingTrigger(): DataFetchingTrigger =
        when (this) {
            is TrackedCategory.Calories -> DataFetchingTrigger.CaloriesFetchingTrigger(
                dataSourceId = this.dataSourceId,
                startDate = this.startDate,
                endDate = this.endDate,
                userId = cachedUserId
            )
            is TrackedCategory.ExerciseOneRepMax -> DataFetchingTrigger.ExerciseFetchingTrigger(
                dataSourceId = this.dataSourceId,
                startDate = this.startDate,
                endDate = this.endDate,
                userId = cachedUserId,
                exerciseId = this.exerciseId
            )
        }


    /**
     * Kotlin Flow's flatMapLatest() can collect a flow and flatMap it whenever it changes, but
     * it only works with 1 input flow.
     *
     * By wrapping inside this class all the different data objects that should triggers flatMapLatest()
     * when they change, developer can indirectly use flatMapLatest() with more than 1 input
     */
    private sealed class DataFetchingTrigger {
        abstract val dataSourceId: String
        abstract val userId: String
        abstract val startDate: LocalDate
        abstract val endDate: LocalDate
        abstract fun withUserId(userId: String): DataFetchingTrigger
        abstract fun withStartDate(startDate: LocalDate): DataFetchingTrigger
        abstract fun withEndDate(endDate: LocalDate): DataFetchingTrigger

        data class CaloriesFetchingTrigger(
            override val dataSourceId: String,
            override val userId: String,
            override val startDate: LocalDate,
            override val endDate: LocalDate,
        ) : DataFetchingTrigger() {
            override fun withUserId(userId: String) = this.copy(userId = userId)

            override fun withStartDate(startDate: LocalDate) = this.copy(startDate = startDate)

            override fun withEndDate(endDate: LocalDate) = this.copy(endDate = endDate)
        }

        data class ExerciseFetchingTrigger(
            override val dataSourceId: String,
            override val userId: String,
            override val startDate: LocalDate,
            override val endDate: LocalDate,
            val exerciseId: String
        ) : DataFetchingTrigger() {


            override fun withUserId(userId: String) = this.copy(userId = userId)

            override fun withStartDate(startDate: LocalDate) = this.copy(startDate = startDate)

            override fun withEndDate(endDate: LocalDate) = this.copy(endDate = endDate)
        }
    }

    sealed interface DataOutput {
        val startDate: LocalDate
        val endDate: LocalDate
        val dataSourceId: String
        val title: String

        object EmptyData : DataOutput {
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
            override val dataSourceId: String, override val title: String = "Calories"
        ) : DataOutput

        data class Exercise(
            override val startDate: LocalDate, override val endDate: LocalDate,
            override val dataSourceId: String, override val title: String
        ) : DataOutput
    }
}

