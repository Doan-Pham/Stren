package com.haidoan.android.stren.core.testing.data

import com.haidoan.android.stren.core.model.Exercise

val exercisesTestData: List<Exercise> = listOf(
    Exercise(
        id = "1",
        name = "Bench Press (Barbell)",
        imageUrls = listOf("https://static.strengthlevel.com/images/illustrations/bench-press-1000x1000.jpg"),
        trainedMuscleGroups = listOf("Chest")
    ),
    Exercise(
        id = "2",
        name = "Deadlift (Barbell)",
        imageUrls = listOf("https://static.strengthlevel.com/images/illustrations/deadlift-1000x1000.jpg"),
        trainedMuscleGroups = listOf("Glute")
    ),
    Exercise(
        id = "3",
        name = "Squat (Barbell)",
        imageUrls = listOf("https://static.strengthlevel.com/images/illustrations/squat-1000x1000.jpg"),
        trainedMuscleGroups = listOf("Quad")
    )
)