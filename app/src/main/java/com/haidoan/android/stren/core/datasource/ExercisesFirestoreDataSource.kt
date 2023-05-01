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

    override fun getExercisesWithLimitAsQuery(limit: Long): Query =
        collection.orderBy("name", Query.Direction.ASCENDING)
            .limit(limit)


    //TODO: Use full-text search
    /**
     * Currently this can only search by prefix, so if [exerciseName] is "Bench",
     * "Bench Press A B", "Bench Something" will be returned" but not "Something Bench"
     *
     * \uf8ff is a special character that's after most character, which allows this
     * query to catch all values that starts with [exerciseName]
     *
     * [Reference](https://stackoverflow.com/questions/46568142/google-firestore-query-on-substring-of-a-property-value-text-search)
     */
    override fun getExercisesByNameAsQuery(exerciseName: String, resultCountLimit: Long): Query =
        collection.whereGreaterThanOrEqualTo("name", exerciseName)
            .whereLessThanOrEqualTo("name", "$exerciseName\\uf8ff")
            .orderBy("name", Query.Direction.ASCENDING)
            .limit(resultCountLimit)
}