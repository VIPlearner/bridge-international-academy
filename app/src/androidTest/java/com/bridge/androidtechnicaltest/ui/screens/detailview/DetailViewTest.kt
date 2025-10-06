package com.bridge.androidtechnicaltest.ui.screens.detailview

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bridge.androidtechnicaltest.ui.TestDataUtils
import com.bridge.androidtechnicaltest.ui.theme.TechnicalTestTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DetailViewTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun detailScreen_displaysLoadingState() {
        val loadingState =
            DetailScreenState(
                uiState = DetailUiState.Loading,
                editPupilState = EditPupilState(),
            )

        composeTestRule.setContent {
            TechnicalTestTheme {
                DetailScreen(
                    uiState = loadingState,
                    onNavigateBack = {},
                    onViewEvent = {
                        // do nothing
                    },
                )
            }
        }

        composeTestRule.onNodeWithText("Pupil Details").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Back").assertIsDisplayed()
    }

    @Test
    fun detailScreen_displaysErrorState() {
        val errorMessage = "Failed to load pupil details"
        val errorState =
            DetailScreenState(
                uiState = DetailUiState.Error(errorMessage),
                editPupilState = EditPupilState(),
            )

        composeTestRule.setContent {
            TechnicalTestTheme {
                DetailScreen(
                    uiState = errorState,
                    onNavigateBack = {},
                    onViewEvent = {
                        // do nothing
                    },
                )
            }
        }

        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
    }

    @Test
    fun detailScreen_displaysSuccessStateWithPupilData() {
        val testPupil =
            TestDataUtils.createTestPupilEntity(
                name = "John Doe",
                country = "Kenya",
                latitude = 1.2921,
                longitude = 36.8219,
            )
        val successState =
            DetailScreenState(
                uiState = DetailUiState.Success(testPupil),
                editPupilState = EditPupilState(),
            )

        composeTestRule.setContent {
            TechnicalTestTheme {
                DetailScreen(
                    uiState = successState,
                    onNavigateBack = {},
                    onViewEvent = {
                        // do nothing
                    },
                )
            }
        }

        composeTestRule.onNodeWithText("John Doe").assertIsDisplayed()
        composeTestRule.onNodeWithText("Kenya").assertIsDisplayed()
        composeTestRule.onNodeWithText("1.2921").assertIsDisplayed()
        composeTestRule.onNodeWithText("36.8219").assertIsDisplayed()
        composeTestRule.onNodeWithText("Country").assertIsDisplayed()
        composeTestRule.onNodeWithText("Latitude").assertIsDisplayed()
        composeTestRule.onNodeWithText("Longitude").assertIsDisplayed()
    }

    @Test
    fun detailScreen_backButtonTriggersNavigation() {
        var backCalled = false
        val testPupil = TestDataUtils.createTestPupilEntity()
        val successState =
            DetailScreenState(
                uiState = DetailUiState.Success(testPupil),
                editPupilState = EditPupilState(),
            )

        composeTestRule.setContent {
            TechnicalTestTheme {
                DetailScreen(
                    uiState = successState,
                    onNavigateBack = { backCalled = true },
                    onViewEvent = {
                        // do nothing
                    },
                )
            }
        }

        composeTestRule.onNodeWithTag("detail_back_button").performClick()
        assert(backCalled)
    }

    @Test
    fun detailScreen_editButtonTriggersDialog() {
        var editCalled = false
        val testPupil = TestDataUtils.createTestPupilEntity()
        val successState =
            DetailScreenState(
                uiState = DetailUiState.Success(testPupil),
                editPupilState = EditPupilState(),
            )

        composeTestRule.setContent {
            TechnicalTestTheme {
                DetailScreen(
                    uiState = successState,
                    onNavigateBack = {},
                    onViewEvent = {
                        if (it is DetailViewEvent.ShowEditDialog) {
                            editCalled = true
                        }
                    },
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Edit Pupil").performClick()
        assert(editCalled)
    }

    @Test
    fun detailScreen_deleteButtonTriggersCallback() {
        var deleteCalled = false
        val testPupil = TestDataUtils.createTestPupilEntity()
        val successState =
            DetailScreenState(
                uiState = DetailUiState.Success(testPupil),
                editPupilState = EditPupilState(),
            )

        composeTestRule.setContent {
            TechnicalTestTheme {
                DetailScreen(
                    uiState = successState,
                    onNavigateBack = {},
                    onViewEvent = {
                        if (it is DetailViewEvent.DeletePupil) {
                            deleteCalled = true
                        }
                    },
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Delete Pupil").performClick()
        assert(deleteCalled)
    }

    @Test
    fun detailScreen_displaysEditDialog() {
        val testPupil =
            TestDataUtils.createTestPupilEntity(
                name = "John Doe",
                country = "Kenya",
            )
        val dialogState =
            DetailScreenState(
                uiState = DetailUiState.Success(testPupil),
                editPupilState = EditPupilState(showDialog = true),
            )

        composeTestRule.setContent {
            TechnicalTestTheme {
                DetailScreen(
                    uiState = dialogState,
                    onNavigateBack = {},
                    onViewEvent = {
                        // do nothing
                    },
                )
            }
        }

        composeTestRule.onNodeWithText("Edit Pupil").assertIsDisplayed()
        composeTestRule.onNodeWithText("Save").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancel").assertIsDisplayed()
    }

    @Test
    fun detailScreen_editDialogSavesUpdatedData() {
        var updatedName = ""
        var updatedCountry = ""

        val testPupil =
            TestDataUtils.createTestPupilEntity(
                name = "John Doe",
                country = "Kenya",
            )
        val dialogState =
            DetailScreenState(
                uiState = DetailUiState.Success(testPupil),
                editPupilState = EditPupilState(showDialog = true),
            )

        composeTestRule.setContent {
            TechnicalTestTheme {
                DetailScreen(
                    uiState = dialogState,
                    onNavigateBack = {},
                    onViewEvent = {
                        if (it is DetailViewEvent.UpdatePupil) {
                            updatedName = it.name
                            updatedCountry = it.country
                        }
                    },
                )
            }
        }

        // Verify dialog is displayed
        composeTestRule.onNodeWithTag("pupil_form_dialog").assertIsDisplayed()
        composeTestRule.onNodeWithTag("pupil_form_title").assertIsDisplayed()

        // Clear and input new values using test tags
        composeTestRule.onNodeWithTag("pupil_form_name_field").performTextClearance()
        composeTestRule.onNodeWithTag("pupil_form_name_field").performTextInput("Jane Smith")

        composeTestRule.onNodeWithTag("pupil_form_country_field").performTextClearance()
        composeTestRule.onNodeWithTag("pupil_form_country_field").performTextInput("Uganda")

        // Click save using test tag
        composeTestRule.onNodeWithTag("pupil_form_save_button").performClick()

        assert(updatedName == "Jane Smith")
        assert(updatedCountry == "Uganda")
    }

    @Test
    fun detailScreen_editDialogCancel() {
        var dismissCalled = false
        val testPupil = TestDataUtils.createTestPupilEntity()
        val dialogState =
            DetailScreenState(
                uiState = DetailUiState.Success(testPupil),
                editPupilState = EditPupilState(showDialog = true),
            )

        composeTestRule.setContent {
            TechnicalTestTheme {
                DetailScreen(
                    uiState = dialogState,
                    onNavigateBack = {},
                    onViewEvent = {
                        if (it is DetailViewEvent.DismissDialog) {
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
    fun detailScreen_showsDeleteLoadingState() {
        val testPupil = TestDataUtils.createTestPupilEntity()
        val deletingState =
            DetailScreenState(
                uiState = DetailUiState.Success(testPupil),
                editPupilState = EditPupilState(isDeleting = true),
            )

        composeTestRule.setContent {
            TechnicalTestTheme {
                DetailScreen(
                    uiState = deletingState,
                    onNavigateBack = {},
                    onViewEvent = {
                        // do nothing
                    },
                )
            }
        }

        composeTestRule.onNode(hasContentDescription("Delete Pupil")).assertDoesNotExist()
    }

    @Test
    fun detailScreen_hidesFabAndDeleteWhenNotInSuccessState() {
        val loadingState =
            DetailScreenState(
                uiState = DetailUiState.Loading,
                editPupilState = EditPupilState(),
            )

        composeTestRule.setContent {
            TechnicalTestTheme {
                DetailScreen(
                    uiState = loadingState,
                    onNavigateBack = {},
                    onViewEvent = {
                        // do nothing
                    },
                )
            }
        }

        composeTestRule.onNode(hasContentDescription("Edit Pupil")).assertDoesNotExist()
        composeTestRule.onNode(hasContentDescription("Delete Pupil")).assertDoesNotExist()
    }

    @Test
    fun detailScreen_editDialogShowsUpdatingState() {
        val testPupil = TestDataUtils.createTestPupilEntity()
        val updatingState =
            DetailScreenState(
                uiState = DetailUiState.Success(testPupil),
                editPupilState = EditPupilState(showDialog = true, isUpdating = true),
            )

        composeTestRule.setContent {
            TechnicalTestTheme {
                DetailScreen(
                    uiState = updatingState,
                    onNavigateBack = {},
                    onViewEvent = {
                        // do nothing
                    },
                )
            }
        }

        composeTestRule.onNodeWithText("Edit Pupil").assertIsDisplayed()
        composeTestRule.onNode(hasText("Save")).assertDoesNotExist()
    }
}
