package com.haidoan.android.app.navigation

import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.testing.TestNavHostController
import androidx.test.platform.app.InstrumentationRegistry
import com.haidoan.android.stren.R
import com.haidoan.android.stren.app.MainActivity
import com.haidoan.android.stren.designsystem.component.TEST_TAG_BOTTOM_NAV
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import java.util.*
import kotlin.concurrent.schedule

@HiltAndroidTest
class NavigationTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)

    val composeTestRule = createAndroidComposeRule<MainActivity>()
    lateinit var navController: TestNavHostController

    val context: Context = InstrumentationRegistry.getInstrumentation().getTargetContext()
    val dashboardLabel = context.resources.getString(R.string.bottom_nav_title_dashboard)
    val trainingLabel = context.resources.getString(R.string.bottom_nav_title_training)
    val nutritionLabel = context.resources.getString(R.string.bottom_nav_title_nutrition)
    val profileLabel = context.resources.getString(R.string.bottom_nav_title_profile)

    @Test
    fun strenNavHost_loginSuccess_dashBoardScreenIsShown() {
        composeTestRule.apply {
            onNodeWithText("Email").performTextInput("asd@gmail.com")
            onNodeWithText("Password").performTextInput("asdqwe")
            onNodeWithText("Login").performClick()
            waitUntilTimeout(5000L)
            onNodeWithTag("Screen-Dashboard").assertIsDisplayed()
            onNodeWithTag(TEST_TAG_BOTTOM_NAV).assertIsDisplayed()
        }
    }

    @Test
    fun strenNavHost_verifyStartDestination() {
        composeTestRule.apply {
            onNodeWithText("Email").performTextInput("asd@gmail.com")
            onNodeWithText("Password").performTextInput("asdqwe")
            onNodeWithText("Login").performClick()
            waitUntil(10000) {
                onAllNodesWithTag(TEST_TAG_BOTTOM_NAV).fetchSemanticsNodes().size == 1
            }
            onNodeWithText(dashboardLabel).assertIsSelected()
        }
    }
}

fun ComposeContentTestRule.waitUntilTimeout(
    timeoutMillis: Long
) {
    AsyncTimer.start(timeoutMillis)
    this.waitUntil(
        condition = { AsyncTimer.expired },
        timeoutMillis = timeoutMillis + 1000
    )
}

object AsyncTimer {
    var expired = false
    fun start(delay: Long = 1000) {
        expired = false
        Timer().schedule(delay) {
            expired = true
        }
    }
}