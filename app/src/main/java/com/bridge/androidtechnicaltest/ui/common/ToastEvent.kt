package com.bridge.androidtechnicaltest.ui.common

sealed class ToastEvent {
    data class Success(
        val message: String,
    ) : ToastEvent()

    data class Error(
        val message: String,
    ) : ToastEvent()

    data class Info(
        val message: String,
    ) : ToastEvent()
}
