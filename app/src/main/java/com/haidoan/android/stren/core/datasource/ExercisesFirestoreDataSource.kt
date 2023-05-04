package com.haidoan.android.stren.core.datasource

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.haidoan.android.stren.core.model.ExerciseCategory
import com.haidoan.android.stren.core.model.ExerciseFilterStandards
import com.haidoan.android.stren.core.model.MuscleGroup
import com.haidoan.android.stren.core.repository.ExerciseExtraFilter
import com.haidoan.android.stren.core.repository.QueryWrapper
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

const val EXERCISE_COLLECTION_PATH = "Exercise"
const val EXERCISE_CATEGORY_COLLECTION_PATH = "ExerciseCategory"
const val MUSCLE_GROUP_COLLECTION_PATH = "MuscleGroup"

class ExercisesFirestoreDataSource @Inject constructor() : ExercisesRemoteDataSource {
    private var exerciseCollection: CollectionReference
    private var exerciseCategoryCollection: CollectionReference
    private var muscleGroupCollection: CollectionReference

    init {
        val firestore = Firebase.firestore
        firestore.firestoreSettings =
            FirebaseFirestoreSettings.Builder().setPersistenceEnabled(false).build()
        exerciseCollection = firestore.collection(EXERCISE_COLLECTION_PATH)
        exerciseCategoryCollection = firestore.collection(EXERCISE_CATEGORY_COLLECTION_PATH)
        muscleGroupCollection = firestore.collection(MUSCLE_GROUP_COLLECTION_PATH)
    }

    override fun getExercisesWithLimitAsQuery(limit: Long): Query =
        exerciseCollection.orderBy("name", Query.Direction.ASCENDING)
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
        exerciseCollection.whereGreaterThanOrEqualTo("name", exerciseName)
            .whereLessThanOrEqualTo("name", "$exerciseName\\uf8ff")
            .orderBy("name", Query.Direction.ASCENDING)
            .limit(resultCountLimit)

    override suspend fun getAllExerciseCategories(): List<ExerciseCategory> =
        exerciseCategoryCollection.get().await()
            .mapNotNull { it.toObject(ExerciseCategory::class.java) }


    override suspend fun getAllMuscleGroups(): List<MuscleGroup> =
        muscleGroupCollection.get().await()
            .mapNotNull { it.toObject(MuscleGroup::class.java) }

    /**
     * This query is very limited due to Firestore's query constraint
     */
    override fun getFilteredExercisesAsQuery(
        filterStandards: ExerciseFilterStandards,
        resultCountLimit: Long
    ): QueryWrapper {
        val query =
            exerciseCollection.whereGreaterThanOrEqualTo("name", filterStandards.exerciseName)
                .whereLessThanOrEqualTo("name", "${filterStandards.exerciseName}\\uf8ff")
                .orderBy("name", Query.Direction.ASCENDING)
                .limit(resultCountLimit)

        val categoriesToFilterBy = filterStandards.exerciseCategories.map { it.name }
        val muscleGroupsToFilterBy = filterStandards.muscleGroupsTrained.map { it.name }
//        Timber.d(TAG, "getFilteredExercisesAsQuery() - categoriesToFilterBy: $categoriesToFilterBy")
//
//        Timber.d(
//            TAG,
//            "getFilteredExercisesAsQuery() - muscleGroupsToFilterBy: $muscleGroupsToFilterBy"
//        )

        if (categoriesToFilterBy.isEmpty()) {
            return if (muscleGroupsToFilterBy.isEmpty()) {
                QueryWrapper(
                    query
                )
            } else {
                QueryWrapper(
                    query.whereArrayContainsAny(
                        "primaryMuscles",
                        muscleGroupsToFilterBy
                    )
                )
            }
        } else {
            return if (muscleGroupsToFilterBy.isEmpty()) {
                QueryWrapper(
                    query.whereIn("category", categoriesToFilterBy)
                )
            } else {
                QueryWrapper(
                    query.whereIn("category", categoriesToFilterBy),
                    ExerciseExtraFilter(muscleGroups = filterStandards.muscleGroupsTrained)
                )

            }
        }
    }
}