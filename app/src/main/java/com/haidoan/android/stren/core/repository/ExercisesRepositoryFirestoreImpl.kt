package com.haidoan.android.stren.core.repository

import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.ktx.Firebase
import com.haidoan.android.stren.core.model.Exercise
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

private const val EXERCISE_COLLECTION_PATH = "Exercise"

class ExercisesRepositoryFirestoreImpl @Inject constructor() :
    ExercisesRepository {

    private val firestore = Firebase.firestore

    init {
        firestore.firestoreSettings =
            FirebaseFirestoreSettings.Builder().setPersistenceEnabled(false).build()
    }

    override fun getAllExercisesStream(): Flow<List<Exercise>> =
        firestore.collection(EXERCISE_COLLECTION_PATH).snapshots().toExerciseList()

    private fun Flow<QuerySnapshot>.toExerciseList() = this.mapNotNull {
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