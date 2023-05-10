package com.haidoan.android.stren.core.testing.data

import com.haidoan.android.stren.core.model.TrainedExercise
import com.haidoan.android.stren.core.model.TrainingMeasurementMetrics
import com.haidoan.android.stren.core.model.Workout
import java.time.LocalDate

val WORKOUTS_TEST_DATA: List<Workout> = listOf(
    Workout(
        "1",
        "Workout 1",
        "Note 1",
        LocalDate.of(1111, 11, 11),
        trainedExercises = EXERCISES_TEST_DATA.map {
            TrainedExercise(
                it,
                listOf(TrainingMeasurementMetrics.WeightAndRep("11kg", 4))
            )
        }),
    Workout(
        "2",
        "Workout 2",
        "Note 2",
        LocalDate.of(2222, 12, 22),
        trainedExercises = EXERCISES_TEST_DATA.map {
            TrainedExercise(
                it,
                listOf(TrainingMeasurementMetrics.WeightAndRep("22kg", 3))
            )
        }),
    Workout(
        "3",
        "Workout 3",
        "Note 3",
        LocalDate.of(3333, 3, 13),
        trainedExercises = EXERCISES_TEST_DATA.map {
            TrainedExercise(
                it,
                listOf(TrainingMeasurementMetrics.WeightAndRep("33kg", 4))
            )
        })

)