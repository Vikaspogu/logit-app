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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.vikaspogu.logit.R
import com.vikaspogu.logit.data.model.Attending
import com.vikaspogu.logit.data.model.RegionalType
import com.vikaspogu.logit.data.model.Type
import com.vikaspogu.logit.ui.NavigationDestinations
import com.vikaspogu.logit.ui.components.TopBar
import com.vikaspogu.logit.ui.theme.SmallHeadingStyle
import com.vikaspogu.logit.ui.util.Constants
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
    val typesUiState by viewModel.typeUiState.collectAsStateWithLifecycle()
    val attendingUiState by viewModel.attendingUiState.collectAsStateWithLifecycle()
    val regionalUiState by viewModel.regionalTypeUiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    if (addEntry.gender.isEmpty()) {
        addEntry.gender = "Male"
    }
    val radioOptions = listOf("Male", "Female")
    val isButtonEnabled = if (viewModel.residentView.value) {
        addEntry.age.isNotEmpty() && addEntry.quantity.isNotEmpty() && addEntry.gender.isNotEmpty() && viewModel.selectedAttending.value.isNotEmpty() && viewModel.selectedProcedure.value.isNotEmpty()
    } else {
        addEntry.asa.isNotEmpty() && addEntry.gender.isNotEmpty() && viewModel.selectedProcedure.value.isNotEmpty()
    }
    if (addEntry.clinical == "Yes") {
        LaunchedEffect(viewModel.selectedClinical) {
            viewModel.updateSelectedClinical(true)
        }
    }
    if (addEntry.cvc == "Yes") {
        LaunchedEffect(viewModel.selectedCVC) {
            viewModel.updateSelectedCVC(true)
        }
    }
    if (addEntry.regionalId != null) {
        LaunchedEffect(viewModel.selectedRegional) {
            viewModel.updateSelectedRegional(true)
        }
    }
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier.padding(10.dp)
    ) {
        if (viewModel.residentView.value) {
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
                        text = stringResource(R.string.age), style = SmallHeadingStyle
                    )
                },
            )
            OutlinedTextField(
                value = addEntry.quantity,
                onValueChange = { onValueChange(addEntry.copy(quantity = it)) },
                modifier = Modifier.fillMaxWidth(),
                enabled = true,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = {
                    Text(
                        text = stringResource(R.string.quantity), style = SmallHeadingStyle
                    )
                })
        } else {
            OutlinedTextField(
                value = addEntry.asa,
                onValueChange = { onValueChange(addEntry.copy(asa = it)) },
                modifier = Modifier.fillMaxWidth(),
                enabled = true,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = {
                    Text(
                        text = stringResource(R.string.asa), style = SmallHeadingStyle
                    )
                },
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = stringResource(R.string.aline),
                    style = SmallHeadingStyle,
                    modifier = Modifier.padding(start = 10.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                Switch(
                    checked = viewModel.selectedClinical.value, onCheckedChange = {
                    if (it) {
                        addEntry.clinical = "Yes"
                    } else {
                        addEntry.clinical = "No"
                    }
                    viewModel.updateSelectedClinical(it)
                }, thumbContent = if (viewModel.selectedClinical.value) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            modifier = Modifier.size(SwitchDefaults.IconSize),
                        )
                    }
                } else {
                    null
                })
            }
            Spacer(Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
            ) {
                Text(
                    text = stringResource(R.string.cvc),
                    style = SmallHeadingStyle,
                    modifier = Modifier.padding(start = 10.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                Switch(
                    checked = viewModel.selectedCVC.value, onCheckedChange = {
                    if (it) {
                        addEntry.cvc = "Yes"
                    } else {
                        addEntry.cvc = "No"
                    }
                    viewModel.updateSelectedCVC(it)
                }, thumbContent = if (viewModel.selectedCVC.value) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            modifier = Modifier.size(SwitchDefaults.IconSize),
                        )
                    }
                } else {
                    null
                })
            }
        }
        Spacer(Modifier.height(10.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {
            Text(
                text = stringResource(R.string.regional),
                style = SmallHeadingStyle,
                modifier = Modifier.padding(start = 10.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                checked = viewModel.selectedRegional.value, onCheckedChange = {
                    viewModel.updateSelectedRegional(it)
                }, thumbContent = if (viewModel.selectedRegional.value) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            modifier = Modifier.size(SwitchDefaults.IconSize),
                        )
                    }
                } else {
                    null
                })
        }
        if (viewModel.selectedRegional.value) {
            Spacer(Modifier.height(10.dp))
            DropdownRegional(
                items = regionalUiState.types,
                label = stringResource(R.string.regional_type),
                addEntry,
                viewModel
            )
        }
        if (viewModel.residentView.value) {
            Spacer(Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = stringResource(R.string.gender),
                    style = SmallHeadingStyle,
                    modifier = Modifier.padding(start = 10.dp)
                )
                radioOptions.forEach { text ->
                    Row(
                        Modifier
                            .height(56.dp)
                            .selectable(
                                selected = (text == viewModel.selectedGender.value), onClick = {
                                    addEntry.gender = text
                                    viewModel.updateSelectedGender(text)
                                }, role = Role.RadioButton
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (text == viewModel.selectedGender.value), onClick = null
                        )
                        Text(
                            text = text,
                            style = SmallHeadingStyle,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = viewModel.selectedDate.value.formatDate(),
                onValueChange = { },
                modifier = Modifier.fillMaxWidth(),
                enabled = true,
                label = {
                    Text(
                        text = stringResource(R.string.date), style = SmallHeadingStyle
                    )
                },
                trailingIcon = {
                    IconButton(onClick = {
                        showDatePicker(
                            Calendar.getInstance()
                                .apply { timeInMillis = viewModel.selectedDate.value },
                            context,
                            onDateSelected = {
                                viewModel.updateSelectedDate(it)
                                addEntry.entryDate = it
                            })
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.DateRange,
                            contentDescription = stringResource(R.string.date)
                        )
                    }
                })
        }
        Spacer(Modifier.height(10.dp))
        Dropdown(
            items = typesUiState.types,
            label = stringResource(R.string.procedure_type),
            addEntry,
            viewModel
        )
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = addEntry.notes,
            onValueChange = { onValueChange(addEntry.copy(notes = it)) },
            modifier = Modifier.fillMaxWidth(),
            enabled = true,
            label = {
                Text(
                    text = stringResource(R.string.notes), style = SmallHeadingStyle
                )
            })
        Spacer(Modifier.height(10.dp))
        if (viewModel.action == Constants.ADD) {
            Button(
                enabled = isButtonEnabled, onClick = {
                    viewModel.saveEntry()
                    navController.navigate(NavigationDestinations.Summary.name)
                }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text(text = stringResource(id = R.string.save))
            }
        } else if (viewModel.action == Constants.EDIT) {
            Button(
                enabled = isButtonEnabled, onClick = {
                    viewModel.updateEntry()
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
fun DropdownRegional(
    items: List<RegionalType>, label: String, addEntry: AddEntry, viewModel: AddEntryViewModel
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    var openDialog by rememberSaveable { mutableStateOf(false) }
    var regionalType by remember {
        mutableStateOf("")
    }
    val editType = items.find { it.id == addEntry.regionalId }
    if (editType?.name?.isNotEmpty() == true) {
        LaunchedEffect(viewModel.selectedRegionalType) {
            viewModel.updateSelectedRegionalType(editType.name)
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
            value = viewModel.selectedRegionalType.value,
            label = { Text(text = label, style = SmallHeadingStyle) },
            onValueChange = { viewModel.updateSelectedRegionalType(it) },
            trailingIcon = {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (viewModel.selectedRegionalType.value.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateSelectedRegionalType("") }) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = stringResource(
                                    id = R.string.cancel
                                )
                            )
                        }
                    }
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            },
        )

        val filteredOptions = items.filter {
            it.name.contains(
                viewModel.selectedRegionalType.value, ignoreCase = true
            )
        }
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { },
        ) {
            filteredOptions.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item.name, style = SmallHeadingStyle) },
                    onClick = {
                        addEntry.regionalId = item.id
                        viewModel.updateSelectedRegionalType(item.name)
                        expanded = false
                        Toast.makeText(context, item.name, Toast.LENGTH_SHORT).show()
                    },
                )

            }
            if (viewModel.selectedRegionalType.value.isEmpty()) {
                IconButton(onClick = { openDialog = true }, Modifier.width(175.dp)) {
                    Row {
                        Icon(
                            imageVector = Icons.Outlined.Add, contentDescription = stringResource(
                                id = R.string.add
                            )
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = stringResource(R.string.add_new_type_confirmation_title),
                            style = SmallHeadingStyle
                        )
                    }
                }
            }
            if (openDialog) AlertDialog(
                shape = RoundedCornerShape(25.dp),
                onDismissRequest = { openDialog = false },
                title = { Text(stringResource(R.string.add_new_type_confirmation_title)) },
                text = {
                    OutlinedTextField(
                        value = regionalType,
                        onValueChange = { regionalType = it },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = true,
                        label = {
                            Text(
                                text = stringResource(id = R.string.regional_type),
                                style = SmallHeadingStyle
                            )
                        })
                },
                confirmButton = {
                    Button(
                        shape = RoundedCornerShape(25.dp),
                        onClick = {
                            try {
                                viewModel.addRegionalType(regionalType)
                                openDialog = false
                            } catch (e: Exception) {
                                Toast.makeText(
                                    context, "Something went wrong.", Toast.LENGTH_LONG
                                ).show()
                            }
                        },
                    ) {
                        Text(
                            stringResource(R.string.save), style = SmallHeadingStyle
                        )
                    }
                },
                dismissButton = {
                    Button(
                        shape = RoundedCornerShape(25.dp), onClick = {
                            openDialog = false
                        }) {
                        Text(
                            stringResource(R.string.cancel), style = SmallHeadingStyle
                        )
                    }
                })

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownAttending(
    items: List<Attending>, label: String, addEntry: AddEntry, viewModel: AddEntryViewModel
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    var openDialog by rememberSaveable { mutableStateOf(false) }
    var attendingName by remember {
        mutableStateOf("")
    }
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
            label = { Text(text = label, style = SmallHeadingStyle) },
            onValueChange = { viewModel.updateSelectedAttending(it) },
            trailingIcon = {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (viewModel.selectedAttending.value.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateSelectedAttending("") }) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = stringResource(
                                    id = R.string.cancel
                                )
                            )
                        }
                    }
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            },
        )

        val filteredOptions =
            items.filter { it.name.contains(viewModel.selectedAttending.value, ignoreCase = true) }
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { },
        ) {
            filteredOptions.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item.name, style = SmallHeadingStyle) },
                    onClick = {
                        addEntry.attendingId = item.id
                        viewModel.updateSelectedAttending(item.name)
                        expanded = false
                        Toast.makeText(context, item.name, Toast.LENGTH_SHORT).show()
                    },
                )

            }
            if (viewModel.selectedAttending.value.isEmpty()) {
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
                            style = SmallHeadingStyle
                        )
                    }
                }
            }
            if (openDialog) AlertDialog(
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
                                style = SmallHeadingStyle
                            )
                        })
                },
                confirmButton = {
                    Button(
                        shape = RoundedCornerShape(25.dp),
                        onClick = {
                            try {
                                viewModel.addAttending(attendingName)
                                openDialog = false
                            } catch (e: Exception) {
                                Toast.makeText(
                                    context, "Something went wrong.", Toast.LENGTH_LONG
                                ).show()
                            }
                        },
                    ) {
                        Text(
                            stringResource(R.string.save), style = SmallHeadingStyle
                        )
                    }
                },
                dismissButton = {
                    Button(
                        shape = RoundedCornerShape(25.dp), onClick = {
                            openDialog = false
                        }) {
                        Text(
                            stringResource(R.string.cancel), style = SmallHeadingStyle
                        )
                    }
                })

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dropdown(
    items: List<Type>, label: String, addEntry: AddEntry, viewModel: AddEntryViewModel
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    var openDialog by rememberSaveable { mutableStateOf(false) }
    var procedureType by remember {
        mutableStateOf("")
    }
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
            label = { Text(text = label, style = SmallHeadingStyle) },
            onValueChange = { viewModel.updateSelectedProcedure(it) },
            trailingIcon = {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (viewModel.selectedProcedure.value.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateSelectedProcedure("") }) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = stringResource(
                                    id = R.string.cancel
                                )
                            )
                        }
                    }
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            },
        )

        val filteredOptions =
            items.filter { it.type.contains(viewModel.selectedProcedure.value, ignoreCase = true) }

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { },
        ) {
            filteredOptions.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item.type, style = SmallHeadingStyle) },
                    onClick = {
                        addEntry.typeId = item.id
                        viewModel.updateSelectedProcedure(item.type)
                        expanded = false
                        Toast.makeText(context, item.type, Toast.LENGTH_SHORT).show()
                    },
                )
            }
            if (viewModel.selectedProcedure.value.isEmpty()) {
                IconButton(onClick = { openDialog = true }, Modifier.width(175.dp)) {
                    Row {
                        Icon(
                            imageVector = Icons.Outlined.Add, contentDescription = stringResource(
                                id = R.string.add
                            )
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = stringResource(R.string.add_new_type_confirmation_title),
                            style = SmallHeadingStyle
                        )
                    }
                }
            }
            if (openDialog) AlertDialog(
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
                                style = SmallHeadingStyle
                            )
                        })
                },
                confirmButton = {
                    Button(
                        shape = RoundedCornerShape(25.dp),
                        onClick = {
                            viewModel.addType(procedureType)
                            openDialog = false
                        },
                    ) {
                        Text(
                            stringResource(R.string.save), style = SmallHeadingStyle
                        )
                    }
                },
                dismissButton = {
                    Button(
                        shape = RoundedCornerShape(25.dp), onClick = {
                            openDialog = false
                        }) {
                        Text(
                            stringResource(R.string.cancel), style = SmallHeadingStyle
                        )
                    }
                })
        }
    }
}

private fun showDatePicker(
    date: Calendar, context: Context, onDateSelected: (Long) -> Unit
) {

    val tempDate = Calendar.getInstance()
    val datePicker = android.app.DatePickerDialog(
        context, { _, year, month, day ->
            tempDate[Calendar.YEAR] = year
            tempDate[Calendar.MONTH] = month
            tempDate[Calendar.DAY_OF_MONTH] = day
            onDateSelected(tempDate.timeInMillis)
        }, date[Calendar.YEAR], date[Calendar.MONTH], date[Calendar.DAY_OF_MONTH]
    )
    datePicker.show()
}

@Composable
fun Long.formatDate(): String {
    val sdf = SimpleDateFormat("EEE, MMM dd, yyyy", Locale.getDefault())
    return sdf.format(this)
}