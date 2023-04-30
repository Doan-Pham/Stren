package com.haidoan.android.stren.core.datasource

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import javax.inject.Inject

const val EXERCISE_COLLECTION_PATH = "Exercise"

class ExercisesFirestoreDataSource @Inject constructor() : ExercisesRemoteDataSource {
    private var collection: CollectionReference

    init {
        val firestore = Firebase.firestore
        firestore.firestoreSettings =
            FirebaseFirestoreSettings.Builder().setPersistenceEnabled(false).build()
        collection = firestore.collection(EXERCISE_COLLECTION_PATH)
    }

    override suspend fun getExercisesWithLimitAsQuery(limit: Long): Query =
        collection.orderBy("name", Query.Direction.ASCENDING)
            .limit(limit)
}