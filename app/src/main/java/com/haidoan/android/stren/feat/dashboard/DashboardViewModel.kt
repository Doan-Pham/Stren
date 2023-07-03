package com.haidoan.android.stren.feat.dashboard

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haidoan.android.stren.core.designsystem.component.toCharEntries
import com.haidoan.android.stren.core.designsystem.component.toCharEntryModelProducer
import com.haidoan.android.stren.core.domain.GetUserFullDataUseCase
import com.haidoan.android.stren.core.model.BiometricsRecord
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

    private val _exercisesToTrack = MutableStateFlow<List<TrainedExercise>>(listOf())

    //TODO: This only works for exercises' 1RM and not other exercises-related metrics
    val exercisesToTrack = _exercisesToTrack.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        listOf()
    )

    private val _biometricsToTrack = MutableStateFlow<List<BiometricsRecord>>(listOf())
    val biometricsToTrack =
        _biometricsToTrack.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf())

    val chartEntryModelProducers = mutableStateMapOf<String, ChartEntryModelProducer>()

    /**
     * Use Jetpack Compose's SnapshotStateList to localize changes to tracked categories, which means
     * that addition, deletion, update of 1 tracked category won't force all other categories to be reset
     */
    val dataOutputsStreams = mutableStateListOf<StateFlow<DataOutput>>()

    /**
     * This val is solely for triggering state flows collection by using collectAsStateWithLifeCycle() in screens and nothing more. The actual data will be contained in [dataOutputsStreams]
     */
    val dataOutputsCentralStateFlow = currentUserId.flatMapLatest { userId ->
        if (userId == UNDEFINED_USER_ID) return@flatMapLatest flowOf(listOf())
        getUserFullDataUseCase(userId).map { user ->
            cachedTrackedCategories = user.trackedCategories
            Timber.d("getUserFullDataUseCase() - trackedCategories: $cachedTrackedCategories")

            val dataOutputsStateFlows = user.trackedCategories.map { category ->
                val rawDataFlow = when (category) {
                    is TrackedCategory.Calories -> {
                        eatingDayRepository.getCaloriesOfDatesStream(
                            userId = userId,
                            startDate = category.startDate,
                            endDate = category.endDate
                        ).mapLatest { caloriesOfDates ->
                            caloriesOfDates.associate { it.date to it.calories.toFloat() }
                        }
                    }
                    is TrackedCategory.ExerciseOneRepMax -> {
                        workoutsRepository.getExerciseOneRepMaxesStream(
                            userId = userId,
                            startDate = category.startDate,
                            endDate = category.endDate,
                            exerciseId = category.exerciseId
                        )
                    }
                    is TrackedCategory.Biometrics -> {
                        userRepository.getBiometricsRecordsStreamById(
                            userId = userId,
                            biometricsId = category.biometricsId,
                            startDate = category.startDate,
                            endDate = category.endDate
                        ).mapLatest { records ->
                            Timber.d("getBiometricsRecordsStream() - records: $records")
                            records.associate { it.recordDate to it.value }
                        }
                    }
                }

                rawDataFlow.flatMapLatest {
                    convertTrackedDataToOutputFlow(category = category, rawData = it)
                }.stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(5000),
                    DataOutput.EmptyData(
                        startDate = category.startDate,
                        endDate = category.endDate,
                        dataSourceId = category.dataSourceId
                    )
                )
            }
            resolveToState(dataOutputsStateFlows)
            dataOutputsStateFlows
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


    fun refreshAllTrainedExercises() {
        viewModelScope.launch {
            val allTrainedExercises =
                workoutsRepository.getAllExercisesTrained(userId = currentUserId.value)
            val alreadyTrackedExercisesIds =
                cachedTrackedCategories.filterIsInstance(TrackedCategory.ExerciseOneRepMax::class.java)
                    .map {
                        it.exerciseId
                    }
            _exercisesToTrack.update { allTrainedExercises.filter { it.exercise.id !in alreadyTrackedExercisesIds } }

            Timber.d(
                "_exercisesToTrack.value: ${
                    _exercisesToTrack.value.map { it.exercise.name }
                }"
            )
        }
    }

    fun refreshAllBiometrics() {
        viewModelScope.launch {
            val allBiometrics =
                userRepository.getAllBiometricsToTrack(userId = currentUserId.value)
            val alreadyTrackedBiometricsIds =
                cachedTrackedCategories
                    .filterIsInstance(TrackedCategory.Biometrics::class.java)
                    .map { it.biometricsId }
            _biometricsToTrack.update { allBiometrics.filter { it.biometricsId !in alreadyTrackedBiometricsIds } }

            Timber.d(
                "_biometricsToTrack.value: ${
                    _biometricsToTrack.value.map { it.biometricsName }
                }"
            )
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
                        exerciseName = _exercisesToTrack.value.first { it.exercise.id == exerciseId }.exercise.name,
                        isDefaultCategory = false
                    )
                )
            }
        }
    }

    fun trackBiometrics(biometricsId: String) {
        if (!cachedTrackedCategories.any {
                it is TrackedCategory.Biometrics && it.biometricsId == biometricsId
            }) {
            viewModelScope.launch {
                userRepository.trackCategory(
                    userId = currentUserId.value,
                    category = TrackedCategory.Biometrics(
                        biometricsId = biometricsId,
                        biometricsName = biometricsToTrack.value.first { it.biometricsId == biometricsId }.biometricsName,
                        isDefaultCategory = false
                    )
                )
            }
        }
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

    fun stopTrackingCategory(dataSourceId: String) {
        viewModelScope.launch {
            userRepository.stopTrackingCategory(
                userId = currentUserId.value,
                dataSourceId = dataSourceId
            )
        }
    }


    private fun convertTrackedDataToOutputFlow(
        category: TrackedCategory,
        rawData: Map<LocalDate, Float>
    ): Flow<DataOutput> {
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

        return flowOf(
            when (category) {
                is TrackedCategory.Calories -> {
                    DataOutput.Calories(
                        startDate = category.startDate,
                        endDate = category.endDate,
                        dataSourceId = category.dataSourceId,
                        isDefaultCategory = category.isDefaultCategory
                    )
                }
                is TrackedCategory.ExerciseOneRepMax -> {
                    DataOutput.Exercise(
                        startDate = category.startDate,
                        endDate = category.endDate,
                        dataSourceId = category.dataSourceId,
                        title = category.exerciseName,
                        isDefaultCategory = category.isDefaultCategory,
                    )
                }
                is TrackedCategory.Biometrics -> {
                    DataOutput.Biometrics(
                        startDate = category.startDate,
                        endDate = category.endDate,
                        dataSourceId = category.dataSourceId,
                        title = category.biometricsName,
                        isDefaultCategory = category.isDefaultCategory,
                    )
                }
            }

        )
    }

    private fun ChartEntryModelProducer.updateChartEntries(
        entriesData: List<Pair<LocalDate, Float>>
    ) {
        this.setEntries(entriesData.toCharEntries())
    }

    private fun resolveToState(newValue: List<StateFlow<DataOutput>>) {
        Timber.d("resolveToState() - testDataOutputs - before All: ${dataOutputsStreams.toList()}")
        Timber.d("resolveToState() - newValue: $newValue")

        val newValueDataSourceIds = newValue.map { it.value.dataSourceId }.toSet()
        Timber.d("resolveToState() - newValue - datasourceId: $newValueDataSourceIds")
        Timber.d("resolveToState() - newValue - values: ${newValue.map { it.value }}")

        if (dataOutputsStreams.isEmpty()) {
            dataOutputsStreams.addAll(newValue)
            return
        }

        val iterator = dataOutputsStreams.iterator()
        while (iterator.hasNext()) {
            val curStateFlow = iterator.next()
            if (curStateFlow.value.dataSourceId !in newValueDataSourceIds) {
                dataOutputsStreams.remove(curStateFlow)
            }
        }
        val oldDataOutputDataSources =
            dataOutputsStreams.associate { it.value.dataSourceId to it.value }

        newValue.forEachIndexed { index, curStateFlow ->
            Timber.d("resolveToState() - curStateFlow: $curStateFlow")
            Timber.d("resolveToState() - testDataOutputs - before: ${dataOutputsStreams.toList()}")
            val curDataOutput = curStateFlow.value
            if (curDataOutput.dataSourceId !in oldDataOutputDataSources.keys) {
                dataOutputsStreams.add(index, curStateFlow)
            } else if (!curDataOutput.isEqualTo(oldDataOutputDataSources[curDataOutput.dataSourceId])) {
                Timber.d("resolveToState() - curDataOutput: $curDataOutput")
                Timber.d("resolveToState() - testDataOutPut: ${oldDataOutputDataSources[curDataOutput.dataSourceId]}")
                dataOutputsStreams.removeAt(index)
                dataOutputsStreams.add(index, curStateFlow)
            }
            Timber.d("resolveToState() - testDataOutputs - after: ${dataOutputsStreams.toList()}")
        }

        Timber.d("resolveToState() - testDataOutputs - After all: ${dataOutputsStreams.toList()}")
    }

    sealed interface DataOutput {
        val isDefaultCategory: Boolean
        val startDate: LocalDate
        val endDate: LocalDate
        val dataSourceId: String
        val title: String

        fun isEqualTo(other: DataOutput?) =
            if (other == null) false
            else this.dataSourceId == other.dataSourceId &&
                    this.startDate.isEqual(other.startDate) &&
                    this.endDate.isEqual(other.endDate)

        data class EmptyData(
            override val startDate: LocalDate = DateUtils.getCurrentDate(),
            override val endDate: LocalDate = DateUtils.getCurrentDate(),
            override val dataSourceId: String,
            override val title: String = "",
            override val isDefaultCategory: Boolean = true
        ) : DataOutput

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

        data class Biometrics(
            override val startDate: LocalDate, override val endDate: LocalDate,
            override val dataSourceId: String, override val title: String,
            override val isDefaultCategory: Boolean
        ) : DataOutput
    }
}