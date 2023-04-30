package com.haidoan.android.stren.core.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.google.firebase.firestore.QuerySnapshot
import com.haidoan.android.stren.core.model.Exercise
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

const val EXERCISE_COLLECTION_PATH = "Exercise"

class ExercisesRepositoryFirestoreImpl @Inject constructor(
    private val dataSource: FirestorePagingSource,
    private val config: PagingConfig
) : ExercisesRepository {

    override fun getAllExercisesStream() = Pager(config = config) { dataSource }.flow

    fun Flow<QuerySnapshot>.toExerciseList() = this.mapNotNull {
        it.documents.mapNotNull { document ->
            @Suppress("UNCHECKED_CAST")
            Exercise(
                document.id,
                document.getString("name") ?: "",
                document.get("instructions") as List<String>,
                document.get("images") as List<String>,
                document.getString("equipment") ?: document.getString("category") ?: "",
                document.get("primaryMuscles") as List<String>
            )
        }
    }
}