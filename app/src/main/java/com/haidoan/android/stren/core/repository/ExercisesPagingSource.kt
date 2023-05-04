package com.haidoan.android.stren.core.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.haidoan.android.stren.core.model.Exercise
import com.haidoan.android.stren.core.model.ExerciseCategory
import com.haidoan.android.stren.core.model.MuscleGroup
import kotlinx.coroutines.tasks.await
import timber.log.Timber

private const val TAG = "ExercisesPagingSource"

internal class ExercisesPagingSource(
    private val queryWrapper: QueryWrapper
) :
    PagingSource<QuerySnapshot, Exercise>() {

    override fun getRefreshKey(state: PagingState<QuerySnapshot, Exercise>): QuerySnapshot? = null

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Exercise> {
        return try {
            val query = queryWrapper.query
            val categoriesToFilterBy = queryWrapper.extraFilter.categories.map { it.name }
            val muscleGroupsToFilterBy = queryWrapper.extraFilter.muscleGroups.map { it.name }

            val currentPage = params.key ?: query.get().await()
            var data: List<Exercise> = currentPage.toExerciseList()
            val documentIndexes = data.mapIndexed { index, exercise -> exercise to index }.toMap()

            Timber.d(TAG, "load() - query: ${query} ")
            Timber.d(TAG, "load() - extraFilter - categories: $categoriesToFilterBy ")
            Timber.d(TAG, "load() - extraFilter - muscleGroupsToFilterBy: $muscleGroupsToFilterBy ")
            Timber.d(TAG, "load() - data (before): $data ")

            if (categoriesToFilterBy.isNotEmpty()) {
                data = data.filter { exercise ->
                    categoriesToFilterBy.contains(exercise.belongedCategory)
                }
                Timber.d(TAG, "load() - data (filtering categories): $data ")
            }
            if (muscleGroupsToFilterBy.isNotEmpty()) {
                data = data.filter { exercise ->
                    Timber.d(
                        TAG,
                        "load() - data (Filtering muscles) - cur exercise: ${exercise.name};${exercise.trainedMuscleGroups}  "
                    )
                    exercise.trainedMuscleGroups.any { it in muscleGroupsToFilterBy }
                }
                Timber.d(TAG, "load() - data (After filtering muscles): $data ")
            }
            Timber.d(TAG, "load() - data (after): $data ")

            var lastVisibleExercise: DocumentSnapshot? = null

            if (data.isNotEmpty()) {
                lastVisibleExercise = currentPage.documents[documentIndexes[data.last()] ?: 0]
                Timber.d(TAG, "lastVisibleExercise: ${lastVisibleExercise.get("name")}")
            }

            val nextPage =
                if (lastVisibleExercise != null) query.startAfter(lastVisibleExercise).get()
                    .await() else null

            LoadResult.Page(
                data = data,
                prevKey = null,
                nextKey = nextPage
            )
        } catch (e: Exception) {
            Timber.e(TAG, "Loading Page Error: $e")
            LoadResult.Error(e)
        }
    }

    private fun QuerySnapshot.toExerciseList() = this.mapNotNull { document ->
        //Timber.d(TAG, "toExerciseList() - document: $document")
        @Suppress("UNCHECKED_CAST")
        (Exercise(
            document.id,
            document.getString("name") ?: "",
            document.get("instructions") as List<String>,
            document.get("images") as List<String>,
            document.getString("category") ?: "",
            document.get("primaryMuscles") as List<String>
        ))
    }
}

data class QueryWrapper(
    val query: Query,
    val extraFilter: ExerciseExtraFilter = ExerciseExtraFilter()
)

data class ExerciseExtraFilter(
    val muscleGroups: List<MuscleGroup> = listOf(),
    val categories: List<ExerciseCategory> = listOf()
)

fun Query.toQueryWrapper() = QueryWrapper(this, ExerciseExtraFilter())