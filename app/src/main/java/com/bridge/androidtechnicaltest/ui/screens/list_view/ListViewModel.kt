package com.bridge.androidtechnicaltest.ui.screens.list_view

import androidx.lifecycle.ViewModel
import com.bridge.androidtechnicaltest.domain.usecase.GetPupilsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    getPupilsUseCase: GetPupilsUseCase,

    ): ViewModel() {
}