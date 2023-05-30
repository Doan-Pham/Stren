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
import com.haidoan.android.stren.core.utils.DateUtils.getCurrentTimeAsTimestamp
import com.haidoan.android.stren.core.utils.DateUtils.toLocalDate
import com.haidoan.android.stren.core.utils.DateUtils.toTimeStampDayStart
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

private const val USER_COLLECTION_PATH = "User"

class UserFirestoreDataSource @Inject constructor() : UserRemoteDataSource {
    private val db = FirebaseFirestore.getInstance()
    private val collectionReference = db.collection(USER_COLLECTION_PATH)

    override fun getUserStream(userId: String): Flow<User> =
        collectionReference.document(userId).snapshots().map { it.toUser() }

    override suspend fun trackCategory(userId: String, category: TrackedCategory) {
        collectionReference.document(userId)
            .update("trackedCategories", FieldValue.arrayUnion(category.toFirestoreObject()))
    }

    override suspend fun updateTrackCategory(
        userId: String, dataSourceId: String, newStartDate: LocalDate, newEndDate: LocalDate
    ) {
        db.runTransaction { transaction ->
            val documentRef = collectionReference.document(userId)
            val snapshot = transaction.get(documentRef)
            Timber.d("snapshot: $snapshot")

            @Suppress("UNCHECKED_CAST")
            val oldValue =
                (snapshot["trackedCategories"] as List<Map<String, *>>)
                    .first { it.containsValue(dataSourceId) }
            Timber.d("oldValue: $oldValue")

            val newValue =
                snapshot.toUser().trackedCategories.first { it.dataSourceId == dataSourceId }
                    .withStartDate(newStartDate)
                    .withEndDate(newEndDate)
                    .toFirestoreObject()
                    .toMutableMap()

            // Updating a value shouldn't change the time at which it was first created
            newValue["createdAt"] = oldValue["createdAt"] as Timestamp

            transaction.update(documentRef, "trackedCategories", FieldValue.arrayRemove(oldValue))
            transaction.update(documentRef, "trackedCategories", FieldValue.arrayUnion(newValue))
        }.await()
    }

    override suspend fun stopTrackingCategory(
        userId: String, dataSourceId: String
    ) {
        db.runTransaction { transaction ->
            val documentRef = collectionReference.document(userId)
            val snapshot = transaction.get(documentRef)
            Timber.d("snapshot: $snapshot")

            @Suppress("UNCHECKED_CAST")
            val oldValue =
                (snapshot["trackedCategories"] as List<Map<String, *>>)
                    .first { it.containsValue(dataSourceId) }
            transaction.update(
                documentRef, "trackedCategories", FieldValue.arrayRemove(oldValue)
            )
        }.await()
    }

    override suspend fun getUser(userId: String): User =
        collectionReference.document(userId).get().await().toUser()

    private fun TrackedCategory.toFirestoreObject(): Map<String, Any> {
        val result = mutableMapOf<String, Any>()
        result["dataSourceId"] = this.dataSourceId
        result["categoryType"] = this.categoryType
        result["startDate"] = this.startDate.toTimeStampDayStart()
        result["endDate"] = this.endDate.toTimeStampDayStart()
        result["createdAt"] = getCurrentTimeAsTimestamp()
        result["isDefaultCategory"] = this.isDefaultCategory
        when (this) {
            is TrackedCategory.Calories -> {
            }
            is TrackedCategory.ExerciseOneRepMax -> {
                result["exerciseId"] = this.exerciseId
                result["exerciseName"] = this.exerciseName
            }
        }
        return result
    }

    private fun DocumentSnapshot.toUser(): User {
        Timber.d("document: $this")

        @Suppress("UNCHECKED_CAST")
        // The categories should be sorted by the time they were created to guarantee
        // a consistent and logical order
        val trackedCategoriesRawData =
            (this.get("trackedCategories") as List<Map<String, Any>>)
                .sortedBy { it["createdAt"] as Timestamp }


        val trackedCategories = mutableListOf<TrackedCategory>()

        for (category in trackedCategoriesRawData) {
            val dataSourceId = category["dataSourceId"] as String
            val startDate = (category["startDate"] as Timestamp).toLocalDate()
            val endDate = (category["endDate"] as Timestamp).toLocalDate()
            when (category["categoryType"]) {
                TrackedCategoryType.CALORIES.name -> trackedCategories.add(
                    TrackedCategory.Calories(
                        dataSourceId = dataSourceId, startDate = startDate, endDate = endDate,
                        isDefaultCategory = category["isDefaultCategory"] as Boolean
                    )
                )
                TrackedCategoryType.EXERCISE_1RM.name -> trackedCategories.add(
                    TrackedCategory.ExerciseOneRepMax(
                        dataSourceId = dataSourceId,
                        startDate = startDate,
                        endDate = endDate,
                        exerciseId = category["exerciseId"] as String,
                        exerciseName = (category["exerciseName"] as String?) ?: "",
                        isDefaultCategory = category["isDefaultCategory"] as Boolean
                    )
                )
            }
        }
        return User(
            id = this.id, email = this["email"] as String, trackedCategories = trackedCategories
        )
    }
}