package com.bridge.androidtechnicaltest.ui.screens.detailview

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.bridge.androidtechnicaltest.domain.entity.PupilEntity
import com.bridge.androidtechnicaltest.domain.usecase.DeletePupilUseCase
import com.bridge.androidtechnicaltest.domain.usecase.GetPupilsUseCase
import com.bridge.androidtechnicaltest.domain.usecase.UpdatePupilUseCase
import com.bridge.androidtechnicaltest.ui.common.BaseViewModel
import com.bridge.androidtechnicaltest.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel
    @Inject
    constructor(
        getPupilsUseCase: GetPupilsUseCase,
        private val updatePupilUseCase: UpdatePupilUseCase,
        private val deletePupilUseCase: DeletePupilUseCase,
        savedStateHandle: SavedStateHandle,
    ) : BaseViewModel() {
        private val pupilId = savedStateHandle.get<String>("pupilId")?.toIntOrNull() ?: -1
        private val editPupilState = MutableStateFlow(EditPupilState())

        val uiState =
            combine(
                getPupilsUseCase().map { pupils -> pupils.find { it.id == pupilId } },
                editPupilState,
            ) { pupil, editState ->
                val detailState =
                    when {
                        pupil != null -> DetailUiState.Success(pupil)
                        else -> DetailUiState.Error("Pupil not found")
                    }
                DetailScreenState(
                    uiState = detailState,
                    editPupilState = editState,
                )
            }.stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                DetailScreenState(uiState = DetailUiState.Loading),
            )

        fun showEditDialog() {
            editPupilState.value = editPupilState.value.copy(showDialog = true)
        }

        fun dismissDialog() {
            editPupilState.value = editPupilState.value.copy(showDialog = false)
        }

        fun updatePupil(
            name: String,
            country: String,
            image: String?,
            latitude: Double,
            longitude: Double,
        ) = viewModelScope.launch {
            editPupilState.value = editPupilState.value.copy(isUpdating = true)

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
                    editPupilState.value = EditPupilState()
                }
                else -> {
                    showErrorToast("Failed to update pupil. Please try again.")
                    editPupilState.value = editPupilState.value.copy(isUpdating = false)
                }
            }
        }

        fun deletePupil(onDeleted: () -> Unit) =
            viewModelScope.launch {
                editPupilState.value = editPupilState.value.copy(isDeleting = true)

                val result = deletePupilUseCase(pupilId)
                when (result) {
                    is Result.Success -> {
                        showSuccessToast("Pupil deleted successfully!")
                        onDeleted()
                    }
                    else -> {
                        showErrorToast("Failed to delete pupil. Please try again.")
                        editPupilState.value = editPupilState.value.copy(isDeleting = false)
                    }
                }
            }
    }
