package com.bridge.androidtechnicaltest.ui.screens.listview.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.bridge.androidtechnicaltest.domain.entity.PupilEntity

@Composable
fun PupilFormDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, Double, Double) -> Unit,
    pupil: PupilEntity? = null,
    isLoading: Boolean = false,
) {
    var name by rememberSaveable { mutableStateOf(pupil?.name ?: "") }
    var country by rememberSaveable { mutableStateOf(pupil?.country ?: "") }
    var imageUrl by rememberSaveable { mutableStateOf(pupil?.image ?: "") }
    var latitude by rememberSaveable { mutableStateOf(pupil?.latitude?.toString() ?: "") }
    var longitude by rememberSaveable { mutableStateOf(pupil?.longitude?.toString() ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.testTag("pupil_form_dialog"),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
            ) {
                Text(
                    text = if (pupil == null) "Add Pupil" else "Edit Pupil",
                    modifier = Modifier.testTag("pupil_form_title"),
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .testTag("pupil_form_name_field"),
                    enabled = !isLoading,
                    singleLine = true,
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = country,
                    onValueChange = { country = it },
                    label = { Text("Country") },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .testTag("pupil_form_country_field"),
                    enabled = !isLoading,
                    singleLine = true,
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("Image URL") },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .testTag("pupil_form_image_url_field"),
                    enabled = !isLoading,
                    singleLine = true,
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = latitude,
                    onValueChange = { latitude = it },
                    label = { Text("Latitude") },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .testTag("pupil_form_latitude_field"),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    enabled = !isLoading,
                    singleLine = true,
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = longitude,
                    onValueChange = { longitude = it },
                    label = { Text("Longitude") },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .testTag("pupil_form_longitude_field"),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    enabled = !isLoading,
                    singleLine = true,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = onDismiss,
                        enabled = !isLoading,
                        modifier = Modifier.testTag("pupil_form_cancel_button"),
                    ) {
                        Text("Cancel")
                    }

                    TextButton(
                        onClick = {
                            val lat = latitude.toDoubleOrNull() ?: 0.0
                            val lng = longitude.toDoubleOrNull() ?: 0.0
                            onSave(name, country, imageUrl, lat, lng)
                        },
                        enabled = !isLoading && name.isNotBlank() && country.isNotBlank(),
                        modifier = Modifier.testTag("pupil_form_save_button"),
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.testTag("pupil_form_loading_indicator"),
                            )
                        } else {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }
}
