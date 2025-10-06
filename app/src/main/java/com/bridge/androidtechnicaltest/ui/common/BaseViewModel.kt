package com.bridge.androidtechnicaltest.ui.common

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

abstract class BaseViewModel : ViewModel() {
    private val _toastEvents = Channel<ToastEvent>(Channel.BUFFERED)
    val toastEvents: Flow<ToastEvent> = _toastEvents.receiveAsFlow()

    protected fun showToast(event: ToastEvent) {
        _toastEvents.trySend(event)
    }

    protected fun showSuccessToast(message: String) {
        showToast(ToastEvent.Success(message))
    }

    protected fun showErrorToast(message: String) {
        showToast(ToastEvent.Error(message))
    }

    protected fun showInfoToast(message: String) {
        showToast(ToastEvent.Info(message))
    }
}
