package com.bridge.androidtechnicaltest.ui.screens.listview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bridge.androidtechnicaltest.R
import com.bridge.androidtechnicaltest.ui.common.ToastHandler
import com.bridge.androidtechnicaltest.ui.screens.listview.components.ListItem
import com.bridge.androidtechnicaltest.ui.screens.listview.components.PlaceholderListItem
import com.bridge.androidtechnicaltest.ui.screens.listview.components.PupilFormDialog

@Composable
fun ListView(
    onPupilClick: (pupilId: String) -> Unit,
    viewModel: ListViewModel = hiltViewModel<ListViewModel>(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    ToastHandler(viewModel.toastEvents)

    ListScreen(
        uiState = uiState.value,
        onPupilClick = { pupilId -> onPupilClick(pupilId.toString()) },
        onSync = { viewModel.syncPupils() },
        onShowAddDialog = { viewModel.showAddDialog() },
        onDismissDialog = { viewModel.dismissDialog() },
        onSavePupil = { name, country, imageUrl, latitude, longitude ->
            viewModel.addPupil(name, country, imageUrl, latitude, longitude)
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    uiState: ListScreenState,
    onPupilClick: (pupilId: Int) -> Unit,
    onSync: () -> Unit,
    onShowAddDialog: () -> Unit,
    onDismissDialog: () -> Unit,
    onSavePupil: (String, String, String, Double, Double) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    Icon(
                        painter = painterResource(R.drawable.banner),
                        contentDescription = null,
                        modifier = Modifier,
                        tint = Color.Unspecified,
                    )
                },
                title = {
                    Text(text = "Bridge International")
                },
                actions = {
                    when (uiState.syncState) {
                        com.bridge.androidtechnicaltest.domain.SyncState.SYNCING -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                            )
                        }
                        else -> {
                            IconButton(onClick = onSync) {
                                Icon(
                                    imageVector = Icons.Default.Sync,
                                    contentDescription = "Sync",
                                )
                            }
                        }
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onShowAddDialog,
                modifier = Modifier.testTag("add_pupil_fab"),
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Pupil",
                )
            }
        },
    ) { paddingValues ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            contentAlignment = Alignment.Center,
        ) {
            when (uiState.uiState) {
                is ListUiState.Loading -> {
                    PlaceholderListView()
                }
                is ListUiState.Empty -> {
                    Text(
                        text = "No pupils available.",
                        modifier = Modifier.testTag("no_pupils_text"),
                    )
                }
                is ListUiState.Success -> {
                    PupilListView(
                        pupils = uiState.uiState.pupils,
                        onPupilClick = onPupilClick,
                    )
                }
                is ListUiState.Error -> {
                    Column {
                        Text(text = "Error: ${uiState.uiState.message}")
                        TextButton(
                            onClick = {
                                onSync()
                            },
                        ) {
                            Text(text = "Retry")
                        }
                    }
                }
            }
        }

        if (uiState.addorEditPupilState.showDialog) {
            PupilFormDialog(
                onDismiss = onDismissDialog,
                onSave = { name, country, imageUrl, latitude, longitude ->
                    onSavePupil(name, country, imageUrl, latitude, longitude)
                },
                isLoading = uiState.addorEditPupilState.isUpdating,
            )
        }
    }
}

@Composable
fun PupilListView(
    pupils: List<PupilItem>,
    onPupilClick: (pupilId: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
    ) {
        items(pupils) { pupil ->
            ListItem(
                onPupilClick = { onPupilClick(pupil.id) },
                pupilName = pupil.name,
                profileUrl = pupil.imageUrl,
                prettyLocation = pupil.prettyLocation,
            )
        }
    }
}

@Composable
fun PlaceholderListView(
    modifier: Modifier = Modifier,
    count: Int = 6,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
    ) {
        items(count) {
            PlaceholderListItem()
        }
    }
}
