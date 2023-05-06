package com.haidoan.android.stren.feat.training.exercises

import androidx.lifecycle.SavedStateHandle
import com.haidoan.android.stren.core.repository.fake.FakeExercisesRepository
import com.haidoan.android.stren.core.testing.data.EXERCISES_TEST_DATA
import com.haidoan.android.stren.core.testing.util.MainDispatcherRule
import com.haidoan.android.stren.feat.trainining.exercises.detail.EXERCISE_ID_ARG
import com.haidoan.android.stren.feat.trainining.exercises.detail.ExerciseDetailViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ExerciseDetailViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: ExerciseDetailViewModel
    private lateinit var fakeExercisesRepository: FakeExercisesRepository


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun currentExercise_viewModelInitWithSavedState_hasData() = runTest {
        var savedState = SavedStateHandle(mapOf(EXERCISE_ID_ARG to EXERCISES_TEST_DATA.first().id))
        fakeExercisesRepository = FakeExercisesRepository(this.backgroundScope)
        viewModel = ExerciseDetailViewModel(savedState, fakeExercisesRepository)

        var collectJob: Job =
            launch(UnconfinedTestDispatcher()) { viewModel.currentExercise.collect() }

        EXERCISES_TEST_DATA.forEach {
            // Cancel previous job for safety
            collectJob.cancel()

            // Calling set() on savedState is not enough, need to reinitialize it
            // for each id to test
            savedState = SavedStateHandle(mapOf(EXERCISE_ID_ARG to it.id))
            viewModel = ExerciseDetailViewModel(savedState, fakeExercisesRepository)

            collectJob = launch(UnconfinedTestDispatcher()) { viewModel.currentExercise.collect() }
            assertEquals(it, viewModel.currentExercise.value)
        }

        collectJob.cancel()
    }
}