package com.bridge.androidtechnicaltest.ui.screens.detailview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bridge.androidtechnicaltest.domain.entity.PupilEntity
import com.bridge.androidtechnicaltest.ui.common.ToastHandler
import com.bridge.androidtechnicaltest.ui.screens.common.ProfileImage
import com.bridge.androidtechnicaltest.ui.screens.common.PupilFormDialog

@Composable
fun DetailView(
    onNavigateBack: () -> Unit,
    viewModel: DetailViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    ToastHandler(viewModel.toastEvents)

    val latestOnNavigateBack by rememberUpdatedState(onNavigateBack)

    LaunchedEffect(uiState.value.uiState) {
        val currentState = uiState.value.uiState
        if (currentState is DetailUiState.Error) {
            latestOnNavigateBack()
        }
    }

    DetailScreen(
        uiState = uiState.value,
        onNavigateBack = onNavigateBack,
        onViewEvent = viewModel::onViewEvent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    uiState: DetailScreenState,
    onNavigateBack: () -> Unit,
    onViewEvent: (DetailViewEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            DetailTopAppBar(
                uiState = uiState,
                onNavigateBack = onNavigateBack,
                onDeletePupil = { onViewEvent(DetailViewEvent.DeletePupil) },
            )
        },
        floatingActionButton = {
            DetailFloatingActionButton(
                uiState = uiState,
                onShowEditDialog = { onViewEvent(DetailViewEvent.ShowEditDialog) },
            )
        },
    ) { paddingValues ->
        DetailContent(
            uiState = uiState,
            paddingValues = paddingValues,
        )

        DetailDialog(
            uiState = uiState,
            onDismissDialog = { onViewEvent(DetailViewEvent.DismissDialog) },
            onUpdatePupil = { name, country, imageUrl, latitude, longitude ->
                onViewEvent(
                    DetailViewEvent.UpdatePupil(
                        name = name,
                        country = country,
                        image = imageUrl,
                        latitude = latitude,
                        longitude = longitude,
                    ),
                )
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailTopAppBar(
    uiState: DetailScreenState,
    onNavigateBack: () -> Unit,
    onDeletePupil: () -> Unit,
) {
    TopAppBar(
        title = { Text("Pupil Details") },
        navigationIcon = {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.testTag("detail_back_button"),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                )
            }
        },
        actions = {
            when (uiState.uiState) {
                is DetailUiState.Success -> {
                    IconButton(
                        onClick = onDeletePupil,
                        enabled = !uiState.editPupilState.isDeleting,
                        modifier = Modifier.testTag("detail_delete_button"),
                    ) {
                        if (uiState.editPupilState.isDeleting) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        } else {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Pupil",
                            )
                        }
                    }
                }
                else -> {}
            }
        },
    )
}

@Composable
private fun DetailFloatingActionButton(
    uiState: DetailScreenState,
    onShowEditDialog: () -> Unit,
) {
    when (uiState.uiState) {
        is DetailUiState.Success -> {
            FloatingActionButton(
                onClick = onShowEditDialog,
                modifier = Modifier.testTag("detail_edit_fab"),
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Pupil",
                )
            }
        }
        else -> {}
    }
}

@Composable
private fun DetailContent(
    uiState: DetailScreenState,
    paddingValues: androidx.compose.foundation.layout.PaddingValues,
) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(paddingValues),
    ) {
        when (val state = uiState.uiState) {
            is DetailUiState.Loading -> {
                CircularProgressIndicator()
            }
            is DetailUiState.Error -> {
                Text(
                    text = state.message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            is DetailUiState.Success -> {
                PupilDetailContent(
                    pupil = state.pupil,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
private fun DetailDialog(
    uiState: DetailScreenState,
    onDismissDialog: () -> Unit,
    onUpdatePupil: (String, String, String, Double, Double) -> Unit,
) {
    when (val state = uiState.uiState) {
        is DetailUiState.Success -> {
            if (uiState.editPupilState.showDialog) {
                PupilFormDialog(
                    onDismiss = onDismissDialog,
                    onSave = { name, country, imageUrl, latitude, longitude ->
                        onUpdatePupil(name, country, imageUrl, latitude, longitude)
                    },
                    pupil = state.pupil,
                    isLoading = uiState.editPupilState.isUpdating,
                )
            }
        }
        else -> {}
    }
}

@Composable
private fun PupilDetailContent(
    pupil: PupilEntity,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        //        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfileImage(
            url = pupil.image,
            contentDescription = "Profile picture of ${pupil.name}",
            modifier =
                Modifier
                    .size(120.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(CircleShape)
                    .testTag("detail_profile_image"),
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = pupil.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(16.dp))

        DetailItem(
            label = "Country",
            value = pupil.country,
        )

        Spacer(modifier = Modifier.height(12.dp))

        DetailItem(
            label = "Latitude",
            value = pupil.latitude.toString(),
        )

        Spacer(modifier = Modifier.height(12.dp))

        DetailItem(
            label = "Longitude",
            value = pupil.longitude.toString(),
        )
    }
}

@Composable
private fun DetailItem(
    label: String,
    value: String,
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}
