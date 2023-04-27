package com.haidoan.android.stren.core.repository

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.ktx.Firebase
import com.haidoan.android.stren.core.model.Exercise
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val EXERCISE_COLLECTION_PATH = "Exercise"

class ExercisesRepositoryFirestoreImpl @Inject constructor() :
    ExercisesRepository {
    private val firestore = Firebase.firestore
    private val exerciseCollection = firestore.collection(EXERCISE_COLLECTION_PATH)

    override fun getAllExercisesStream(): Flow<List<Exercise>> =
        exerciseCollection.snapshots().map { it.toObjects(Exercise::class.java) }

}