package com.haidoan.android.stren.core.datasource.remote.impl

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.snapshots
import com.haidoan.android.stren.core.datasource.remote.base.UserRemoteDataSource
import com.haidoan.android.stren.core.model.TrackedCategory
import com.haidoan.android.stren.core.model.TrackedCategoryType
import com.haidoan.android.stren.core.model.User
import com.haidoan.android.stren.core.utils.DateUtils.toLocalDate
import com.haidoan.android.stren.core.utils.DateUtils.toTimeStampDayStart
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

private const val USER_COLLECTION_PATH = "User"

class UserFirestoreDataSource @Inject constructor() : UserRemoteDataSource {
    private val collectionReference =
        FirebaseFirestore.getInstance().collection(USER_COLLECTION_PATH)

    override fun getUserStream(userId: String): Flow<User> =
        collectionReference.document(userId).snapshots().map { it.toUser() }

    override suspend fun trackCategory(userId: String, category: TrackedCategory) {
        collectionReference.document(userId)
            .update("trackedCategories", FieldValue.arrayUnion(category.toFirestoreObject()))
    }

    private fun TrackedCategory.toFirestoreObject() = when (this) {
        is TrackedCategory.Calories -> {
            mapOf(
                "dataSourceId" to this.dataSourceId,
                "categoryType" to this.categoryType,
                "startDate" to this.startDate.toTimeStampDayStart(),
                "endDate" to this.endDate.toTimeStampDayStart(),
            )
        }
        is TrackedCategory.ExerciseOneRepMax -> {
            mapOf(
                "dataSourceId" to this.dataSourceId,
                "categoryType" to this.categoryType,
                "startDate" to this.startDate.toTimeStampDayStart(),
                "endDate" to this.endDate.toTimeStampDayStart(),
                "exerciseId" to this.exerciseId
            )
        }
    }

    private fun DocumentSnapshot.toUser(): User {
        Timber.d("document: $this")

        @Suppress("UNCHECKED_CAST")
        val trackedCategoriesRawData = this.get("trackedCategories") as List<Map<String, Any>>
        val trackedCategories = mutableListOf<TrackedCategory>()

        for (category in trackedCategoriesRawData) {
            val dataSourceId = category["dataSourceId"] as String
            val startDate = (category["startDate"] as Timestamp).toLocalDate()
            val endDate = (category["endDate"] as Timestamp).toLocalDate()
            when (category["categoryType"]) {
                TrackedCategoryType.CALORIES.name -> trackedCategories.add(
                    TrackedCategory.Calories(
                        dataSourceId = dataSourceId,
                        startDate = startDate,
                        endDate = endDate
                    )
                )
                TrackedCategoryType.EXERCISE_1RM.name -> trackedCategories.add(
                    TrackedCategory.ExerciseOneRepMax(
                        dataSourceId = dataSourceId,
                        startDate = startDate,
                        endDate = endDate,
                        exerciseId = category["exerciseId"] as String,
                        exerciseName = (category["exerciseName"] as String?) ?: ""
                    )
                )
            }
        }
        return User(
            id = this.id,
            email = this["email"] as String,
            trackedCategories = trackedCategories
        )
    }
}