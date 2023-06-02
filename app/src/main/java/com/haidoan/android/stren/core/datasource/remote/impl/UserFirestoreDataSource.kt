package com.haidoan.android.stren.core.datasource.remote.impl

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.snapshots
import com.haidoan.android.stren.core.datasource.remote.base.UserRemoteDataSource
import com.haidoan.android.stren.core.model.*
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
private const val BIOMETRICS_RECORD_COLLECTION_PATH = "BiometricsRecord"

class UserFirestoreDataSource @Inject constructor() : UserRemoteDataSource {
    private val db = FirebaseFirestore.getInstance()
    private val userCollectionReference = db.collection(USER_COLLECTION_PATH)
    private fun biometricsRecordCollectionRef(userId: String) =
        db.collection("$USER_COLLECTION_PATH/$userId/$BIOMETRICS_RECORD_COLLECTION_PATH")

    override fun getUserStream(userId: String): Flow<User> =
        userCollectionReference.document(userId).snapshots().map { it.toUser() }

    override suspend fun getUser(userId: String): User =
        userCollectionReference.document(userId).get().await().toUser()

    override suspend fun isUserExists(userId: String): Boolean =
        userCollectionReference.document(userId).get().await().exists()

    override suspend fun addUser(user: User) {
        userCollectionReference.document(user.id).set(user).await()
    }

    override suspend fun trackCategory(userId: String, category: TrackedCategory) {
        userCollectionReference.document(userId)
            .update("trackedCategories", FieldValue.arrayUnion(category.toFirestoreObject()))
    }

    override suspend fun updateTrackCategory(
        userId: String, dataSourceId: String, newStartDate: LocalDate, newEndDate: LocalDate
    ) {
        db.runTransaction { transaction ->
            val documentRef = userCollectionReference.document(userId)
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
            val documentRef = userCollectionReference.document(userId)
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

    override suspend fun addBiometricsRecord(
        userId: String,
        biometricsRecords: List<BiometricsRecord>
    ) {
        val batch = db.batch()
        biometricsRecords.forEach { biometricsRecord ->
            val docRef = biometricsRecordCollectionRef(userId).document()
            batch.set(docRef, biometricsRecord.toFirestoreObject())
        }
        batch.commit().await()
    }

    override suspend fun addGoals(userId: String, goals: List<Goal>) {
        // Since FieldValue.arrayUnion() accepts a vararg parameter
        // Need to convert goals to vararg
        // Reference: https://stackoverflow.com/questions/51161558/kotlin-convert-list-to-vararg
        userCollectionReference
            .document(userId)
            .update("goals", FieldValue.arrayUnion(*goals.map { it }.toTypedArray()))
            .await()
    }

    override suspend fun completeOnboarding(userId: String) {
        userCollectionReference.document(userId).update("shouldShowOnboarding", false).await()
    }

    private fun BiometricsRecord.toFirestoreObject(): Map<String, Any> {
        val result = mutableMapOf<String, Any>()
        result["biometricsId"] = this.biometricsId
        result["recordDate"] = this.recordDate.toTimeStampDayStart()
        result["measurementUnit"] = this.measurementUnit
        result["value"] = this.value

        Timber.d("BiometricsRecord.toFirestoreObject() - result: $result")
        return result
    }

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

        @Suppress("UNCHECKED_CAST")
        val goalsRawData = this.get("goals") as List<Map<String, Any>>
        val goals = mutableListOf<Goal>()
        for (goal in goalsRawData) {
            val id = goal["id"] as String
            val value = goal["value"] as Float
            val name = goal["name"] as String
            when (goal["type"]) {
                GoalType.FOOD_NUTRIENT.name -> goals.add(
                    Goal.FoodNutrientGoal(
                        id = id,
                        name = name,
                        value = value,
                        foodNutrientId = goal["foodNutrientId"] as String
                    )
                )
            }
        }

        return User(
            id = this.id,
            email = this["email"] as String,
            trackedCategories = trackedCategories,
            shouldShowOnboarding = this["shouldShowOnboarding"] as Boolean,
            goals = goals
        )
    }
}