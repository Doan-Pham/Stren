package com.haidoan.android.stren.core.datasource.remote.impl

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.haidoan.android.stren.core.datasource.remote.base.DefaultValuesRemoteDataSource
import com.haidoan.android.stren.core.model.TrackedCategory
import com.haidoan.android.stren.core.model.TrackedCategoryType
import com.haidoan.android.stren.core.utils.DateUtils
import com.haidoan.android.stren.core.utils.DateUtils.toLocalDate
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val DEFAULT_TRACKED_CATEGORY_COLLECTION_PATH = "DefaultTrackedCategory"

internal class DefaultValuesFirestoreDataSource @Inject constructor() :
    DefaultValuesRemoteDataSource {

    private val defaultCategoriesCollection =
        FirebaseFirestore.getInstance().collection(DEFAULT_TRACKED_CATEGORY_COLLECTION_PATH)

    override suspend fun getDefaultTrackedCategories(): List<TrackedCategory> =
        defaultCategoriesCollection.get().await().toDefaultTrackedCategories()

    private fun QuerySnapshot.toDefaultTrackedCategories(): List<TrackedCategory> {
        val trackedCategories = mutableListOf<TrackedCategory>()

        for (document in this.documents) {
            val category = document.data as Map<String, Any>

            val dataSourceId = category["dataSourceId"] as String
            val startDate =
                (category["startDate"] as Timestamp?)?.toLocalDate() ?: DateUtils.getCurrentDate()
                    .minusWeeks(1)
            val endDate =
                (category["startDate"] as Timestamp?)?.toLocalDate() ?: DateUtils.getCurrentDate()

            when (category["categoryType"]) {
                TrackedCategoryType.CALORIES.name -> trackedCategories.add(
                    TrackedCategory.Calories(
                        dataSourceId = dataSourceId,
                        startDate = startDate,
                        endDate = endDate,
                        isDefaultCategory = true
                    )
                )
                TrackedCategoryType.EXERCISE_1RM.name -> trackedCategories.add(
                    TrackedCategory.ExerciseOneRepMax(
                        dataSourceId = dataSourceId,
                        startDate = startDate,
                        endDate = endDate,
                        exerciseId = (category["exerciseId"] as String?) ?: "",
                        exerciseName = (category["exerciseName"] as String?) ?: "",
                        isDefaultCategory = true
                    )
                )
            }

        }
        return trackedCategories
    }
}