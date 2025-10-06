package com.bridge.androidtechnicaltest.ui.screens.listview

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bridge.androidtechnicaltest.domain.SyncState
import com.bridge.androidtechnicaltest.ui.TestDataUtils
import com.bridge.androidtechnicaltest.ui.theme.TechnicalTestTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ListViewTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun listScreen_displaysLoadingState() {
        val loadingState =
            ListScreenState(
                uiState = ListUiState.Loading,
                syncState = SyncState.UP_TO_DATE,
                addorEditPupilState = AddorEditPupilState(),
            )

        composeTestRule.setContent {
            TechnicalTestTheme {
                ListScreen(
                    uiState = loadingState,
                    onPupilClick = {},
                    onSync = {},
                    onShowAddDialog = {},
                    onDismissDialog = {},
                    onSavePupil = { _, _, _, _, _ -> },
                )
            }
        }

        composeTestRule.onNodeWithText("Bridge International").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Sync").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Add Pupil").assertIsDisplayed()
    }

    @Test
    fun listScreen_displaysEmptyState() {
        val emptyState =
            ListScreenState(
                uiState = ListUiState.Empty,
                syncState = SyncState.UP_TO_DATE,
                addorEditPupilState = AddorEditPupilState(),
            )

        composeTestRule.setContent {
            TechnicalTestTheme {
                ListScreen(
                    uiState = emptyState,
                    onPupilClick = {},
                    onSync = {},
                    onShowAddDialog = {},
                    onDismissDialog = {},
                    onSavePupil = { _, _, _, _, _ -> },
                )
            }
        }

        composeTestRule.onNodeWithText("No pupils available.").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Add Pupil").assertIsDisplayed()
    }

    @Test
    fun listScreen_displaysSuccessStateWithPupils() {
        val pupils = TestDataUtils.createTestPupilList(3)
        val successState =
            ListScreenState(
                uiState = ListUiState.Success(pupils),
                syncState = SyncState.UP_TO_DATE,
                addorEditPupilState = AddorEditPupilState(),
            )

        composeTestRule.setContent {
            TechnicalTestTheme {
                ListScreen(
                    uiState = successState,
                    onPupilClick = {},
                    onSync = {},
                    onShowAddDialog = {},
                    onDismissDialog = {},
                    onSavePupil = { _, _, _, _, _ -> },
                )
            }
        }

        pupils.forEach { pupil ->
            composeTestRule.onNodeWithText(pupil.name).assertIsDisplayed()
            composeTestRule.onNodeWithText(pupil.prettyLocation).assertIsDisplayed()
        }
    }

    @Test
    fun listScreen_displaysErrorState() {
        val errorMessage = "Network error occurred"
        val errorState =
            ListScreenState(
                uiState = ListUiState.Error(errorMessage),
                syncState = SyncState.UP_TO_DATE,
                addorEditPupilState = AddorEditPupilState(),
            )

        composeTestRule.setContent {
            TechnicalTestTheme {
                ListScreen(
                    uiState = errorState,
                    onPupilClick = {},
                    onSync = {},
                    onShowAddDialog = {},
                    onDismissDialog = {},
                    onSavePupil = { _, _, _, _, _ -> },
                )
            }
        }

        composeTestRule.onNodeWithText("Error: $errorMessage").assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
    }

    @Test
    fun listScreen_syncButtonTriggersCallback() {
        var syncCalled = false
        val state =
            ListScreenState(
                uiState = ListUiState.Empty,
                syncState = SyncState.UP_TO_DATE,
                addorEditPupilState = AddorEditPupilState(),
            )

        composeTestRule.setContent {
            TechnicalTestTheme {
                ListScreen(
                    uiState = state,
                    onPupilClick = {},
                    onSync = { syncCalled = true },
                    onShowAddDialog = {},
                    onDismissDialog = {},
                    onSavePupil = { _, _, _, _, _ -> },
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Sync").performClick()
        assert(syncCalled)
    }

    @Test
    fun listScreen_displaysSyncingState() {
        val syncingState =
            ListScreenState(
                uiState = ListUiState.Empty,
                syncState = SyncState.SYNCING,
                addorEditPupilState = AddorEditPupilState(),
            )

        composeTestRule.setContent {
            TechnicalTestTheme {
                ListScreen(
                    uiState = syncingState,
                    onPupilClick = {},
                    onSync = {},
                    onShowAddDialog = {},
                    onDismissDialog = {},
                    onSavePupil = { _, _, _, _, _ -> },
                )
            }
        }

        composeTestRule.onNode(hasContentDescription("Sync")).assertDoesNotExist()
    }

    @Test
    fun listScreen_pupilClickTriggersCallback() {
        val pupils = TestDataUtils.createTestPupilList(1)
        var clickedPupilId = -1
        val successState =
            ListScreenState(
                uiState = ListUiState.Success(pupils),
                syncState = SyncState.UP_TO_DATE,
                addorEditPupilState = AddorEditPupilState(),
            )

        composeTestRule.setContent {
            TechnicalTestTheme {
                ListScreen(
                    uiState = successState,
                    onPupilClick = { pupilId -> clickedPupilId = pupilId },
                    onSync = {},
                    onShowAddDialog = {},
                    onDismissDialog = {},
                    onSavePupil = { _, _, _, _, _ -> },
                )
            }
        }

        composeTestRule.onNodeWithText(pupils.first().name).performClick()
        assert(clickedPupilId == pupils.first().id)
    }

    @Test
    fun listScreen_addButtonTriggersDialog() {
        var showDialogCalled = false
        val state =
            ListScreenState(
                uiState = ListUiState.Empty,
                syncState = SyncState.UP_TO_DATE,
                addorEditPupilState = AddorEditPupilState(),
            )

        composeTestRule.setContent {
            TechnicalTestTheme {
                ListScreen(
                    uiState = state,
                    onPupilClick = {},
                    onSync = {},
                    onShowAddDialog = { showDialogCalled = true },
                    onDismissDialog = {},
                    onSavePupil = { _, _, _, _, _ -> },
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Add Pupil").performClick()
        assert(showDialogCalled)
    }

    @Test
    fun listScreen_displaysAddPupilDialog() {
        val dialogState =
            ListScreenState(
                uiState = ListUiState.Empty,
                syncState = SyncState.UP_TO_DATE,
                addorEditPupilState = AddorEditPupilState(showDialog = true),
            )

        composeTestRule.setContent {
            TechnicalTestTheme {
                ListScreen(
                    uiState = dialogState,
                    onPupilClick = {},
                    onSync = {},
                    onShowAddDialog = {},
                    onDismissDialog = {},
                    onSavePupil = { _, _, _, _, _ -> },
                )
            }
        }

        composeTestRule.onNodeWithText("Add Pupil").assertIsDisplayed()
        composeTestRule.onNodeWithText("Name").assertIsDisplayed()
        composeTestRule.onNodeWithText("Country").assertIsDisplayed()
        composeTestRule.onNodeWithText("Save").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancel").assertIsDisplayed()
    }

    @Test
    fun listScreen_addPupilDialogSavesData() {
        var savedName = ""
        var savedCountry = ""
        var savedImageUrl = ""
        var savedLatitude = 0.0
        var savedLongitude = 0.0

        val dialogState =
            ListScreenState(
                uiState = ListUiState.Empty,
                syncState = SyncState.UP_TO_DATE,
                addorEditPupilState = AddorEditPupilState(showDialog = true),
            )

        composeTestRule.setContent {
            TechnicalTestTheme {
                ListScreen(
                    uiState = dialogState,
                    onPupilClick = {},
                    onSync = {},
                    onShowAddDialog = {},
                    onDismissDialog = {},
                    onSavePupil = { name, country, imageUrl, latitude, longitude ->
                        savedName = name
                        savedCountry = country
                        savedImageUrl = imageUrl
                        savedLatitude = latitude
                        savedLongitude = longitude
                    },
                )
            }
        }

        composeTestRule.onNode(hasText("Name")).performTextInput("John Doe")
        composeTestRule.onNode(hasText("Country")).performTextInput("Kenya")
        composeTestRule.onNode(hasText("Image URL")).performTextInput("https://example.com/john.jpg")
        composeTestRule.onNode(hasText("Latitude")).performTextInput("1.2921")
        composeTestRule.onNode(hasText("Longitude")).performTextInput("36.8219")
        composeTestRule.onNodeWithText("Save").performClick()

        assert(savedName == "John Doe")
        assert(savedCountry == "Kenya")
        assert(savedImageUrl == "https://example.com/john.jpg")
        assert(savedLatitude == 1.2921)
        assert(savedLongitude == 36.8219)
    }

    @Test
    fun listScreen_addPupilDialogCancel() {
        var dismissCalled = false
        val dialogState =
            ListScreenState(
                uiState = ListUiState.Empty,
                syncState = SyncState.UP_TO_DATE,
                addorEditPupilState = AddorEditPupilState(showDialog = true),
            )

        composeTestRule.setContent {
            TechnicalTestTheme {
                ListScreen(
                    uiState = dialogState,
                    onPupilClick = {},
                    onSync = {},
                    onShowAddDialog = {},
                    onDismissDialog = { dismissCalled = true },
                    onSavePupil = { _, _, _, _, _ -> },
                )
            }
        }

        composeTestRule.onNodeWithText("Cancel").performClick()
        assert(dismissCalled)
    }

    @Test
    fun listScreen_retryButtonInErrorState() {
        var retryCalled = false
        val errorState =
            ListScreenState(
                uiState = ListUiState.Error("Network error"),
                syncState = SyncState.UP_TO_DATE,
                addorEditPupilState = AddorEditPupilState(),
            )

        composeTestRule.setContent {
            TechnicalTestTheme {
                ListScreen(
                    uiState = errorState,
                    onPupilClick = {},
                    onSync = { retryCalled = true },
                    onShowAddDialog = {},
                    onDismissDialog = {},
                    onSavePupil = { _, _, _, _, _ -> },
                )
            }
        }

        composeTestRule.onNodeWithText("Retry").performClick()
        assert(retryCalled)
    }
}
