package com.bridge.androidtechnicaltest.ui.screens.list_view.components

import android.R.attr.contentDescription
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.bridge.androidtechnicaltest.utils.shimmerLoading

@Composable
fun ListItem(
    modifier: Modifier = Modifier,
    pupilName: String,
    profileUrl: String,
    prettyLocation: String,
    onPupilClick: () -> Unit
) {
    Surface(
        modifier = modifier,
        onClick = onPupilClick,
    ) {
         Row(
             modifier = Modifier
                 .fillMaxWidth()
                 .padding(16.dp),
             verticalAlignment = Alignment.CenterVertically,
             horizontalArrangement = Arrangement.spacedBy(12.dp)
         ) {
             // Profile picture with person icon placeholder
             Box(
                 modifier = Modifier
                     .size(48.dp)
                     .clip(CircleShape)
                     .background(MaterialTheme.colorScheme.surfaceVariant),
                 contentAlignment = Alignment.Center
             ) {
                 ProfileImage(
                     url = profileUrl,
                     contentDescription = "Profile picture of $pupilName",
                     modifier = Modifier
                         .size(48.dp)
                         .clip(CircleShape),
                 )
             }

             Column(
                 verticalArrangement = Arrangement.spacedBy(4.dp)
             ) {
                 Text(
                     text = pupilName,
                     style = MaterialTheme.typography.bodyLarge,
                     fontWeight = FontWeight.Medium
                 )
                 Text(
                     text = prettyLocation,
                     style = MaterialTheme.typography.bodyMedium,
                     color = MaterialTheme.colorScheme.onSurfaceVariant
                 )
             }
         }
     }
}

@Composable
fun PlaceholderListItem(
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .shimmerLoading()
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 120.dp, height = 16.dp)
                        .shimmerLoading()
                )
                Box(
                    modifier = Modifier
                        .size(width = 80.dp, height = 14.dp)
                        .shimmerLoading()
                )
            }
        }
    }
}



@Preview(showSystemUi = false, showBackground = true)
@Composable
fun ListItemPreview() {
    val profileUrl = "https://plus.unsplash.com/premium_photo-1689568126014-06fea9d5d341?fm=jpg&q=60&w=3000&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MXx8cHJvZmlsZXxlbnwwfHwwfHx8MA%3D%3D"

    Column{
        ListItem(
            pupilName = "John Doe",
            profileUrl = profileUrl + "sdimn",
            prettyLocation = "New York, USA",
            onPupilClick = {}
        )
        PlaceholderListItem()
    }
}