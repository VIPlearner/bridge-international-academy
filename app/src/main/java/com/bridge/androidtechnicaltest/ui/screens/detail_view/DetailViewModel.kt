package com.bridge.androidtechnicaltest.ui.screens.detail_view

import androidx.lifecycle.ViewModel
import com.bridge.androidtechnicaltest.domain.usecase.UpdatePupilUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    updatePupilUseCase: UpdatePupilUseCase,
): ViewModel() {
}