package com.haidoan.android.feat.training

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.haidoan.android.stren.feat.trainining.TrainingTabsScreen
import org.junit.Rule
import org.junit.Test

class TrainingTabsScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Composable
    private fun FakeScreen(testTag: String) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .testTag(testTag)
        )
    }

    private var tabsNameAndScreensTestTag = listOf(
        Pair("TestScreen1TabTitle", "TestScreen1"),
        Pair("TestScreen2TabTitle", "TestScreen2"),
        Pair("TestScreen3TabTitle", "TestScreen3")
    )

    @Test
    fun tabsScreen_clickOnTab_correctScreenIsShown() {
        composeTestRule.setContent {
            BoxWithConstraints {
                TrainingTabsScreen(tabsNameAndScreensTestTag.map {
                    Pair(
                        it.first,
                        FakeScreen(testTag = it.second)
                    )
                })
            }
        }
        tabsNameAndScreensTestTag.forEach { pair ->
            composeTestRule.onNodeWithText(pair.first).performClick()
            composeTestRule.onNodeWithTag(pair.second).assertIsDisplayed()
        }
    }
}