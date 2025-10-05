package com.bridge.androidtechnicaltest.ui.screens.list_view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bridge.androidtechnicaltest.R
import com.bridge.androidtechnicaltest.data.db.dto.PupilList
import com.bridge.androidtechnicaltest.ui.screens.list_view.components.ListItem
import com.bridge.androidtechnicaltest.ui.screens.list_view.components.PlaceholderListItem

@Composable
fun ListView(
    onPupilClick: (pupilId: String) -> Unit,
    viewModel: ListViewModel = hiltViewModel<ListViewModel>()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    ListScreen(
        uiState = uiState.value,
        onPupilClick = { pupilId -> onPupilClick(pupilId.toString()) },
        onSync = { viewModel.syncPupils() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    uiState: ListScreenState,
    onPupilClick: (pupilId: Int) -> Unit,
    onSync: () -> Unit
)  {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    Icon(
                        painter = painterResource(R.drawable.banner),
                        contentDescription = null,
                        modifier = Modifier,
                        tint = Color.Unspecified
                    )
                },
                title = {
                    Text (text = "Bridge International")
                }
            )
        },
        floatingActionButton = {

        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (uiState.uiState) {
                is ListUiState.Loading -> {
                    PlaceholderListView()
                }
                is ListUiState.Empty -> {
                    Text(text = "No pupils available.")
                }
                is ListUiState.Success -> {
                    PupilListView(
                        pupils = uiState.uiState.pupils,
                        onPupilClick = onPupilClick
                    )
                }
                is ListUiState.Error -> {
                    Column{
                        Text(text = "Error: ${uiState.uiState.message}")
                        TextButton(
                            onClick = {
                                onSync()
                            }
                        ) {
                            Text (text = "Retry" )
                        }
                    }
                }
            }
        }

    }
}

@Composable
fun PupilListView(
    modifier: Modifier = Modifier,
    pupils: List<PupilItem>,
    onPupilClick: (pupilId: Int) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        items(pupils) { pupil ->
            ListItem(
                pupilName = pupil.name,
                profileUrl = pupil.imageUrl,
                prettyLocation = pupil.prettyLocation,
                onPupilClick = { onPupilClick(pupil.id) }
            )
        }
    }
}

@Composable
fun PlaceholderListView(
    modifier: Modifier = Modifier,
    count: Int = 6
) {
    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        items(count) {
            PlaceholderListItem()
        }
    }
}
