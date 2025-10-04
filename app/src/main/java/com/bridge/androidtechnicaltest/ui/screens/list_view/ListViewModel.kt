package com.bridge.androidtechnicaltest.ui.screens.list_view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bridge.androidtechnicaltest.domain.SyncState
import com.bridge.androidtechnicaltest.domain.usecase.GetPupilsUseCase
import com.bridge.androidtechnicaltest.domain.usecase.GetPupilsWithLocationUseCase
import com.bridge.androidtechnicaltest.domain.usecase.GetSyncStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    getPupilsWithLocationUseCase: GetPupilsWithLocationUseCase,
    getSyncStateUseCase: GetSyncStateUseCase
): ViewModel() {

    val uiState = combine(
        getPupilsWithLocationUseCase(),
        getSyncStateUseCase()
    ) { pupils, syncState ->
        val listState = if (pupils.isEmpty() && syncState == SyncState.SYNCING) {
            ListUiState.Loading
        } else if (pupils.isEmpty() && syncState == SyncState.OUT_OF_DATE) {
            ListUiState.Error("No data available. Please sync to get the latest data.")
        } else if (pupils.isEmpty() && syncState == SyncState.UP_TO_DATE) {
            ListUiState.Empty
        } else {
            ListUiState.Success(
                pupils = pupils.map(PupilItem.Companion::fromEntity),
            )
        }
        ListScreenState(
            uiState = listState,
            syncState = syncState
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        ListScreenState(
            uiState = ListUiState.Loading,
            syncState = SyncState.SYNCING
        )
    )

}