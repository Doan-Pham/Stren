package com.haidoan.android.stren.core.datasource.remote.impl

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.haidoan.android.stren.core.datasource.remote.base.DefaultValuesRemoteDataSource
import com.haidoan.android.stren.core.model.Biometrics
import com.haidoan.android.stren.core.model.TrackedCategory
import com.haidoan.android.stren.core.model.TrackedCategoryType
import com.haidoan.android.stren.core.utils.DateUtils
import com.haidoan.android.stren.core.utils.DateUtils.toLocalDate
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

private const val DEFAULT_TRACKED_CATEGORY_COLLECTION_PATH = "DefaultTrackedCategory"
private const val DEFAULT_BIOMETRICS_COLLECTION_PATH = "DefaultBiometrics"

internal class DefaultValuesFirestoreDataSource @Inject constructor() :
    DefaultValuesRemoteDataSource {

    private val defaultCategoriesCollection =
        FirebaseFirestore.getInstance().collection(DEFAULT_TRACKED_CATEGORY_COLLECTION_PATH)

    private val defaultBiometricsCollection =
        FirebaseFirestore.getInstance().collection(DEFAULT_BIOMETRICS_COLLECTION_PATH)

    override suspend fun getDefaultTrackedCategories(): List<TrackedCategory> =
        defaultCategoriesCollection.get().await().toDefaultTrackedCategories()

    override suspend fun getDefaultBiometrics(): List<Biometrics> =
        defaultBiometricsCollection.get().await().mapNotNull { it.toObject(Biometrics::class.java) }

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
            try {
                when (enumValueOf<TrackedCategoryType>(category["categoryType"] as String)) {
                    TrackedCategoryType.CALORIES -> trackedCategories.add(
                        TrackedCategory.Calories(
                            dataSourceId = dataSourceId,
                            startDate = startDate,
                            endDate = endDate,
                            isDefaultCategory = true
                        )
                    )
                    TrackedCategoryType.EXERCISE_1RM -> trackedCategories.add(
                        TrackedCategory.ExerciseOneRepMax(
                            dataSourceId = dataSourceId,
                            startDate = startDate,
                            endDate = endDate,
                            exerciseId = (category["exerciseId"] as String?) ?: "",
                            exerciseName = (category["exerciseName"] as String?) ?: "",
                            isDefaultCategory = true
                        )
                    )
                    TrackedCategoryType.BIOMETRICS -> trackedCategories.add(
                        TrackedCategory.Biometrics(
                            dataSourceId = dataSourceId,
                            startDate = startDate,
                            endDate = endDate,
                            biometricsId = (category["biometricsId"] as String?) ?: "",
                            biometricsName = (category["biometricsName"] as String?) ?: "",
                            isDefaultCategory = true
                        )
                    )
                }
            } catch (e: Exception) {
                Timber.e("QuerySnapshot.toDefaultTrackedCategories() fails with exception: $e, stacktrace: ${e.stackTrace}")
            }
        }
        return trackedCategories
    }
}