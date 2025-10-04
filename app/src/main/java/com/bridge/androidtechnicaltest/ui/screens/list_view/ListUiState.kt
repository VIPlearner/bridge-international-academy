package com.bridge.androidtechnicaltest.ui.screens.list_view

import com.bridge.androidtechnicaltest.domain.entity.PupilEntity
import com.bridge.androidtechnicaltest.domain.SyncState
import com.bridge.androidtechnicaltest.domain.entity.PupilWithLocationEntity

data class PupilItem(
    val id: Int,
    val name: String,
    val prettyLocation: String
) {
    companion object {
        fun fromEntity(entity: PupilWithLocationEntity) = PupilItem(
            id = entity.id,
            name = entity.name,
            prettyLocation = entity.prettyLocation ?: ""
        )
    }
}

sealed class ListUiState {
    object Loading : ListUiState()
    object Empty : ListUiState()
    data class Success(
        val pupils: List<PupilItem>
    ) : ListUiState()
    data class Error(val message: String) : ListUiState()
}

data class ListScreenState(
    val uiState: ListUiState,
    val syncState: SyncState
)