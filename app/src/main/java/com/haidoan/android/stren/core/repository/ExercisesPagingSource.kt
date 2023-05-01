package com.haidoan.android.stren.core.repository

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.haidoan.android.stren.core.model.Exercise
import kotlinx.coroutines.tasks.await

private const val TAG = "ExercisesPagingSource"

class ExercisesPagingSource(
    private val query: Query
) :
    PagingSource<QuerySnapshot, Exercise>() {

    override fun getRefreshKey(state: PagingState<QuerySnapshot, Exercise>): QuerySnapshot? = null

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Exercise> {
        return try {
            val currentPage = params.key ?: query.get().await()
            var lastVisibleExercise: DocumentSnapshot? = null

            if (currentPage.size() > 0) {
                lastVisibleExercise = currentPage.documents[currentPage.size() - 1]
                Log.d(TAG, "lastVisibleExercise: ${lastVisibleExercise.get("name")}")
            }

            val nextPage =
                if (lastVisibleExercise != null) query.startAfter(lastVisibleExercise).get()
                    .await() else null

            LoadResult.Page(
                data = currentPage.toExerciseList(),
                prevKey = null,
                nextKey = nextPage
            )
        } catch (e: Exception) {
            Log.e(TAG, "Loading Page Error: $e")
            LoadResult.Error(e)
        }
    }

    private fun QuerySnapshot.toExerciseList() = this.mapNotNull { document ->
        Log.d(TAG, "toExerciseList() - document: $document")
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