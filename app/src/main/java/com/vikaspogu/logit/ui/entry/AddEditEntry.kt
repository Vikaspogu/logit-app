package com.vikaspogu.logit.ui.entry

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.vikaspogu.logit.R
import com.vikaspogu.logit.data.model.Attending
import com.vikaspogu.logit.data.model.Type
import com.vikaspogu.logit.ui.NavigationDestinations
import com.vikaspogu.logit.ui.components.TopBar
import com.vikaspogu.logit.ui.util.Constants
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun AddEditEntry(
    navController: NavHostController,
    modifier: Modifier,
    viewModel: AddEntryViewModel = hiltViewModel(),
) {
    Scaffold(topBar = {
        TopBar(true, navController, NavigationDestinations.Summary)
    }) { innerPadding ->
        LazyColumn {
            item {
                AddEditForm(
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
                    viewModel
                )
            }
        }

    }
}


@Composable
fun AddEditForm(
    modifier: Modifier,
    navController: NavHostController,
    onValueChange: (AddEntry) -> Unit = {},
    addEntry: AddEntry,
    viewModel: AddEntryViewModel
) {
    val typesUiState by viewModel.typeUiState.collectAsState()
    val attendingUiState by viewModel.attendingUiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    if (addEntry.gender.isEmpty()) {
        addEntry.gender = "Male"
    }
    val radioOptions = listOf("Male", "Female")
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(addEntry.gender) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = modifier.padding(10.dp)
    ) {
        DropdownAttending(
            items = attendingUiState.attending,
            label = stringResource(R.string.attending),
            addEntry,
            viewModel
        )
        OutlinedTextField(
            value = addEntry.age,
            onValueChange = { onValueChange(addEntry.copy(age = it)) },
            modifier = Modifier.fillMaxWidth(),
            enabled = true,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = {
                Text(
                    text = stringResource(R.string.age),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            radioOptions.forEach { text ->
                Row(
                    Modifier
                        .height(56.dp)
                        .selectable(
                            selected = (text == addEntry.gender),
                            onClick = {
                                onOptionSelected(text)
                                addEntry.gender = text
                            },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (text == selectedOption),
                        onClick = null
                    )
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }
        OutlinedTextField(
            value = addEntry.quantity,
            onValueChange = { onValueChange(addEntry.copy(quantity = it)) },
            modifier = Modifier.fillMaxWidth(),
            enabled = true,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = {
                Text(
                    text = stringResource(R.string.quantity),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        )
        OutlinedTextField(
            value = addEntry.notes,
            onValueChange = { onValueChange(addEntry.copy(notes = it)) },
            modifier = Modifier.fillMaxWidth(),
            enabled = true,
            label = {
                Text(
                    text = stringResource(R.string.notes),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = addEntry.entryDate.formatDate(),
                onValueChange = { },
                modifier = Modifier.fillMaxWidth(),
                enabled = true,
                label = {
                    Text(
                        text = stringResource(R.string.date),
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
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
                            contentDescription = stringResource(R.string.date)
                        )
                    }
                }
            )

        }
        Spacer(Modifier.height(10.dp))
        Dropdown(
            items = typesUiState.types,
            label = stringResource(R.string.procedure_type),
            addEntry,
            viewModel
        )
        Spacer(Modifier.height(20.dp))
        if (viewModel.action == Constants.ADD) {
            Button(
                onClick = {
                    coroutineScope.launch {
                        viewModel.saveEntry()
                    }
                    navController.navigate(NavigationDestinations.Summary.name)
                }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text(text = stringResource(id = R.string.save))
            }
        } else if (viewModel.action == Constants.EDIT) {
            Button(
                onClick = {
                    coroutineScope.launch {
                        viewModel.updateEntry()
                    }
                    navController.navigate(NavigationDestinations.Entries.name)
                }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text(text = stringResource(id = R.string.update))
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownAttending(
    items: List<Attending>,
    label: String,
    addEntry: AddEntry,
    viewModel: AddEntryViewModel
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    var openDialog by rememberSaveable { mutableStateOf(false) }
    var attendingName by remember {
        mutableStateOf("")
    }

    val coroutineScope = rememberCoroutineScope()

    val editType = items.find { it.id == addEntry.attendingId }
    if (editType?.name?.isNotEmpty() == true) {
        LaunchedEffect(viewModel.selectedAttending) {
            viewModel.updateSelectedAttending(editType.name)
        }
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        TextField(
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            value = viewModel.selectedAttending.value,
            label = { Text(text = label, style = MaterialTheme.typography.bodyMedium) },
            onValueChange = { viewModel.updateSelectedAttending(it) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
        )

        val filteredOptions =
            items.filter { it.name.contains(viewModel.selectedAttending.value, ignoreCase = true) }
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { },
        ) {
            if (filteredOptions.isEmpty()) {
                IconButton(onClick = { openDialog = true }, Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Add, contentDescription = stringResource(
                                id = R.string.add
                            )
                        )
                        Text(
                            text = stringResource(R.string.add),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            filteredOptions.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item.name, style = MaterialTheme.typography.bodyMedium) },
                    onClick = {
                        addEntry.attendingId = item.id
                        viewModel.updateSelectedAttending(item.name)
                        expanded = false
                        Toast.makeText(context, item.name, Toast.LENGTH_SHORT).show()
                    },
                )

            }
            if (filteredOptions.isNotEmpty()) {
                IconButton(onClick = { openDialog = true }, Modifier.width(175.dp)) {
                    Row {
                        Icon(
                            imageVector = Icons.Outlined.Add, contentDescription = stringResource(
                                id = R.string.add
                            )
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = stringResource(R.string.add_attending_confirmation_title),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            if (openDialog)
                AlertDialog(
                    shape = RoundedCornerShape(25.dp),
                    onDismissRequest = { openDialog = false },
                    title = { Text(stringResource(R.string.add_attending_confirmation_title)) },
                    text = {
                        OutlinedTextField(
                            value = attendingName,
                            onValueChange = { attendingName = it },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = true,
                            label = {
                                Text(
                                    text = stringResource(id = R.string.attending),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        )
                    },
                    confirmButton = {
                        Button(
                            shape = RoundedCornerShape(25.dp),
                            onClick = {
                                try {
                                    coroutineScope.launch {
                                        viewModel.addAttending(attendingName)
                                    }
                                    openDialog = false
                                } catch (e: Exception) {
                                    Toast.makeText(
                                        context,
                                        "Something went wrong.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            },
                        ) {
                            Text(
                                stringResource(R.string.save),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    },
                    dismissButton = {
                        Button(
                            shape = RoundedCornerShape(25.dp),
                            onClick = {
                                openDialog = false
                            }) {
                            Text(
                                stringResource(R.string.cancel),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                )

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dropdown(
    items: List<Type>,
    label: String,
    addEntry: AddEntry,
    viewModel: AddEntryViewModel
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    var openDialog by rememberSaveable { mutableStateOf(false) }
    var procedureType by remember {
        mutableStateOf("")
    }

    val coroutineScope = rememberCoroutineScope()

    val editType = items.find { it.id == addEntry.typeId }
    if (editType?.type?.isNotEmpty() == true) {
        LaunchedEffect(viewModel.selectedProcedure) {
            viewModel.updateSelectedProcedure(editType.type)
        }
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        TextField(
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            value = viewModel.selectedProcedure.value,
            label = { Text(text = label, style = MaterialTheme.typography.bodyMedium) },
            onValueChange = { viewModel.updateSelectedProcedure(it) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
        )

        val filteredOptions =
            items.filter { it.type.contains(viewModel.selectedProcedure.value, ignoreCase = true) }

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { },
        ) {
            if (filteredOptions.isEmpty()) {
                IconButton(onClick = { openDialog = true }, Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Add, contentDescription = stringResource(
                                id = R.string.add
                            )
                        )
                        Text(
                            text = stringResource(R.string.add_new_type_confirmation_title),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            filteredOptions.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item.type, style = MaterialTheme.typography.bodyMedium) },
                    onClick = {
                        addEntry.typeId = item.id
                        viewModel.updateSelectedProcedure(item.type)
                        expanded = false
                        Toast.makeText(context, item.type, Toast.LENGTH_SHORT).show()
                    },
                )
            }
            if (filteredOptions.isNotEmpty()) {
                IconButton(onClick = { openDialog = true }, Modifier.width(175.dp)) {
                    Row {
                        Icon(
                            imageVector = Icons.Outlined.Add,
                            contentDescription = stringResource(
                                id = R.string.add
                            )
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = stringResource(R.string.add_new_type_confirmation_title),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            if (openDialog)
                AlertDialog(
                    shape = RoundedCornerShape(25.dp),
                    onDismissRequest = { openDialog = false },
                    title = { Text(stringResource(R.string.add_new_type_confirmation_title)) },
                    text = {
                        OutlinedTextField(
                            value = procedureType,
                            onValueChange = { procedureType = it },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = true,
                            label = {
                                Text(
                                    text = stringResource(id = R.string.procedure_type),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        )
                    },
                    confirmButton = {
                        Button(
                            shape = RoundedCornerShape(25.dp),
                            onClick = {
                                coroutineScope.launch {
                                    viewModel.addType(procedureType)
                                }
                                openDialog = false
                            },
                        ) {
                            Text(
                                stringResource(R.string.save),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    },
                    dismissButton = {
                        Button(
                            shape = RoundedCornerShape(25.dp),
                            onClick = {
                                openDialog = false
                            }) {
                            Text(
                                stringResource(R.string.cancel),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                )
        }
    }
}

private fun showDatePicker(
    date: Calendar,
    context: Context,
    onDateSelected: (Long) -> Unit
) {

    val tempDate = Calendar.getInstance()
    val datePicker = android.app.DatePickerDialog(
        context,
        { _, year, month, day ->
            tempDate[Calendar.YEAR] = year
            tempDate[Calendar.MONTH] = month
            tempDate[Calendar.DAY_OF_MONTH] = day
        },
        date[Calendar.YEAR],
        date[Calendar.MONTH],
        date[Calendar.DAY_OF_MONTH]
    )
    datePicker.show()
}

@Composable
fun Long.formatDate(): String {
    val sdf = SimpleDateFormat("EEE, MMM dd, yyyy", Locale.getDefault())
    return sdf.format(this)
}
