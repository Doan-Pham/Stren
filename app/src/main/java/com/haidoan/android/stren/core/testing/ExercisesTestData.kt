package com.haidoan.android.stren.util

import com.haidoan.android.stren.core.model.Exercise
import com.haidoan.android.stren.core.model.ExerciseCategory

val EXERCISES_TEST_DATA: List<Exercise> = listOf(
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

val EXERCISE_CATEGORIES_TEST_DATA: List<ExerciseCategory> = listOf(
    ExerciseCategory(
        id = "1",
        name = "Barbell",
    ),
    ExerciseCategory(
        id = "2",
        name = "Dumbbell",
    ),
    ExerciseCategory(
        id = "3",
        name = "Cardio",
    ),
)
