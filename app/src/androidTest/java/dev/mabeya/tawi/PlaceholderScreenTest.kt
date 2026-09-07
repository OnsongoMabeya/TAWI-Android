package dev.mabeya.tawi

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.mabeya.tawi.ui.PlaceholderScreenTags
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Phase 1, item 1.8: the app launches on a device/emulator without crashing and shows the
 * Compose placeholder screen.
 */
@RunWith(AndroidJUnit4::class)
class PlaceholderScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun activityReachesResumedStateWithoutCrashing() {
        assertEquals(Lifecycle.State.RESUMED, composeTestRule.activityRule.scenario.state)
    }

    @Test
    fun placeholderScreenIsRendered() {
        composeTestRule.onNodeWithTag(PlaceholderScreenTags.ROOT).assertIsDisplayed()
        composeTestRule.onNodeWithTag(PlaceholderScreenTags.HEADLINE).assertIsDisplayed()
        composeTestRule.onNodeWithTag(PlaceholderScreenTags.TAGLINE).assertIsDisplayed()
    }

    @Test
    fun placeholderScreenShowsStringResources() {
        val context = composeTestRule.activity
        composeTestRule
            .onNodeWithText(context.getString(R.string.placeholder_headline))
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithText(context.getString(R.string.placeholder_tagline))
            .assertIsDisplayed()
    }
}
