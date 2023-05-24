package com.haidoan.android.stren.core.model

import com.google.firebase.firestore.DocumentId
import java.time.LocalDate

data class EatingDay(
    @DocumentId
    val id: String = "Undefined EatingDay ID",
    val date: LocalDate,
    val meals: List<Meal> = listOf()
)

data class Meal(val name: String = "Undefined Meal name", val foods: List<Food> = listOf())
