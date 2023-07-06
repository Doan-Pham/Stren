package com.haidoan.android.stren.data

import com.haidoan.android.stren.core.datasource.remote.fake.FakeExercisesRemoteDataSource
import com.haidoan.android.stren.core.repository.base.ExercisesRepository
import com.haidoan.android.stren.core.testing.EXERCISE_CATEGORIES_TEST_DATA
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ExercisesRepositoryTest {
    lateinit var subjectUnderTest: ExercisesRepository
    lateinit var fakeRemoteDataSource: FakeExercisesRemoteDataSource

    @Before
    fun setup() {
        fakeRemoteDataSource = FakeExercisesRemoteDataSource()
        // subjectUnderTest = ExercisesRepositoryImpl(fakeRemoteDataSource)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getAllExerciseCategories_dataSourceLoaded_dataReceived() = runTest {
        var fakeData = EXERCISE_CATEGORIES_TEST_DATA
        fakeRemoteDataSource.setExerciseCategories(fakeData)
        var exerciseCategories = subjectUnderTest.getAllExerciseCategories().toList().flatten()
        assertEquals(fakeData, exerciseCategories)

        fakeData = emptyList()
        fakeRemoteDataSource.setExerciseCategories(fakeData)
        exerciseCategories = subjectUnderTest.getAllExerciseCategories().toList().flatten()
        assertEquals(fakeData, exerciseCategories)
    }

}
