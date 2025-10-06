package com.bridge.androidtechnicaltest.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.bridge.androidtechnicaltest.ui.screens.detailview.DetailView
import com.bridge.androidtechnicaltest.ui.screens.listview.ListView

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.List.route,
    ) {
        composable(Screen.List.route) {
            ListView(
                onPupilClick = { pupilId: String ->
                    navController.navigate(Screen.Detail.createRoute(pupilId))
                },
            )
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("pupilId") { type = NavType.StringType }),
        ) { backStackEntry ->
            DetailView(
                onNavigateBack = {
                    navController.popBackStack()
                },
            )
        }
    }
}
