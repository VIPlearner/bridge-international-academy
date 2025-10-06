package com.bridge.androidtechnicaltest.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bridge.androidtechnicaltest.domain.SyncState
import com.bridge.androidtechnicaltest.ui.TestDataUtils
import com.bridge.androidtechnicaltest.ui.screens.detailview.DetailScreen
import com.bridge.androidtechnicaltest.ui.screens.detailview.DetailScreenState
import com.bridge.androidtechnicaltest.ui.screens.detailview.DetailUiState
import com.bridge.androidtechnicaltest.ui.screens.detailview.EditPupilState
import com.bridge.androidtechnicaltest.ui.screens.listview.AddorEditPupilState
import com.bridge.androidtechnicaltest.ui.screens.listview.ListScreen
import com.bridge.androidtechnicaltest.ui.screens.listview.ListScreenState
import com.bridge.androidtechnicaltest.ui.screens.listview.ListUiState
import com.bridge.androidtechnicaltest.ui.theme.TechnicalTestTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationIntegrationTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun navigationFlow_listToDetailAndBack() {
        val pupils = TestDataUtils.createTestPupilList(3)
        val selectedPupil =
            TestDataUtils.createTestPupilEntity(
                id = pupils.first().id,
                name = pupils.first().name,
            )

        composeTestRule.setContent {
            TechnicalTestTheme {
                var currentScreen by remember { mutableStateOf("list") }
                var selectedPupilId by remember { mutableStateOf(-1) }

                when (currentScreen) {
                    "list" -> {
                        ListScreen(
                            uiState =
                                ListScreenState(
                                    uiState = ListUiState.Success(pupils),
                                    syncState = SyncState.UP_TO_DATE,
                                    addorEditPupilState = AddorEditPupilState(),
                                ),
                            onPupilClick = { pupilId ->
                                selectedPupilId = pupilId
                                currentScreen = "detail"
                            },
                            onSync = {},
                            onShowAddDialog = {},
                            onDismissDialog = {},
                            onSavePupil = { _, _, _, _, _ -> },
                        )
                    }
                    "detail" -> {
                        DetailScreen(
                            uiState =
                                DetailScreenState(
                                    uiState = DetailUiState.Success(selectedPupil),
                                    editPupilState = EditPupilState(),
                                ),
                            onNavigateBack = { currentScreen = "list" },
                            onShowEditDialog = {},
                            onDismissDialog = {},
                            onUpdatePupil = { _, _, _, _, _ -> },
                            onDeletePupil = {},
                        )
                    }
                }
            }
        }

        composeTestRule.onNodeWithText("Bridge International").assertIsDisplayed()
        composeTestRule.onNodeWithText(pupils.first().name).assertIsDisplayed()

        composeTestRule.onNodeWithText(pupils.first().name).performClick()

        composeTestRule.onNodeWithText("Pupil Details").assertIsDisplayed()
        composeTestRule.onNodeWithText(selectedPupil.name).assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Back").performClick()

        composeTestRule.onNodeWithText("Bridge International").assertIsDisplayed()
        composeTestRule.onNodeWithText(pupils.first().name).assertIsDisplayed()
    }

    @Test
    fun navigationFlow_listToDetailWithEdit() {
        val pupils = TestDataUtils.createTestPupilList(1)
        val selectedPupil =
            TestDataUtils.createTestPupilEntity(
                id = pupils.first().id,
                name = pupils.first().name,
            )

        composeTestRule.setContent {
            TechnicalTestTheme {
                var currentScreen by remember { mutableStateOf("list") }
                var showEditDialog by remember { mutableStateOf(false) }

                when (currentScreen) {
                    "list" -> {
                        ListScreen(
                            uiState =
                                ListScreenState(
                                    uiState = ListUiState.Success(pupils),
                                    syncState = SyncState.UP_TO_DATE,
                                    addorEditPupilState = AddorEditPupilState(),
                                ),
                            onPupilClick = { currentScreen = "detail" },
                            onSync = {},
                            onShowAddDialog = {},
                            onDismissDialog = {},
                            onSavePupil = { _, _, _, _, _ -> },
                        )
                    }
                    "detail" -> {
                        DetailScreen(
                            uiState =
                                DetailScreenState(
                                    uiState = DetailUiState.Success(selectedPupil),
                                    editPupilState = EditPupilState(showDialog = showEditDialog),
                                ),
                            onNavigateBack = { currentScreen = "list" },
                            onShowEditDialog = { showEditDialog = true },
                            onDismissDialog = { showEditDialog = false },
                            onUpdatePupil = { _, _, _, _, _ ->
                                showEditDialog = false
                            },
                            onDeletePupil = { currentScreen = "list" },
                        )
                    }
                }
            }
        }

        composeTestRule.onNodeWithText(pupils.first().name).performClick()

        composeTestRule.onNodeWithText("Pupil Details").assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Edit Pupil").performClick()

        composeTestRule.onNodeWithText("Edit Pupil").assertIsDisplayed()

        composeTestRule.onNodeWithText("Cancel").performClick()

        composeTestRule.onNodeWithText("Pupil Details").assertIsDisplayed()
    }

    @Test
    fun navigationFlow_deleteFromDetailReturnsToList() {
        val pupils = TestDataUtils.createTestPupilList(1)
        val selectedPupil =
            TestDataUtils.createTestPupilEntity(
                id = pupils.first().id,
                name = pupils.first().name,
            )

        composeTestRule.setContent {
            TechnicalTestTheme {
                var currentScreen by remember { mutableStateOf("detail") }

                when (currentScreen) {
                    "list" -> {
                        ListScreen(
                            uiState =
                                ListScreenState(
                                    uiState = ListUiState.Empty,
                                    syncState = SyncState.UP_TO_DATE,
                                    addorEditPupilState = AddorEditPupilState(),
                                ),
                            onPupilClick = {},
                            onSync = {},
                            onShowAddDialog = {},
                            onDismissDialog = {},
                            onSavePupil = { _, _, _, _, _ -> },
                        )
                    }
                    "detail" -> {
                        DetailScreen(
                            uiState =
                                DetailScreenState(
                                    uiState = DetailUiState.Success(selectedPupil),
                                    editPupilState = EditPupilState(),
                                ),
                            onNavigateBack = { currentScreen = "list" },
                            onShowEditDialog = {},
                            onDismissDialog = {},
                            onUpdatePupil = { _, _, _, _, _ -> },
                            onDeletePupil = { currentScreen = "list" },
                        )
                    }
                }
            }
        }

        composeTestRule.onNodeWithText("Pupil Details").assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Delete Pupil").performClick()

        composeTestRule.onNodeWithText("No pupils available.").assertIsDisplayed()
    }

    @Test
    fun listView_addPupilDialogFlow() {
        composeTestRule.setContent {
            TechnicalTestTheme {
                var showDialog by remember { mutableStateOf(false) }
                var pupilAdded by remember { mutableStateOf(false) }

                ListScreen(
                    uiState =
                        ListScreenState(
                            uiState =
                                if (pupilAdded) {
                                    ListUiState.Success(TestDataUtils.createTestPupilList(1))
                                } else {
                                    ListUiState.Empty
                                },
                            syncState = SyncState.UP_TO_DATE,
                            addorEditPupilState = AddorEditPupilState(showDialog = showDialog),
                        ),
                    onPupilClick = {},
                    onSync = {},
                    onShowAddDialog = { showDialog = true },
                    onDismissDialog = { showDialog = false },
                    onSavePupil = { _, _, _, _, _ ->
                        showDialog = false
                        pupilAdded = true
                    },
                )
            }
        }

        composeTestRule.onNodeWithTag("no_pupils_text").assertIsDisplayed()

        composeTestRule.onNodeWithTag("add_pupil_fab").performClick()

        composeTestRule.onNodeWithTag("pupil_form_dialog").assertIsDisplayed()
        composeTestRule.onNodeWithTag("pupil_form_title").assertIsDisplayed()

        composeTestRule.onNodeWithTag("pupil_form_name_field").performTextInput("Test Pupil")
        composeTestRule.onNodeWithTag("pupil_form_country_field").performTextInput("Test Country")
        composeTestRule.onNodeWithTag("pupil_form_image_url_field").performTextInput("https://example.com/image.jpg")
        composeTestRule.onNodeWithTag("pupil_form_latitude_field").performTextInput("40.7128")
        composeTestRule.onNodeWithTag("pupil_form_longitude_field").performTextInput("-74.0060")

        composeTestRule.onNodeWithTag("pupil_form_save_button").performClick()

        composeTestRule.onNodeWithText("Pupil 1").assertIsDisplayed()
    }
}
