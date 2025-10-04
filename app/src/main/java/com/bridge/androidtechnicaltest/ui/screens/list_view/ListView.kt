package com.bridge.androidtechnicaltest.ui.screens.list_view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bridge.androidtechnicaltest.data.db.dto.PupilList

@Composable
fun ListView(
    onPupilClick: (pupilId: String) -> Unit,
    viewModel: ListViewModel = hiltViewModel<ListViewModel>()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

}

@Composable
fun ListScreen(
    uiState: ListScreenState,
    onPupilClick: (pupilId: Int) -> Unit
)  {
    Scaffold(
        topBar = {}
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (uiState.uiState) {
                is ListUiState.Loading -> {
                    Text(text = "Loading...")
                }
                is ListUiState.Empty -> {
                    Text(text = "No pupils available.")
                }
                is ListUiState.Success -> {
//                    PupilList(
//                        pupils = uiState.uiState.pupils,
//                        onPupilClick = onPupilClick
//                    )
                }
                is ListUiState.Error -> {
                    Text(text = "Error: ${uiState.uiState.message}")
                }
            }
        }

    }
}
