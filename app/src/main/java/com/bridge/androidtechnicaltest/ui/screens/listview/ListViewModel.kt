package com.bridge.androidtechnicaltest.ui.screens.listview

import androidx.lifecycle.viewModelScope
import com.bridge.androidtechnicaltest.data.sync.PupilSyncService
import com.bridge.androidtechnicaltest.domain.SyncState
import com.bridge.androidtechnicaltest.domain.entity.PupilEntity
import com.bridge.androidtechnicaltest.domain.usecase.AddPupilUseCase
import com.bridge.androidtechnicaltest.domain.usecase.GetPupilsWithLocationUseCase
import com.bridge.androidtechnicaltest.domain.usecase.GetSyncStateUseCase
import com.bridge.androidtechnicaltest.domain.usecase.UpdatePupilUseCase
import com.bridge.androidtechnicaltest.ui.common.BaseViewModel
import com.bridge.androidtechnicaltest.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ListViewEvent {
    object Sync : ListViewEvent()

    object ShowAddDialog : ListViewEvent()

    object DismissDialog : ListViewEvent()

    data class AddPupil(
        val name: String,
        val country: String,
        val image: String?,
        val latitude: Double,
        val longitude: Double,
    ) : ListViewEvent()
}

@HiltViewModel
class ListViewModel
    @Inject
    constructor(
        private val addPupilUseCase: AddPupilUseCase,
        private val updatePupilUseCase: UpdatePupilUseCase,
        getPupilsWithLocationUseCase: GetPupilsWithLocationUseCase,
        getSyncStateUseCase: GetSyncStateUseCase,
        private val syncService: PupilSyncService,
    ) : BaseViewModel() {
        private val addOrEditPupilState = MutableStateFlow(AddorEditPupilState())

        val uiState =
            combine(
                getPupilsWithLocationUseCase(),
                getSyncStateUseCase(),
                addOrEditPupilState,
            ) { pupils, syncState, addOrEditPupilState_ ->
                val listState =
                    if (pupils.isEmpty() && syncState == SyncState.SYNCING) {
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
                    addorEditPupilState = addOrEditPupilState_,
                    uiState = listState,
                    syncState = syncState,
                )
            }.stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                ListScreenState(
                    addorEditPupilState = AddorEditPupilState(),
                    uiState = ListUiState.Loading,
                    syncState = SyncState.SYNCING,
                ),
            )

        fun onViewEvent(event: ListViewEvent) {
            when (event) {
                is ListViewEvent.Sync -> syncPupils()
                is ListViewEvent.ShowAddDialog -> showAddDialog()
                is ListViewEvent.DismissDialog -> dismissDialog()
                is ListViewEvent.AddPupil ->
                    addPupil(
                        event.name,
                        event.country,
                        event.image,
                        event.latitude,
                        event.longitude,
                    )
            }
        }

        private fun syncPupils() =
            viewModelScope.launch {
                if (syncService.sync()) {
                    showSuccessToast("Sync completed successfully")
                } else {
                    showErrorToast("Sync failed. Please try again.")
                }
            }

        private fun showAddDialog() {
            addOrEditPupilState.value =
                addOrEditPupilState.value.copy(
                    showDialog = true,
                    pupil = null,
                )
        }

        private fun dismissDialog() {
            addOrEditPupilState.value = AddorEditPupilState()
        }

        private fun addPupil(
            name: String,
            country: String,
            image: String?,
            latitude: Double,
            longitude: Double,
        ) = viewModelScope.launch {
            val pupilEntity =
                PupilEntity(
                    name = name,
                    country = country,
                    image = image,
                    latitude = latitude,
                    longitude = longitude,
                )

            addOrEditPupilState.value =
                addOrEditPupilState.value.copy(
                    isUpdating = true,
                    pupil = pupilEntity,
                )

            val res =
                addPupilUseCase(
                    PupilEntity(
                        name = name,
                        country = country,
                        image = image,
                        latitude = latitude,
                        longitude = longitude,
                    ),
                )

            when (res) {
                is Result.Success -> {
                    showSuccessToast("Pupil added successfully!")
                    addOrEditPupilState.value = AddorEditPupilState()
                }
                else -> {
                    showErrorToast("Failed to add pupil. Please try again.")
                    addOrEditPupilState.value =
                        addOrEditPupilState.value.copy(
                            isUpdating = false,
                        )
                }
            }
        }

        fun updatePupil(
            pupilId: Int,
            name: String,
            country: String,
            image: String?,
            latitude: Double,
            longitude: Double,
        ) = viewModelScope.launch {
            addOrEditPupilState.value =
                addOrEditPupilState.value.copy(
                    isUpdating = true,
                )

            val updatedPupil =
                PupilEntity(
                    id = pupilId,
                    name = name,
                    country = country,
                    image = image,
                    latitude = latitude,
                    longitude = longitude,
                )

            val result = updatePupilUseCase(updatedPupil)
            when (result) {
                is Result.Success -> {
                    showSuccessToast("Pupil updated successfully!")
                    addOrEditPupilState.value = AddorEditPupilState()
                }
                else -> {
                    showErrorToast("Failed to update pupil. Please try again.")
                    addOrEditPupilState.value =
                        addOrEditPupilState.value.copy(
                            isUpdating = false,
                        )
                }
            }
        }
    }
