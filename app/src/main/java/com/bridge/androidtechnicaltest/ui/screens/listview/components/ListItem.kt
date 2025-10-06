package com.bridge.androidtechnicaltest.ui.screens.listview.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bridge.androidtechnicaltest.ui.screens.common.ProfileImage
import com.bridge.androidtechnicaltest.utils.shimmerLoading

@Composable
fun ListItem(
    onPupilClick: () -> Unit,
    pupilName: String,
    profileUrl: String,
    prettyLocation: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.testTag("list_item_surface"),
        onClick = onPupilClick,
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("list_item_row"),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Profile picture with person icon placeholder
            Box(
                modifier =
                    Modifier
                        .size(48.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .testTag("list_item_profile_box"),
                contentAlignment = Alignment.Center,
            ) {
                ProfileImage(
                    url = profileUrl,
                    contentDescription = "Profile picture of $pupilName",
                    modifier =
                        Modifier
                            .clip(CircleShape)
                            .testTag("list_item_profile_image"),
                )
            }

            Column(
                modifier = Modifier.testTag("list_item_info_column"),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = pupilName,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.testTag("list_item_name"),
                )
                Text(
                    text = prettyLocation,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.testTag("list_item_location"),
                )
            }
        }
    }
}

@Composable
fun PlaceholderListItem(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier =
                    Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .shimmerLoading(),
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(width = 120.dp, height = 16.dp)
                            .shimmerLoading(),
                )
                Box(
                    modifier =
                        Modifier
                            .size(width = 80.dp, height = 14.dp)
                            .shimmerLoading(),
                )
            }
        }
    }
}

@Preview(showSystemUi = false, showBackground = true)
@Composable
private fun ListItemPreview() {
    val profileUrl =
        "https://plus.unsplash.com/premium_photo-1689568126014-06fea9d5d341?fm=jpg&q=" +
            "60&w=3000&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MXx8cHJvZmlsZXxlbnwwfHwwfHx8MA%" +
            "3D%3D"

    Column {
        ListItem(
            pupilName = "John Doe",
            profileUrl = profileUrl + "sdimn",
            prettyLocation = "New York, USA",
            onPupilClick = {},
        )
        PlaceholderListItem()
    }
}
