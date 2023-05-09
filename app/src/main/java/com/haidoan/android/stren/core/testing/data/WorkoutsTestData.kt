package com.haidoan.android.stren.core.testing.data

import com.haidoan.android.stren.core.model.TrainedExercise
import com.haidoan.android.stren.core.model.TrainingMeasurementMetrics
import com.haidoan.android.stren.core.model.Workout
import com.haidoan.android.stren.core.utils.DateUtils

val WORKOUTS_TEST_DATA: List<Workout> = listOf(
    Workout(
        "1",
        "Workout 1",
        "Note 1",
        DateUtils.getCurrentDate(),
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
        DateUtils.getCurrentDate(),
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
        DateUtils.getCurrentDate(),
        trainedExercises = EXERCISES_TEST_DATA.map {
            TrainedExercise(
                it,
                listOf(TrainingMeasurementMetrics.WeightAndRep("33kg", 4))
            )
        })

)