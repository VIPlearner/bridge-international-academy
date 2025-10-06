package com.bridge.androidtechnicaltest.ui.screens.common

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
import androidx.compose.runtime.remember
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
    var formState by remember {
        mutableStateOf(
            PupilFormState(
                name = pupil?.name ?: "",
                country = pupil?.country ?: "",
                imageUrl = pupil?.image ?: "",
                latitude = pupil?.latitude?.toString() ?: "",
                longitude = pupil?.longitude?.toString() ?: "",
            ),
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.testTag("pupil_form_dialog"),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
            ) {
                PupilFormHeader(pupil = pupil)

                Spacer(modifier = Modifier.height(16.dp))

                PupilFormFields(
                    formState = formState,
                    onFormStateChange = { formState = it },
                    isLoading = isLoading,
                )

                Spacer(modifier = Modifier.height(16.dp))

                PupilFormActions(
                    formState = formState,
                    onDismiss = onDismiss,
                    onSave = onSave,
                    isLoading = isLoading,
                )
            }
        }
    }
}

@Composable
private fun PupilFormHeader(pupil: PupilEntity?) {
    Text(
        text = if (pupil == null) "Add Pupil" else "Edit Pupil",
        modifier = Modifier.testTag("pupil_form_title"),
    )
}

@Composable
private fun PupilFormFields(
    formState: PupilFormState,
    isLoading: Boolean,
    onFormStateChange: (PupilFormState) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = formState.name,
            onValueChange = { onFormStateChange(formState.copy(name = it)) },
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
            value = formState.country,
            onValueChange = { onFormStateChange(formState.copy(country = it)) },
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
            value = formState.imageUrl,
            onValueChange = { onFormStateChange(formState.copy(imageUrl = it)) },
            label = { Text("Image URL") },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .testTag("pupil_form_image_url_field"),
            enabled = !isLoading,
            singleLine = true,
        )

        Spacer(modifier = Modifier.height(8.dp))

        PupilLocationFields(
            formState = formState,
            onFormStateChange = onFormStateChange,
            isLoading = isLoading,
        )
    }
}

@Composable
private fun PupilLocationFields(
    formState: PupilFormState,
    isLoading: Boolean,
    onFormStateChange: (PupilFormState) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = formState.latitude,
            onValueChange = { onFormStateChange(formState.copy(latitude = it)) },
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
            value = formState.longitude,
            onValueChange = { onFormStateChange(formState.copy(longitude = it)) },
            label = { Text("Longitude") },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .testTag("pupil_form_longitude_field"),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            enabled = !isLoading,
            singleLine = true,
        )
    }
}

@Composable
private fun PupilFormActions(
    formState: PupilFormState,
    onDismiss: () -> Unit,
    onSave: (String, String, String, Double, Double) -> Unit,
    isLoading: Boolean,
) {
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
                val lat = formState.latitude.toDoubleOrNull() ?: 0.0
                val lng = formState.longitude.toDoubleOrNull() ?: 0.0
                onSave(formState.name, formState.country, formState.imageUrl, lat, lng)
            },
            enabled = !isLoading && formState.name.isNotBlank() && formState.country.isNotBlank(),
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

data class PupilFormState(
    val name: String = "",
    val country: String = "",
    val imageUrl: String = "",
    val latitude: String = "",
    val longitude: String = "",
)
