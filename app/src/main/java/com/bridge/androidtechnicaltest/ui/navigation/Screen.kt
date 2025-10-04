package com.bridge.androidtechnicaltest.ui.navigation

sealed class Screen(val route: String) {
    object List : Screen("list")
    object Detail : Screen("detail/{pupilId}") {
        fun createRoute(pupilId: String): String {
            return "detail/$pupilId"
        }
    }
}
