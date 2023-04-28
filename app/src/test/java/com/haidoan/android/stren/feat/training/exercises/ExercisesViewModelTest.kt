package com.haidoan.android.stren.feat.training.exercises

import com.haidoan.android.stren.core.repository.fake.FakeExercisesRepository
import com.haidoan.android.stren.core.testing.data.exercisesTestData
import com.haidoan.android.stren.core.testing.util.MainDispatcherRule
import com.haidoan.android.stren.feat.trainining.exercises.ExercisesUiState
import com.haidoan.android.stren.feat.trainining.exercises.ExercisesViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class ExercisesViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: ExercisesViewModel
    private lateinit var fakeExercisesRepository: FakeExercisesRepository

    @Before
    fun setUp() {
        fakeExercisesRepository = FakeExercisesRepository()
        viewModel = ExercisesViewModel(fakeExercisesRepository)
    }

    @Test
    fun uiState_initialValue_isLoading() = runTest {
        assertEquals(ExercisesUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun uiState_exercisesLoaded_isLoadComplete() = runTest {
        // Need to manually collect flow since StateFlow created with stateIn doesn't update unless there's collectors
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        fakeExercisesRepository.setExercises(exercisesTestData)
        assertEquals(ExercisesUiState.LoadComplete(exercisesTestData), viewModel.uiState.value)

        fakeExercisesRepository.setExercises(emptyList())
        assertEquals(ExercisesUiState.LoadComplete(emptyList()), viewModel.uiState.value)

        collectJob.cancel()
    }
}