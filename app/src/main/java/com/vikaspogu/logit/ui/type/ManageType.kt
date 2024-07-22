package com.vikaspogu.logit.ui.type

import android.icu.util.Calendar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.vikaspogu.logit.R
import com.vikaspogu.logit.data.model.Type
import com.vikaspogu.logit.ui.NavigationDestinations
import com.vikaspogu.logit.ui.components.BottomBar
import com.vikaspogu.logit.ui.components.TopBar
import com.vikaspogu.logit.ui.util.Constants
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageType(
    navController: NavHostController,
    viewModel: ManageTypeViewModel,
    modifier: Modifier
) {
    val typeUiState by viewModel.typeUiState.collectAsState()
    var openDialog by remember {
        mutableStateOf(false)
    }
    var procedureType by remember {
        mutableStateOf("")
    }
    val coroutineScope = rememberCoroutineScope()
    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            title = {
                TopBar(true, navController, NavigationDestinations.Settings)
            },
        )
    }, floatingActionButton = {
        FloatingActionButton(onClick = { openDialog = true}) {
            Icon(
                imageVector = Icons.Default.Add, contentDescription = "Add"
            )
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
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    ),
                    label = { Text(text = "Procedure Type") }
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
                        style = MaterialTheme.typography.labelSmall
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
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        )
    }, bottomBar = {
        BottomBar(navController = navController)
    }) { innerPadding ->
        TypeList(
            typeList = typeUiState.typeList, contentPadding = innerPadding,
            modifier = modifier.fillMaxSize(),
            viewModel
        )

    }
}

@Composable
fun TypeList(
    typeList: List<Type>,
    contentPadding: PaddingValues,
    modifier: Modifier,
    viewModel: ManageTypeViewModel
) {
    LazyColumn(
        modifier = modifier, contentPadding = contentPadding
    ) {
        item { TypeDetails(typeList, modifier, viewModel) }
    }
}

@Composable
fun TypeDetails(typeList: List<Type>, modifier: Modifier, viewModel: ManageTypeViewModel) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.Center) {
        Text(
            modifier = Modifier
                .padding(16.dp),
            text = stringResource(id = R.string.manage_types),
            style = MaterialTheme.typography.displayMedium
        )
        for (type in typeList) {
            TypeCard(
                type, modifier, viewModel
            )
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun TypeCard(type: Type, modifier: Modifier, viewModel: ManageTypeViewModel) {
    var editable by remember {
        mutableStateOf(false)
    }
    var text by remember {
        mutableStateOf(type.type)
    }
    val coroutineScope = rememberCoroutineScope()
    Card(
        modifier = modifier.padding(5.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        shape = MaterialTheme.shapes.large,
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (!editable) {
                Text(
                    text = type.type,
                    style = MaterialTheme.typography.displayMedium,
                )
                Spacer(modifier = modifier.weight(1f))
                IconButton(onClick = { editable = true }) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = stringResource(R.string.edit)
                    )
                }
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = stringResource(R.string.delete)
                    )
                }
            }
            if (editable) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    ),
                    label = { Text(text = stringResource(id = R.string.manage_types)) },
                    trailingIcon = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            IconButton(onClick = {
                                coroutineScope.launch {
                                    viewModel.updateType(type.id, text)
                                }
                                editable = !editable
                            }) {
                                Icon(
                                    imageVector = Icons.Outlined.Check,
                                    contentDescription = stringResource(id = R.string.save)
                                )
                            }
                            IconButton(onClick = {
                                editable = !editable
                            }) {
                                Icon(
                                    imageVector = Icons.Outlined.Close,
                                    contentDescription = stringResource(id = R.string.cancel)
                                )
                            }
                        }
                    }
                )
            }

        }

    }
}