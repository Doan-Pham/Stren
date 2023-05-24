package com.haidoan.android.stren.core.datasource.remote.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.haidoan.android.stren.core.model.EatingDay
import com.haidoan.android.stren.core.model.Meal
import com.haidoan.android.stren.core.utils.DateUtils.toLocalDate

internal data class FirestoreEatingDay(
    @DocumentId
    val id: String = "Undefined EatingDay ID",
    val date: Timestamp = Timestamp.now(),
    val meals: List<Meal> = listOf()
)

internal fun FirestoreEatingDay.toExternalModel(): EatingDay =
    EatingDay(id = id, date = date.toLocalDate(), meals = meals)