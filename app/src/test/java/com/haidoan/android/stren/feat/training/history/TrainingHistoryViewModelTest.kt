package com.haidoan.android.stren.feat.training.history

import com.haidoan.android.stren.core.repository.fake.FakeWorkoutsRepository
import com.haidoan.android.stren.core.service.FakeAuthenticationServiceImpl
import com.haidoan.android.stren.core.testing.WORKOUTS_TEST_DATA
import com.haidoan.android.stren.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TrainingHistoryViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var viewModel: TrainingHistoryViewModel
    private lateinit var fakeAuthenticationService: FakeAuthenticationServiceImpl
    private lateinit var fakeWorkoutsRepository: FakeWorkoutsRepository


    @Test
    fun uiState_userIdNotLoaded_isLoading() = runTest {
        fakeWorkoutsRepository = FakeWorkoutsRepository()
        fakeAuthenticationService = FakeAuthenticationServiceImpl()
        viewModel = TrainingHistoryViewModel(fakeAuthenticationService, fakeWorkoutsRepository)

        Assert.assertEquals(TrainingHistoryUiState.Loading, viewModel.uiState.value)

        fakeAuthenticationService.setUserId(UNDEFINED_USER_ID)
        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }
        Assert.assertEquals(TrainingHistoryUiState.Loading, viewModel.uiState.value)
        job.cancel()
    }

    @Test
    fun uiState_userIdOrCurrentDateChanged_correctWorkoutsShown() = runTest {
        fakeWorkoutsRepository = FakeWorkoutsRepository()
        fakeAuthenticationService = FakeAuthenticationServiceImpl()
        viewModel = TrainingHistoryViewModel(fakeAuthenticationService, fakeWorkoutsRepository)

        val randomUserId = "abc"

        fakeWorkoutsRepository.setWorkouts(randomUserId, WORKOUTS_TEST_DATA)
        fakeAuthenticationService.setUserId(randomUserId)

        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }

        WORKOUTS_TEST_DATA.forEach { workout ->
            viewModel.selectDate(workout.date)
            Assert.assertEquals(
                TrainingHistoryUiState.LoadComplete(randomUserId,
                    WORKOUTS_TEST_DATA.filter { it.date.isEqual(workout.date) }, workout.date,
                    WORKOUTS_TEST_DATA.map { it.date }
                ),
                viewModel.uiState.value
            )

        }
        job.cancel()
    }
}