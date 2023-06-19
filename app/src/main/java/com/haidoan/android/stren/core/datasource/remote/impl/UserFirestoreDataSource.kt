package com.haidoan.android.stren.core.datasource.remote.impl

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.snapshots
import com.haidoan.android.stren.core.datasource.remote.base.UserRemoteDataSource
import com.haidoan.android.stren.core.model.*
import com.haidoan.android.stren.core.utils.DateUtils.getCurrentTimeAsTimestamp
import com.haidoan.android.stren.core.utils.DateUtils.toLocalDate
import com.haidoan.android.stren.core.utils.DateUtils.toTimeStampDayEnd
import com.haidoan.android.stren.core.utils.DateUtils.toTimeStampDayStart
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

private const val USER_COLLECTION_PATH = "User"
private const val BIOMETRICS_RECORD_COLLECTION_PATH = "BiometricsRecord"
private const val CUSTOM_EXERCISE_COLLECTION_PATH = "CustomExercise"

class UserFirestoreDataSource @Inject constructor() : UserRemoteDataSource {
    private val db = FirebaseFirestore.getInstance()
    private val userCollectionReference = db.collection(USER_COLLECTION_PATH)
    private fun biometricsRecordsCollectionRef(userId: String) =
        db.collection("$USER_COLLECTION_PATH/$userId/$BIOMETRICS_RECORD_COLLECTION_PATH")

    override fun getUserStream(userId: String): Flow<User> =
        combine(
            userCollectionReference.document(userId).snapshots(),
            biometricsRecordsCollectionRef(userId).snapshots()
        ) { userBasicInfoSnapshot, biometricsRecordsSnapshot ->
            val userBasicInfo = userBasicInfoSnapshot.toUser()
            val biometricsRecords = biometricsRecordsSnapshot.toBiometricsRecords().filterLatest()
            Timber.d("userBasicInfo: $userBasicInfo")
            Timber.d("biometricsRecords: $biometricsRecords")
            userBasicInfo.copy(biometricsRecords = biometricsRecords)
        }

    override suspend fun getUser(userId: String): User {
        val userBasicInfo = userCollectionReference.document(userId).get().await().toUser()
        val biometricsRecords =
            biometricsRecordsCollectionRef(userId).get().await().toBiometricsRecords()
                .filterLatest()
        return userBasicInfo.copy(biometricsRecords = biometricsRecords)
    }

    override suspend fun isUserExists(userId: String): Boolean =
        userCollectionReference.document(userId).get().await().exists()

    override suspend fun addUser(user: User) {
        userCollectionReference.document(user.id).set(user).await()
    }

    override suspend fun modifyUserProfile(
        userId: String,
        displayName: String,
        age: Long,
        sex: String
    ) {
        userCollectionReference.document(userId).update(
            mapOf("displayName" to displayName, "age" to age, "sex" to sex)
        ).await()
    }

    override suspend fun getAllBiometricsToTrack(userId: String): List<BiometricsRecord> =
        biometricsRecordsCollectionRef(userId).get().await().toBiometricsRecords().filterLatest()

    override fun getBiometricsRecordsStreamById(
        userId: String,
        biometricsId: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<BiometricsRecord>> =
        biometricsRecordsCollectionRef(userId)
            .whereEqualTo("biometricsId", biometricsId)
            .whereGreaterThanOrEqualTo("recordDate", startDate.toTimeStampDayStart())
            .whereLessThanOrEqualTo("recordDate", endDate.toTimeStampDayEnd())
            .snapshots()
            .map {
                Timber.d(" Querysnapshot - $it")
                Timber.d(" it.toBiometricsRecords() - ${it.toBiometricsRecords()}")
                it.toBiometricsRecords()
            }

    override fun getAllBiometricsRecordsStream(userId: String): Flow<List<BiometricsRecord>> =
        biometricsRecordsCollectionRef(userId)
            .snapshots()
            .map {
                Timber.d(" it.toBiometricsRecords() - ${it.toBiometricsRecords()}")
                it.toBiometricsRecords()
            }

    override suspend fun addBiometricsRecord(
        userId: String,
        biometricsRecord: BiometricsRecord
    ) {
        biometricsRecordsCollectionRef(userId).add(biometricsRecord.toFirestoreObject()).await()
    }

    override suspend fun addBiometricsRecords(
        userId: String,
        biometricsRecords: List<BiometricsRecord>
    ) {
        val batch = db.batch()
        biometricsRecords.forEach { biometricsRecord ->
            val docRef = biometricsRecordsCollectionRef(userId).document()
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

    override suspend fun trackCategory(userId: String, category: TrackedCategory) {
        // For some reason, unless runTransaction() is used to update document, application
        // won't receive real-time updates from Firestore
        db.runTransaction { transaction ->
            val documentRef = userCollectionReference.document(userId)
            transaction.update(
                documentRef,
                "trackedCategories",
                FieldValue.arrayUnion(category.toFirestoreObject())
            )
        }.await()
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

    override suspend fun completeOnboarding(userId: String) {
        userCollectionReference.document(userId).update("shouldShowOnboarding", false).await()
    }

    private fun BiometricsRecord.toFirestoreObject(): Map<String, Any> {
        val result = mutableMapOf<String, Any>()
        result["biometricsId"] = this.biometricsId
        result["biometricsName"] = this.biometricsName
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
            is TrackedCategory.Biometrics -> {
                result["biometricsId"] = this.biometricsId
                result["biometricsName"] = this.biometricsName
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
            try {
                when (enumValueOf<TrackedCategoryType>(category["categoryType"] as String)) {
                    TrackedCategoryType.CALORIES -> trackedCategories.add(
                        TrackedCategory.Calories(
                            dataSourceId = dataSourceId,
                            startDate = startDate,
                            endDate = endDate,
                            isDefaultCategory = category["isDefaultCategory"] as Boolean
                        )
                    )
                    TrackedCategoryType.EXERCISE_1RM -> trackedCategories.add(
                        TrackedCategory.ExerciseOneRepMax(
                            dataSourceId = dataSourceId,
                            startDate = startDate,
                            endDate = endDate,
                            exerciseId = category["exerciseId"] as String,
                            exerciseName = (category["exerciseName"] as String?) ?: "",
                            isDefaultCategory = category["isDefaultCategory"] as Boolean
                        )
                    )
                    TrackedCategoryType.BIOMETRICS -> trackedCategories.add(
                        TrackedCategory.Biometrics(
                            dataSourceId = dataSourceId,
                            startDate = startDate,
                            endDate = endDate,
                            biometricsId = (category["biometricsId"] as String?) ?: "",
                            biometricsName = (category["biometricsName"] as String?) ?: "",
                            isDefaultCategory = category["isDefaultCategory"] as Boolean
                        )
                    )
                }
            } catch (e: Exception) {
                Timber.e("QuerySnapshot.toDefaultTrackedCategories() fails with exception: $e, stacktrace: ${e.stackTrace}")
            }
        }

        @Suppress("UNCHECKED_CAST")
        val goalsRawData = this.get("goals") as List<Map<String, Any>>
        val goals = mutableListOf<Goal>()
        for (goal in goalsRawData) {
            val id = goal["id"] as String
            val value = (goal["value"] as Double).toFloat()
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
            displayName = this["displayName"] as String,
            age = this["age"] as Long,
            sex = this["sex"] as String,
            email = this["email"] as String,
            trackedCategories = trackedCategories,
            shouldShowOnboarding = this["shouldShowOnboarding"] as Boolean,
            goals = goals
        )
    }

    private fun QuerySnapshot.toBiometricsRecords(): List<BiometricsRecord> {
        return this.mapNotNull {
            Timber.d("document: $it")
            BiometricsRecord(
                id = it.id,
                biometricsId = it["biometricsId"] as String,
                biometricsName = it["biometricsName"] as String,
                recordDate = (it["recordDate"] as Timestamp).toLocalDate(),
                measurementUnit = it["measurementUnit"] as String,
                value = (it["value"].toString()).toFloat(),
            )
        }
    }
}