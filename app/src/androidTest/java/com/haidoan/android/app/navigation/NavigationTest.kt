package com.haidoan.android.app.navigation

import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.Espresso
import androidx.test.platform.app.InstrumentationRegistry
import com.haidoan.android.NavigationTestActivity
import com.haidoan.android.stren.app.navigation.TopLevelDestination
import com.haidoan.android.stren.core.designsystem.component.TEST_TAG_BOTTOM_NAV
import com.haidoan.android.stren.core.designsystem.component.TEST_TAG_TOP_BAR
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

    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private val dashboardBottomNavItem =
        context.resources.getString(TopLevelDestination.DASHBOARD.titleTextId)
    private val trainingBottomNavItem =
        context.resources.getString(TopLevelDestination.TRAINING.titleTextId)
    private val nutritionBottomNavItem =
        context.resources.getString(TopLevelDestination.NUTRITION.titleTextId)
    private val profileBottomNavItem =
        context.resources.getString(TopLevelDestination.PROFILE.titleTextId)


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

    @Test
    fun bottomNavBar_backFromAnyDestination_returnsToDashboard() {
        composeTestRule.apply {
            activity.isUserSignedIn = true
            waitUntil { onAllNodesWithTag(TEST_TAG_BOTTOM_NAV).fetchSemanticsNodes().size == 1 }

            // GIVEN the user navigated to some destinations
            onNodeWithText(trainingBottomNavItem).performClick()
            onNodeWithText(nutritionBottomNavItem).performClick()
            onNodeWithText(profileBottomNavItem).performClick()
            onNodeWithText(nutritionBottomNavItem).performClick()

            // WHEN the user uses the system button/gesture to go back,
            Espresso.pressBack()

            // THEN the app shows the Dashboard destination
            onNodeWithText(dashboardBottomNavItem).assertIsSelected()
        }
    }

    @Test
    fun topAppBar_navigateToNonAuthDestination_isShown() {
        composeTestRule.apply {
            // Top app bar should not be shown in authentication destinations
            activity.isUserSignedIn = false
            onNodeWithTag(TEST_TAG_TOP_BAR).assertDoesNotExist()

            // Otherwise, Top app bar should be shown
            activity.isUserSignedIn = true
            waitUntil { onAllNodesWithTag(TEST_TAG_BOTTOM_NAV).fetchSemanticsNodes().size == 1 }
            onNodeWithTag(TEST_TAG_TOP_BAR).assertIsDisplayed()

            onNodeWithText(trainingBottomNavItem).performClick()
            onNodeWithText(nutritionBottomNavItem).performClick()
            onNodeWithTag(TEST_TAG_TOP_BAR).assertIsDisplayed()

            // TODO: Add test cases where use navigates to destinations in nested graph
        }

        //TODO: Test app bar's icon in top-level/non-top-level destinations
        //TODO: Test app bar's icon in ProfileScreen
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