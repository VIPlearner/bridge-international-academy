package com.bridge.androidtechnicaltest.ui.common

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.Flow
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

@Composable
fun ToastHandler(toastEvents: Flow<ToastEvent>) {
    val context = LocalContext.current as Activity

    LaunchedEffect(toastEvents) {
        toastEvents.collect { event ->
            when (event) {
                is ToastEvent.Success -> {
                    MotionToast.createToast(
                        context,
                        "Success 😍",
                        event.message,
                        MotionToastStyle.SUCCESS,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        null,
                    )
                }
                is ToastEvent.Error -> {
                    MotionToast.createToast(
                        context,
                        "Error ☹️",
                        event.message,
                        MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        null,
                    )
                }
                is ToastEvent.Info -> {
                    MotionToast.createToast(
                        context,
                        "Info",
                        event.message,
                        MotionToastStyle.INFO,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        null,
                    )
                }
            }
        }
    }
}
