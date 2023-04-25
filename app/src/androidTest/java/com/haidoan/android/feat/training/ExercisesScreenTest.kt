package com.haidoan.android.feat.training

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.haidoan.android.stren.core.testing.data.exercisesTestData
import com.haidoan.android.stren.feat.trainining.exercises.EXERCISES_LOADING_ANIMATION_TEST_TAG
import com.haidoan.android.stren.feat.trainining.exercises.ExercisesScreen
import com.haidoan.android.stren.feat.trainining.exercises.ExercisesUiState
import org.junit.Rule
import org.junit.Test

class ExercisesScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun loadingAnimation_uiStateIsLoading_isShown() {
        composeTestRule.setContent {
            BoxWithConstraints {
                ExercisesScreen(uiState = ExercisesUiState.Loading)
            }
        }
        composeTestRule.onNodeWithTag(EXERCISES_LOADING_ANIMATION_TEST_TAG).assertExists()
        composeTestRule.onNodeWithTag(EXERCISES_LOADING_ANIMATION_TEST_TAG).assertIsDisplayed()
    }

    @Test
    fun exercisesScreen_uiStateLoadComplete_exercisesAreShown() {
        composeTestRule.setContent {
            BoxWithConstraints {
                ExercisesScreen(uiState = ExercisesUiState.LoadComplete(exercisesTestData))
            }
        }

        exercisesTestData.forEach {
            composeTestRule.onNodeWithText(it.name).assertExists()
            composeTestRule.onNodeWithText(it.trainedMuscleGroups.first()).assertExists()
        }
    }
}