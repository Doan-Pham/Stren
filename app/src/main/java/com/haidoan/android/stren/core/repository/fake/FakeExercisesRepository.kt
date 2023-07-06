package com.haidoan.android.stren.core.repository.fake

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.testing.asPagingSourceFactory
import com.haidoan.android.stren.core.model.Exercise
import com.haidoan.android.stren.core.model.ExerciseCategory
import com.haidoan.android.stren.core.model.ExerciseQueryParameters
import com.haidoan.android.stren.core.model.MuscleGroup
import com.haidoan.android.stren.core.repository.base.ExercisesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import timber.log.Timber

class FakeExercisesRepository(testScope: CoroutineScope) : ExercisesRepository {
    // Backing MutableFlow for testing
    private val _exercisesFlow: MutableSharedFlow<List<Exercise>> =
        MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    fun setExercises(exercises: List<Exercise>) {
        _exercisesFlow.tryEmit(exercises)
    }

    private val exercisesFlow = _exercisesFlow

    private val pagingSourceFactory =
        exercisesFlow.asPagingSourceFactory(coroutineScope = testScope)

    override fun getExercisesWithLimit(limit: Long): Flow<PagingData<Exercise>> =
        Pager(config = PagingConfig(20), pagingSourceFactory = { pagingSourceFactory() }).flow

    override fun getExercisesByNameWithLimit(
        exerciseName: String,
        limit: Long
    ): Flow<PagingData<Exercise>> {
        TODO("Not yet implemented")
    }

    override fun getAllExerciseCategories(): Flow<List<ExerciseCategory>> {
        TODO("Not yet implemented")
    }

    override fun getAllMuscleGroups(): Flow<List<MuscleGroup>> {
        TODO("Not yet implemented")
    }

    override fun searchExercises(
        filterStandards: ExerciseQueryParameters,
        resultCountLimit: Long
    ): Flow<PagingData<Exercise>> {
        TODO("Not yet implemented")
    }

    private val _exerciseFlow = flowOf(com.haidoan.android.stren.util.EXERCISES_TEST_DATA)


    override fun getExerciseById(exerciseId: String): Flow<Exercise> =
        _exerciseFlow.map { exercises ->
            Timber.d("exercises: $exercises;exerciseId: $exerciseId ")
            exercises.first { it.id == exerciseId }
        }

    override suspend fun getExercisesByIds(exerciseIds: List<String>): List<Exercise> {
        TODO("Not yet implemented")
    }

    override suspend fun createCustomExercise(userId: String, exercise: Exercise) {
        TODO("Not yet implemented")
    }
}
