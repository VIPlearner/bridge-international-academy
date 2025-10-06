package com.bridge.androidtechnicaltest.ui.screens.detailview

import com.bridge.androidtechnicaltest.domain.entity.PupilEntity

data class DetailScreenState(
    val uiState: DetailUiState,
    val editPupilState: EditPupilState = EditPupilState(),
)

data class EditPupilState(
    val showDialog: Boolean = false,
    val isUpdating: Boolean = false,
    val isDeleting: Boolean = false,
)

sealed interface DetailUiState {
    object Loading : DetailUiState

    data class Success(
        val pupil: PupilEntity,
    ) : DetailUiState

    data class Error(
        val message: String,
    ) : DetailUiState
}
