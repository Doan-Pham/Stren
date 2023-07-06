package com.haidoan.android.feat.training

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test

class ExercisesScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun exercisesScreen_exercisesLoaded_exercisesAreShown() {
        composeTestRule.setContent {
            BoxWithConstraints {
                val exercises =
                    flowOf(PagingData.from(com.haidoan.android.stren.util.EXERCISES_TEST_DATA)).collectAsLazyPagingItems()
                //ExercisesScreen(pagedExercises = exercises)
            }
        }
        com.haidoan.android.stren.util.EXERCISES_TEST_DATA.forEach {
            composeTestRule.onNodeWithText(it.name).assertExists()
            composeTestRule.onNodeWithText(it.trainedMuscleGroups.first()).assertExists()
        }
    }
}