package com.haidoan.android.stren.core.repository.fake

import com.haidoan.android.stren.core.model.Exercise
import com.haidoan.android.stren.core.repository.ExercisesRepository
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class FakeExercisesRepository : ExercisesRepository {
    // Backing MutableFlow for testing
    private val _exercisesStream: MutableSharedFlow<List<Exercise>> =
        MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    fun setExercises(exercises: List<Exercise>) {
        _exercisesStream.tryEmit(exercises)
    }

    override fun getAllExercisesStream(): Flow<List<Exercise>> = _exercisesStream
}