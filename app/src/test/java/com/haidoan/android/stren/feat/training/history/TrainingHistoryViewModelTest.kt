package com.haidoan.android.stren.feat.training.history

import com.haidoan.android.stren.core.repository.fake.FakeWorkoutsRepository
import com.haidoan.android.stren.core.service.FakeAuthenticationServiceImpl
import com.haidoan.android.stren.core.testing.data.WORKOUTS_TEST_DATA
import com.haidoan.android.stren.core.testing.util.MainDispatcherRule
import com.haidoan.android.stren.core.utils.DateUtils
import com.haidoan.android.stren.feat.trainining.history.TrainingHistoryUiState
import com.haidoan.android.stren.feat.trainining.history.TrainingHistoryViewModel
import com.haidoan.android.stren.feat.trainining.history.UNDEFINED_USER_ID
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
    fun uiState_userIdLoaded_showWorkouts() = runTest {
        fakeWorkoutsRepository = FakeWorkoutsRepository()
        fakeAuthenticationService = FakeAuthenticationServiceImpl()
        viewModel = TrainingHistoryViewModel(fakeAuthenticationService, fakeWorkoutsRepository)

        val randomUserId = "abc"

        // Must set fakeWorkoutsRepository before fakeAuthenticationService, since
        // changes in the latter will trigger WorkoutViewModel to call the former
        // (in which case it should already have the data)
        fakeWorkoutsRepository.setWorkouts(randomUserId, WORKOUTS_TEST_DATA)
        fakeAuthenticationService.setUserId(randomUserId)

        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }
        Assert.assertEquals(
            TrainingHistoryUiState.LoadComplete(
                WORKOUTS_TEST_DATA,
                DateUtils.getCurrentDate()
            ), viewModel.uiState.value
        )
        job.cancel()
    }

}