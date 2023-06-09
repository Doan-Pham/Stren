package com.haidoan.android.stren.feat.training.exercises

import androidx.paging.PagingData
import androidx.paging.testing.asSnapshot
import com.haidoan.android.stren.core.model.Exercise
import com.haidoan.android.stren.core.repository.fake.FakeExercisesRepository
import com.haidoan.android.stren.core.service.FakeAuthenticationServiceImpl
import com.haidoan.android.stren.core.testing.EXERCISES_TEST_DATA
import com.haidoan.android.stren.feat.training.exercises.view_exercises.ExercisesViewModel
import com.haidoan.android.stren.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class ExercisesViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: ExercisesViewModel
    private lateinit var fakeExercisesRepository: FakeExercisesRepository

    @Test
    fun exercises_exercisesLoadedFromDataLayer_viewModelUpdated() = runTest {
        // Since the flow in repository may outlive ViewModel, needs to pass
        // backgroundScope
        fakeExercisesRepository = FakeExercisesRepository(this.backgroundScope)
        viewModel = ExercisesViewModel(
            authenticationService = FakeAuthenticationServiceImpl(),
            exercisesRepository = fakeExercisesRepository
        )

        val exercises: Flow<PagingData<Exercise>> = viewModel.exercises

        fakeExercisesRepository.setExercises(EXERCISES_TEST_DATA)
        var snapshot: List<Exercise> = exercises.asSnapshot {}
        assertEquals(snapshot, EXERCISES_TEST_DATA)

        fakeExercisesRepository.setExercises(emptyList())
        snapshot = exercises.asSnapshot {}
        assertEquals(snapshot, emptyList<Exercise>())

    }
}