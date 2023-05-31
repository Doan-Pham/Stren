package com.haidoan.android.stren.core.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import com.haidoan.android.stren.core.utils.DateUtils
import java.time.LocalDate

data class User(
    @DocumentId
    val id: String,
    val email: String,
    val shouldShowOnboarding: Boolean = true,
    val trackedCategories: List<TrackedCategory> = listOf()
) {
    companion object {
        val undefined = User("Undefined", "Undefined")
    }
}

sealed class TrackedCategory {
    @get:PropertyName("isDefaultCategory")
    abstract val isDefaultCategory: Boolean
    abstract val categoryType: TrackedCategoryType
    abstract val dataSourceId: String
    abstract val startDate: LocalDate
    abstract val endDate: LocalDate
    abstract fun withStartDate(startDate: LocalDate): TrackedCategory
    abstract fun withEndDate(endDate: LocalDate): TrackedCategory
    data class Calories(
        override val dataSourceId: String = DataSourceBaseId.DATA_SOURCE_ID_CALORIES.toString(),
        override val startDate: LocalDate = DateUtils.getCurrentDate().minusWeeks(1),
        override val endDate: LocalDate = DateUtils.getCurrentDate(),
        override val categoryType: TrackedCategoryType = TrackedCategoryType.CALORIES,
        override val isDefaultCategory: Boolean
    ) : TrackedCategory() {
        override fun withStartDate(startDate: LocalDate): TrackedCategory =
            this.copy(startDate = startDate)

        override fun withEndDate(endDate: LocalDate): TrackedCategory = this.copy(endDate = endDate)
    }


    data class ExerciseOneRepMax(
        override val categoryType: TrackedCategoryType = TrackedCategoryType.EXERCISE_1RM,
        override val startDate: LocalDate = DateUtils.getCurrentDate().minusWeeks(1),
        override val endDate: LocalDate = DateUtils.getCurrentDate(),
        val exerciseId: String,
        val exerciseName: String,
        override val dataSourceId: String =
            DataSourceBaseId.DATA_SOURCE_ID_EXERCISE_1RM.toString() + "_" + exerciseId,
        override val isDefaultCategory: Boolean
    ) : TrackedCategory() {
        override fun withStartDate(startDate: LocalDate): TrackedCategory =
            this.copy(startDate = startDate)

        override fun withEndDate(endDate: LocalDate): TrackedCategory = this.copy(endDate = endDate)
    }
}

enum class TrackedCategoryType {
    CALORIES, EXERCISE_1RM
}

enum class DataSourceBaseId {
    DATA_SOURCE_ID_CALORIES, DATA_SOURCE_ID_EXERCISE_1RM
}