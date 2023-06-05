package com.haidoan.android.stren.core.datasource.remote.impl

import com.algolia.search.client.Index
import com.algolia.search.dsl.filters
import com.algolia.search.dsl.query
import com.algolia.search.helper.deserialize
import com.haidoan.android.stren.core.datasource.remote.base.ExercisesSearchDataSource
import com.haidoan.android.stren.core.datasource.remote.di.ExerciseIndex
import com.haidoan.android.stren.core.model.Exercise
import com.haidoan.android.stren.core.model.ExerciseFilterStandards
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExercisesAlgoliaDataSource @Inject constructor(
    @ExerciseIndex
    private val exerciseIndex: Index
) : ExercisesSearchDataSource {

    override suspend fun searchExercise(
        exerciseFilterStandards: ExerciseFilterStandards,
        dataPageSize: Long,
        dataPageIndex: Int
    ): List<Exercise> {
        val exerciseName = exerciseFilterStandards.exerciseName
        val trainedMuscleGroups = exerciseFilterStandards.muscleGroupsTrained
        val belongedCategories = exerciseFilterStandards.exerciseCategories
        Timber.d("searchExercise() - trainedMuscleGroups: $trainedMuscleGroups")
        Timber.d("searchExercise() - belongedCategories: $belongedCategories")
        val query = query(exerciseName) {
            filters {
                orFacet {
                    belongedCategories.forEach { exerciseCategory ->
                        facet("category", exerciseCategory.name)
                    }
                }
                orFacet {
                    trainedMuscleGroups.forEach { muscleGroup ->
                        facet("primaryMuscles", muscleGroup.name)
                    }
                }
            }
            page = dataPageIndex
            hitsPerPage = dataPageSize.toInt()
        }
        val result = exerciseIndex.search(query)
        Timber.d("searchExercise() - result: $result")
        Timber.d("searchExercise() - result deserialized: ${result.hits.deserialize(Exercise.serializer())}")
        return result.hits.deserialize(Exercise.serializer())
    }
}