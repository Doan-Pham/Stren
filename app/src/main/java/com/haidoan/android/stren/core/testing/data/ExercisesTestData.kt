package com.haidoan.android.stren.core.testing.data

import com.haidoan.android.stren.core.model.Exercise

val exercisesTestData: List<Exercise> = listOf(
    Exercise(
        name = "Bench Press (Barbell)",
        imageUrl = "https://static.strengthlevel.com/images/illustrations/bench-press-1000x1000.jpg",
        trainedMuscleGroups = listOf("Chest")
    ),
    Exercise(
        name = "Deadlift (Barbell)",
        imageUrl = "https://static.strengthlevel.com/images/illustrations/deadlift-1000x1000.jpg",
        trainedMuscleGroups = listOf("Glute")
    ),
    Exercise(
        name = "Squat (Barbell)",
        imageUrl = "https://static.strengthlevel.com/images/illustrations/squat-1000x1000.jpg",
        trainedMuscleGroups = listOf("Quad")
    )
)