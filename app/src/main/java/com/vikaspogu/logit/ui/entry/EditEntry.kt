package com.vikaspogu.logit.ui.entry

import android.app.TimePickerDialog
import android.content.Context
import android.icu.util.Calendar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.vikaspogu.logit.ui.NavigationDestinations
import com.vikaspogu.logit.ui.components.TopBar
import kotlinx.coroutines.launch

@Composable
fun EditEntry(navController: NavHostController, viewModel: EditEntryViewModel, modifier: Modifier) {

    Scaffold(topBar = {
        TopBar(true, navController, NavigationDestinations.Entries)
    }) { innerPadding ->
        EditForm(
            modifier = modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding(),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                )
                .fillMaxWidth(),
            navController = navController,
            viewModel::updateUiState,
            viewModel.addEntryUiState.addEntry,
            viewModel,
        )
    }
}


@Composable
fun EditForm(
    modifier: Modifier,
    navController: NavHostController,
    onValueChange: (AddEntry) -> Unit = {},
    addEntry: AddEntry,
    viewModel: EditEntryViewModel,
) {
    val typesUiState by viewModel.typeUiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = modifier.padding(10.dp)
    ) {
        OutlinedTextField(
            value = addEntry.attendingName,
            onValueChange = { onValueChange(addEntry.copy(attendingName = it)) },
            modifier = Modifier.fillMaxWidth(),
            enabled = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            label = { Text(text = "Attending") }
        )
        OutlinedTextField(
            value = addEntry.age.toString(),
            onValueChange = { onValueChange(addEntry.copy(age = it.toInt())) },
            modifier = Modifier.fillMaxWidth(),
            enabled = true,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            label = { Text(text = "Age") }
        )
        OutlinedTextField(
            value = addEntry.quantity.toString(),
            onValueChange = { onValueChange(addEntry.copy(quantity = it.toInt())) },
            modifier = Modifier.fillMaxWidth(),
            enabled = true,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            label = { Text(text = "Quantity") }
        )
        OutlinedTextField(
            value = addEntry.notes,
            onValueChange = { onValueChange(addEntry.copy(notes = it)) },
            modifier = Modifier.fillMaxWidth(),
            enabled = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            label = { Text(text = "Notes") }
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = addEntry.entryDate.fullDate(),
                onValueChange = { },
                modifier = Modifier.fillMaxWidth(),
                enabled = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
                label = { Text(text = "Date") },
                trailingIcon = {
                    IconButton(onClick = {
                        showDatePicker(
                            Calendar.getInstance().apply { timeInMillis = addEntry.entryDate },
                            context,
                            onDateSelected = {
                                addEntry.entryDate = it
                            }
                        )
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.DateRange,
                            contentDescription = "Date"
                        )
                    }
                }
            )

        }
        Spacer(Modifier.height(10.dp))
        Dropdown(
            items = typesUiState.types,
            label = "Procedure Type",
            addEntry
        )
        Spacer(Modifier.height(20.dp))
        Button(onClick = {
            coroutineScope.launch {
                viewModel.updateEntry()
            }
            navController.navigate(NavigationDestinations.Summary.name)
        }, shape = MaterialTheme.shapes.large, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Update")
        }
    }
}

private fun showDatePicker(
    date: Calendar,
    context: Context,
    onDateSelected: (Long) -> Unit
) {

    val tempDate = Calendar.getInstance()
    val timePicker = TimePickerDialog(
        context,
        { _, hour, minute ->
            tempDate[Calendar.HOUR_OF_DAY] = hour
            tempDate[Calendar.MINUTE] = minute
            onDateSelected(tempDate.timeInMillis)
        }, date[Calendar.HOUR_OF_DAY], date[Calendar.MINUTE], false
    )
    val datePicker = android.app.DatePickerDialog(
        context,
        { _, year, month, day ->
            tempDate[Calendar.YEAR] = year
            tempDate[Calendar.MONTH] = month
            tempDate[Calendar.DAY_OF_MONTH] = day
            timePicker.show()
        },
        date[Calendar.YEAR],
        date[Calendar.MONTH],
        date[Calendar.DAY_OF_MONTH]
    )
    datePicker.show()
}
