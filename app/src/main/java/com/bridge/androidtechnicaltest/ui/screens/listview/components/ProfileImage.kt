package com.bridge.androidtechnicaltest.ui.screens.listview.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.bridge.androidtechnicaltest.utils.shimmerLoading

@Composable
fun ProfileImage(
    url: String?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    val painter =
        rememberAsyncImagePainter(
            model =
                ImageRequest
                    .Builder(LocalContext.current)
                    .data(url)
                    .crossfade(true)
                    .build(),
        )

    val state by painter.state.collectAsState()

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        when {
            url.isNullOrBlank() -> {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = contentDescription,
                    tint = Color.Gray,
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                )
            }

            state is AsyncImagePainter.State.Loading -> {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .shimmerLoading(),
                )
            }

            state is AsyncImagePainter.State.Error -> {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Image failed to load",
                    tint = Color.Gray,
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                )
            }

            state is AsyncImagePainter.State.Success -> {
                Image(
                    painter = painter,
                    contentDescription = contentDescription,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }
        }
    }
}
