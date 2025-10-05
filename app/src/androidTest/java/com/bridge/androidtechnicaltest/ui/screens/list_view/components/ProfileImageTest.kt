package com.bridge.androidtechnicaltest.ui.screens.list_view.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileImageTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun profileImage_withNullUrl_displaysPersonIcon() {
        composeTestRule.setContent {
            ProfileImage(
                url = null,
                contentDescription = "Profile picture",
                modifier = Modifier.size(100.dp)
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Profile picture")
            .assertIsDisplayed()
    }

    @Test
    fun profileImage_withBlankUrl_displaysPersonIcon() {
        composeTestRule.setContent {
            ProfileImage(
                url = "",
                contentDescription = "Profile picture",
                modifier = Modifier.size(100.dp)
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Profile picture")
            .assertIsDisplayed()
    }

    @Test
    fun profileImage_withWhitespaceUrl_displaysPersonIcon() {
        composeTestRule.setContent {
            ProfileImage(
                url = "   ",
                contentDescription = "Profile picture",
                modifier = Modifier.size(100.dp)
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Profile picture")
            .assertIsDisplayed()
    }

    @Test
    fun profileImage_withValidUrl_displaysLoadingState() {
        composeTestRule.setContent {
            ProfileImage(
                url = "https://example.com/image.jpg",
                contentDescription = "Profile picture",
                modifier = Modifier
                    .size(100.dp)
                    .testTag("profile_image")
            )
        }

        composeTestRule
            .onNodeWithTag("profile_image")
            .assertIsDisplayed()
    }

    @Test
    fun profileImage_withInvalidUrl_displaysErrorIcon() {
        composeTestRule.setContent {
            ProfileImage(
                url = "invalid-url",
                contentDescription = "Profile picture",
                modifier = Modifier.size(100.dp)
            )
        }

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            try {
                composeTestRule
                    .onNodeWithContentDescription("Image failed to load")
                    .assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }

        composeTestRule
            .onNodeWithContentDescription("Image failed to load")
            .assertIsDisplayed()
    }

    @Test
    fun profileImage_appliesModifierCorrectly() {
        val testTag = "test_profile_image"

        composeTestRule.setContent {
            ProfileImage(
                url = null,
                contentDescription = "Profile picture",
                modifier = Modifier
                    .size(100.dp)
                    .testTag(testTag)
            )
        }

        composeTestRule
            .onNodeWithTag(testTag)
            .assertIsDisplayed()
    }

    @Test
    fun profileImage_withEmptyStringContentDescription_displaysIcon() {
        composeTestRule.setContent {
            ProfileImage(
                url = null,
                contentDescription = "",
                modifier = Modifier.size(100.dp)
            )
        }

        composeTestRule
            .onNodeWithContentDescription("")
            .assertIsDisplayed()
    }

    @Test
    fun profileImage_handlesNetworkError_displaysErrorIcon() {
        composeTestRule.setContent {
            ProfileImage(
                url = "https://nonexistent-domain-12345.com/image.jpg",
                contentDescription = "Profile picture",
                modifier = Modifier.size(100.dp)
            )
        }

        composeTestRule.waitUntil(timeoutMillis = 10000) {
            try {
                composeTestRule
                    .onNodeWithContentDescription("Image failed to load")
                    .assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }

        composeTestRule
            .onNodeWithContentDescription("Image failed to load")
            .assertIsDisplayed()
    }

    @Test
    fun profileImage_with404Url_displaysErrorIcon() {
        composeTestRule.setContent {
            ProfileImage(
                url = "https://httpstat.us/404",
                contentDescription = "Profile picture",
                modifier = Modifier.size(100.dp)
            )
        }

        composeTestRule.waitUntil(timeoutMillis = 10000) {
            try {
                composeTestRule
                    .onNodeWithContentDescription("Image failed to load")
                    .assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }

        composeTestRule
            .onNodeWithContentDescription("Image failed to load")
            .assertIsDisplayed()
    }
}
