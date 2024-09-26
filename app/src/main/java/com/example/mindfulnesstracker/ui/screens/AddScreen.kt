package com.example.mindfulnesstracker.ui.screens

import android.text.format.DateFormat
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mindfulnesstracker.R
import com.example.mindfulnesstracker.ui.components.HabitScaffold
import com.example.mindfulnesstracker.ui.theme.ButtonBlue
import com.example.mindfulnesstracker.ui.viewmodels.AddScreenViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(onAdded: () -> Unit) {
    val viewModel: AddScreenViewModel = viewModel()

    HabitScaffold(
        screenTitle = stringResource(R.string.screen_title_add),
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .padding(paddingValues)
                    .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val isTimeDialogShown = remember { mutableStateOf(false) }
            val title = remember { mutableStateOf("") }
            val hour = remember { mutableStateOf(0) }
            val minute = remember { mutableStateOf(0) }

            OutlinedTextField(
                value = title.value,
                onValueChange = { title.value = it },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(stringResource(R.string.title))
                },
            )

            Spacer(Modifier.height(32.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier =
                    Modifier
                        .height(48.dp)
                        .clickable {
                            isTimeDialogShown.value = true
                        },
            ) {
                Text(
                    text = stringResource(R.string.reminder),
                    modifier = Modifier.weight(1f),
                )

                Text(
                    text = stringResource(R.string.time),
                )

                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR, hour.value)
                calendar.set(Calendar.MINUTE, minute.value)

                val format = DateFormat.getTimeFormat(LocalContext.current)
                Text(
                    text = format.format(calendar.time),
                )
            }
            if (isTimeDialogShown.value) {
                val state = rememberTimePickerState()
                val close = { isTimeDialogShown.value = false }
                AlertDialog(
                    onDismissRequest = close,
                    confirmButton = {
                        TextButton(onClick = {
                            hour.value = state.hour
                            minute.value = state.minute
                            close()
                        }) {
                            Text(text = stringResource(android.R.string.ok))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = close) {
                            Text(text = stringResource(android.R.string.cancel))
                        }
                    },
                    title = { Text(stringResource(R.string.reminder)) },
                    text = { TimeInput(state) },
                )
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    viewModel.save(
                        title = title.value,
                        hour = hour.value,
                        minute = minute.value,
                    )

                    onAdded()
                },
                modifier = Modifier.size(192.dp, 52.dp),
                shape = MaterialTheme.shapes.large,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = ButtonBlue,
                    ),
            ) {
                Text(text = stringResource(id = R.string.save))
            }
        }
    }
}
