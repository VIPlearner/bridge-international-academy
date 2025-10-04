package com.bridge.androidtechnicaltest.ui.screens.common

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoadingScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loadingIndicator_isVisible_onLaunch() {
        // Given
        composeTestRule.setContent {
            LoadingScreen()
        }

        // Then
        composeTestRule
            .onNodeWithTag("loadingIndicator")
            .assertIsDisplayed()
    }

    @Test
    fun loadingMessage_isDisplayed_withDefaultText() {
        // Given
        composeTestRule.setContent {
            LoadingScreen()
        }

        // Then
        composeTestRule
            .onNodeWithTag("loadingMessage")
            .assertIsDisplayed()
            .assertTextEquals("Loading")
    }

    @Test
    fun loadingMessage_displaysCustomText_whenProvided() {
        // Given
        val customMessage = "Fetching data..."
        composeTestRule.setContent {
            LoadingScreen(text = customMessage)
        }

        // Then
        composeTestRule
            .onNodeWithTag("loadingMessage")
            .assertIsDisplayed()
            .assertTextEquals(customMessage)
    }

    @Test
    fun loadingScreen_handlesEmptyMessage() {
        // Given
        composeTestRule.setContent {
            LoadingScreen(text = "")
        }

        // Then
        composeTestRule
            .onNodeWithTag("loadingIndicator")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag("loadingMessage")
            .assertIsDisplayed()
            .assertTextEquals("")
    }

    @Test
    fun loadingScreen_handlesLongMessage() {
        // Given
        val longMessage = "This is a very long loading message that tests how the UI handles longer text content in the loading screen"
        composeTestRule.setContent {
            LoadingScreen(text = longMessage)
        }

        // Then
        composeTestRule
            .onNodeWithTag("loadingIndicator")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag("loadingMessage")
            .assertIsDisplayed()
            .assertTextEquals(longMessage)
    }
}
