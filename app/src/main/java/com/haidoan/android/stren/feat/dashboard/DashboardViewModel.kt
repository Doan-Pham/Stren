package com.haidoan.android.stren.feat.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haidoan.android.stren.core.designsystem.component.toCharEntries
import com.haidoan.android.stren.core.designsystem.component.toCharEntryModelProducer
import com.haidoan.android.stren.core.repository.base.EatingDayRepository
import com.haidoan.android.stren.core.service.AuthenticationService
import com.haidoan.android.stren.core.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

private const val UNDEFINED_USER_ID = "Undefined User ID"

@HiltViewModel
internal class DashboardViewModel @Inject constructor(
    authenticationService: AuthenticationService,
    private val eatingDayRepository: EatingDayRepository
) : ViewModel() {
    /**
     * Need to initialize userId before init{} block, or the init{} block will access
     * it when it's null and causes NullPointerException
     */
    private val _dataFetchingTriggers: MutableStateFlow<DataFetchingTriggers> =
        MutableStateFlow(
            DataFetchingTriggers(
                userId = UNDEFINED_USER_ID,
                startDate = DateUtils.getCurrentDate(),
                endDate = DateUtils.getCurrentDate()
            )
        )

    val chartEntryModelProducer =
        listOf(DateUtils.getCurrentDate() to 0f).toCharEntryModelProducer()

    init {
        authenticationService.addAuthStateListeners(
            onUserAuthenticated = { userId ->
                _dataFetchingTriggers.value = _dataFetchingTriggers.value.copy(userId = userId)
                Timber.d("authStateListen - User signed in - userId: $userId")
            },
            onUserNotAuthenticated = {
                _dataFetchingTriggers.value =
                    _dataFetchingTriggers.value.copy(userId = UNDEFINED_USER_ID)
                Timber.d("authStateListen - User signed out")
            })


    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState = _dataFetchingTriggers.flatMapLatest { triggers ->
        if (triggers.userId != UNDEFINED_USER_ID) {
            eatingDayRepository.getCaloriesOfDatesStream(
                userId = triggers.userId,
                startDate = triggers.startDate,
                endDate = triggers.endDate
            ).map {
                val chartData = it.map { caloriesOfDate ->
                    Pair(
                        caloriesOfDate.date,
                        caloriesOfDate.calories.toFloat()
                    )
                }
                Timber.d("chartData: $chartData")
                modifyChartEntries(chartData)
                DataType.Calories(
                    startDate = triggers.startDate,
                    endDate = triggers.endDate
                )
            }
        } else flowOf(
            DataType.Calories(
                startDate = DateUtils.getCurrentDate(),
                endDate = DateUtils.getCurrentDate()
            )
        )
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), DataType.Calories(
            startDate = DateUtils.getCurrentDate(),
            endDate = DateUtils.getCurrentDate()
        )
    )

    fun modifyChartEntries(entriesData: List<Pair<LocalDate, Float>>) {
        chartEntryModelProducer.setEntries(entriesData.toCharEntries())
    }

    fun updateDateRange(startDate: LocalDate, endDate: LocalDate) {
        Timber.d("startDate: $startDate, endDate: $endDate")
        _dataFetchingTriggers.value =
            _dataFetchingTriggers.value.copy(startDate = startDate, endDate = endDate)
    }

    /**
     * Kotlin Flow's flatMapLatest() can collect a flow and flatMap it whenever it changes, but
     * it only works with 1 input flow.
     *
     * By wrapping inside this class all the different data objects that should triggers flatMapLatest()
     * when they change, developer can indirectly use flatMapLatest() with more than 1 input
     */
    private data class DataFetchingTriggers(
        val userId: String,
        val startDate: LocalDate,
        val endDate: LocalDate
    )

    sealed interface DataType {
        val startDate: LocalDate
        val endDate: LocalDate

        data class Calories(override val startDate: LocalDate, override val endDate: LocalDate) :
            DataType
    }
}

