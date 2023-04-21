package com.haidoan.android.app.navigation

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.testing.TestNavHostController
import androidx.test.platform.app.InstrumentationRegistry
import com.haidoan.android.NavigationTestActivity
import com.haidoan.android.stren.R
import com.haidoan.android.stren.designsystem.component.TEST_TAG_BOTTOM_NAV
import com.haidoan.android.stren.feat.auth.login.TEST_TAG_SCREEN_LOGIN
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*
import kotlin.concurrent.schedule

@HiltAndroidTest
class NavigationTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)


    val composeTestRule = createAndroidComposeRule<NavigationTestActivity>()
    lateinit var navController: TestNavHostController

    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    val dashboardLabel = context.resources.getString(R.string.bottom_nav_title_dashboard)
    val trainingLabel = context.resources.getString(R.string.bottom_nav_title_training)
    val nutritionLabel = context.resources.getString(R.string.bottom_nav_title_nutrition)
    val profileLabel = context.resources.getString(R.string.bottom_nav_title_profile)


    @Before
    fun setupStrenNavHost() {
        composeTestRule.activity.isUserSignedIn = false
    }


    @Test
    fun strenNavHost_userNotSignedIn_loginScreenIsShown() {
        composeTestRule.apply {
            onNodeWithTag(TEST_TAG_SCREEN_LOGIN).assertIsDisplayed()
            onNodeWithText("Email").assertIsDisplayed()
            onNodeWithText("Password").assertIsDisplayed()
            onNodeWithText("Login").assertIsDisplayed()
        }
    }

    @Test
    fun strenNavHost_userAlreadySignedIn_dashboardScreenIsShown() {
        composeTestRule.apply {
            activity.isUserSignedIn = true
            waitUntilTimeout(4000L)
            onNodeWithTag("Screen-Dashboard").assertIsDisplayed()
            onNodeWithTag(TEST_TAG_BOTTOM_NAV).assertIsDisplayed()
        }
    }

    @Test
    fun strenNavHost_userSignOut_loginScreenIsShown() {
        composeTestRule.apply {
            activity.isUserSignedIn = true
            waitUntil { onAllNodesWithTag(TEST_TAG_BOTTOM_NAV).fetchSemanticsNodes().size == 1 }
            activity.isUserSignedIn = false
            onNodeWithTag(TEST_TAG_SCREEN_LOGIN).assertIsDisplayed()
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