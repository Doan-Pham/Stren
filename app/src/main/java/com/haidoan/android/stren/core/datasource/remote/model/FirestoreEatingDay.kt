package com.haidoan.android.stren.core.datasource.remote.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.haidoan.android.stren.core.model.EatingDay
import com.haidoan.android.stren.core.model.FoodNutrient
import com.haidoan.android.stren.core.model.Meal
import com.haidoan.android.stren.core.utils.DateUtils.toLocalDate
import com.haidoan.android.stren.core.utils.DateUtils.toTimeStampDayStart

internal data class FirestoreEatingDay(
    @DocumentId
    val id: String = "Undefined EatingDay ID",
    val date: Timestamp = Timestamp.now(),
    val meals: List<Meal> = listOf(),
    val totalCalories: Int = 0,
    val totalMacros: List<FoodNutrient> = listOf()
) {
    companion object {
        internal fun from(eatingDay: EatingDay): FirestoreEatingDay =
            FirestoreEatingDay(
                id = eatingDay.id,
                date = eatingDay.date.toTimeStampDayStart(),
                meals = eatingDay.meals,
                totalCalories = eatingDay.totalCalories,
                totalMacros = eatingDay.totalMacros
            )
    }
}

internal fun FirestoreEatingDay.toExternalModel(): EatingDay =
    EatingDay(id = id, date = date.toLocalDate(), meals = meals)