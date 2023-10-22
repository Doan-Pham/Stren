package com.haidoan.android.stren.core.model

import com.google.firebase.firestore.DocumentId
import java.time.LocalDate

data class TrainingProgram(
    @DocumentId
    val id: String = "Undefined Training Program ID",
    val name: String,
    val totalNumOfDay: Int,
    val numOfDaysPerWeek: Int,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val routinesByDayOffset: Map<Int, List<Routine>>,
) {
    companion object {
        fun dummy() = TrainingProgram(
            name = "Undefined",
            numOfDaysPerWeek = -1,
            totalNumOfDay = -1,
            startDate = LocalDate.now(),
            endDate = LocalDate.now(),
            routinesByDayOffset = mapOf()
        )
    }
}