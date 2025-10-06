package com.bridge.androidtechnicaltest.ui.screens.listview

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
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
                    onViewEvent = {},
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
                    onViewEvent = {},
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
                    onViewEvent = {},
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
                    onViewEvent = {},
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
                    onViewEvent = { event ->
                        if (event is ListViewEvent.Sync) {
                            syncCalled = true
                        }
                    },
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
                    onViewEvent = {},
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
                    onViewEvent = {},
                )
            }
        }

        composeTestRule.onNodeWithText(pupils[0].name).performClick()
        assert(clickedPupilId == pupils[0].id)
    }

    @Test
    fun listScreen_addPupilButtonTriggersDialog() {
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
                    onViewEvent = { event ->
                        if (event is ListViewEvent.ShowAddDialog) {
                            showDialogCalled = true
                        }
                    },
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Add Pupil").performClick()
        assert(showDialogCalled)
    }

    @Test
    fun listScreen_displaysDialogWhenStateIsShow() {
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
                    onViewEvent = {},
                )
            }
        }

        composeTestRule.onNodeWithText("Add Pupil").assertIsDisplayed()
        composeTestRule.onNodeWithText("Name").assertIsDisplayed()
        composeTestRule.onNodeWithText("Country").assertIsDisplayed()
    }

    @Test
    fun listScreen_savePupilTriggersCallback() {
        var savedName = ""
        var savedCountry = ""
        var savedImageUrl: String? = null
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
                    onViewEvent = { event ->
                        if (event is ListViewEvent.AddPupil) {
                            savedName = event.name
                            savedCountry = event.country
                            savedImageUrl = event.image
                            savedLatitude = event.latitude
                            savedLongitude = event.longitude
                        }
                    },
                )
            }
        }

        composeTestRule.onNodeWithText("Name").performTextInput("Test Name")
        composeTestRule.onNodeWithText("Country").performTextInput("Test Country")
        composeTestRule.onNodeWithTag("pupil_form_save_button").performClick()

        assert(savedName == "Test Name")
        assert(savedCountry == "Test Country")
        assert(savedImageUrl == "")
        assert(savedLatitude == 0.0)
        assert(savedLongitude == 0.0)
    }

    @Test
    fun listScreen_dismissDialogTriggersCallback() {
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
                    onViewEvent = { event ->
                        if (event is ListViewEvent.DismissDialog) {
                            dismissCalled = true
                        }
                    },
                )
            }
        }

        composeTestRule.onNodeWithText("Cancel").performClick()
        assert(dismissCalled)
    }

    @Test
    fun listScreen_retryButtonTriggersSync() {
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
                    onViewEvent = { event ->
                        if (event is ListViewEvent.Sync) {
                            retryCalled = true
                        }
                    },
                )
            }
        }

        composeTestRule.onNodeWithText("Retry").performClick()
        assert(retryCalled)
    }
}
