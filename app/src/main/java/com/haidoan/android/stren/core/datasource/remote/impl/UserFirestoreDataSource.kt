package com.haidoan.android.stren.core.datasource.remote.impl

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.snapshots
import com.haidoan.android.stren.core.datasource.remote.base.UserRemoteDataSource
import com.haidoan.android.stren.core.model.TrackedCategory
import com.haidoan.android.stren.core.model.User
import com.haidoan.android.stren.core.utils.DateUtils.toLocalDate
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

    private fun DocumentSnapshot.toUser(): User {
        Timber.d("document: $this")

        @Suppress("UNCHECKED_CAST")
        val trackedCategoriesRawData = this.get("trackedCategories") as List<Map<String, Any>>
        val trackedCategories = mutableListOf<TrackedCategory>()

        for (category in trackedCategoriesRawData) {
            val dataSourceId = category["dataSourceId"] as String
            val startDate = (category["startDate"] as Timestamp).toLocalDate()
            val endDate = (category["startDate"] as Timestamp).toLocalDate()
            when (category["categoryType"]) {
                "CALORIES" -> trackedCategories.add(
                    TrackedCategory.Calories(
                        dataSourceId = dataSourceId,
                        startDate = startDate,
                        endDate = endDate
                    )
                )
                "EXERCISE_1RM" -> trackedCategories.add(
                    TrackedCategory.ExerciseOneRepMax(
                        dataSourceId = dataSourceId,
                        startDate = startDate,
                        endDate = endDate,
                        exerciseId = category["exerciseId"] as String
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