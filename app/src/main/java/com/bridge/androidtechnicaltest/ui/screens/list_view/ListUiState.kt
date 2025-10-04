package com.bridge.androidtechnicaltest.ui.screens.list_view

import com.bridge.androidtechnicaltest.domain.entity.PupilEntity

enum class SyncState {
    UP_TO_DATE,
    OUT_OF_DATE,
    SYNCING
}
data class PupilItem(
    val id: Int,
    val name: String,
    val prettyLocation: String
)

sealed class ListUiState {
    object Loading : ListUiState()
    data class Success(
        val syncState: SyncState,
        val pupils: List<PupilItem>
    ) : ListUiState()
    data class Error(val message: String) : ListUiState()
}