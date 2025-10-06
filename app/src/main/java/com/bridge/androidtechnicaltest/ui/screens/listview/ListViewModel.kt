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
        private val _addOrEditPupilState = MutableStateFlow(AddorEditPupilState())

        val uiState =
            combine(
                getPupilsWithLocationUseCase(),
                getSyncStateUseCase(),
                _addOrEditPupilState,
            ) { pupils, syncState, addOrEditPupilState ->
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
                    addorEditPupilState = addOrEditPupilState,
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

        fun syncPupils() =
            viewModelScope.launch {
                if (syncService.sync()) {
                    showSuccessToast("Sync completed successfully")
                } else {
                    showErrorToast("Sync failed. Please try again.")
                }
            }

        fun showAddDialog() {
            _addOrEditPupilState.value =
                _addOrEditPupilState.value.copy(
                    showDialog = true,
                    pupil = null,
                )
        }

        fun dismissDialog() {
            _addOrEditPupilState.value = AddorEditPupilState()
        }

        fun addPupil(
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

            _addOrEditPupilState.value =
                _addOrEditPupilState.value.copy(
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
                    _addOrEditPupilState.value = AddorEditPupilState()
                }
                else -> {
                    showErrorToast("Failed to add pupil. Please try again.")
                    _addOrEditPupilState.value =
                        _addOrEditPupilState.value.copy(
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
            _addOrEditPupilState.value =
                _addOrEditPupilState.value.copy(
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
                    _addOrEditPupilState.value = AddorEditPupilState()
                }
                else -> {
                    showErrorToast("Failed to update pupil. Please try again.")
                    _addOrEditPupilState.value =
                        _addOrEditPupilState.value.copy(
                            isUpdating = false,
                        )
                }
            }
        }
    }
