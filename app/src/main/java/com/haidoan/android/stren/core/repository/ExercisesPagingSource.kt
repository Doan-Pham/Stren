package com.haidoan.android.stren.core.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.haidoan.android.stren.core.datasource.ExercisesRemoteDataSource
import com.haidoan.android.stren.core.model.Exercise

import kotlinx.coroutines.tasks.await

class ExercisesPagingSource(
    private val dataSource: ExercisesRemoteDataSource,
    private val pageSize: Long
) :
    PagingSource<QuerySnapshot, Exercise>() {

    override fun getRefreshKey(state: PagingState<QuerySnapshot, Exercise>): QuerySnapshot? = null

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Exercise> {
        return try {
            val exercisesQuery: Query = dataSource.getExercisesWithLimitAsQuery(pageSize)
            val currentPage = params.key ?: exercisesQuery.get().await()
            val lastVisibleExercise = currentPage.documents[currentPage.size() - 1]
            val nextPage = exercisesQuery.startAfter(lastVisibleExercise).get().await()
            LoadResult.Page(
                data = currentPage.toExerciseList(),
                prevKey = null,
                nextKey = nextPage
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    private fun QuerySnapshot.toExerciseList() = this.mapNotNull { document ->
        @Suppress("UNCHECKED_CAST")
        (Exercise(
            document.id,
            document.getString("name") ?: "",
            document.get("instructions") as List<String>,
            document.get("images") as List<String>,
            document.getString("equipment") ?: document.getString("category") ?: "",
            document.get("primaryMuscles") as List<String>
        ))

    }
}