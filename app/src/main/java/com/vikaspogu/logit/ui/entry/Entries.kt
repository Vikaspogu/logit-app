package com.vikaspogu.logit.ui.entry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.vikaspogu.logit.R
import com.vikaspogu.logit.data.model.EntryType
import com.vikaspogu.logit.ui.NavigationDestinations
import com.vikaspogu.logit.ui.components.BottomBar
import com.vikaspogu.logit.ui.components.TopBar
import com.vikaspogu.logit.ui.util.Constants
import kotlinx.coroutines.launch

@Composable
fun EntriesScreen(
    navController: NavHostController, modifier: Modifier, viewModel: EntriesViewModel
) {
    val entriesUiState by viewModel.entriesUiState.collectAsState()
    Scaffold(topBar = {
        TopBar(false, navController, NavigationDestinations.Entries)
    }, bottomBar = {
        BottomBar(navController = navController)
    }) { innerPadding ->
        Entries(
            entries = entriesUiState.entries,
            contentPadding = innerPadding,
            modifier = modifier.fillMaxSize(),
            navController,
            viewModel
        )
    }
}

@Composable
private fun Entries(
    entries: List<EntryType>,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: EntriesViewModel
) {
    LazyColumn(
        modifier = modifier, contentPadding = contentPadding
    ) {
        items(entries) { item ->
            EntriesCard(
                entry = item,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small)),
                navController,
                viewModel
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EntriesCard(
    entry: EntryType,
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: EntriesViewModel
) {
    var openDialog by rememberSaveable { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    Card(modifier = modifier
        .padding(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        shape = MaterialTheme.shapes.large,
        onClick = {
            navController.navigate(
                "addEdit/{action}/{entryId}".replace(
                    oldValue = "{entryId}", newValue = entry.entryId.toString()
                ).replace(oldValue = "{action}", newValue = Constants.EDIT)
            )
        }) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small)),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column() {
                        Text(
                            text = entry.type, style = MaterialTheme.typography.displayMedium
                        )
                        Row {
                            Text(
                                text = "Date: ",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                text = entry.entryDate.formatDate(),
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                        Row {
                            Text(
                                text = "Attending: ",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                text = entry.attendingName,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                        Row {
                            Text(
                                text = "Patients age: ",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                text = entry.age.toString(),
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                        Row {
                            Text(
                                text = "Quantity: ",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                text = entry.quantity.toString(),
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                        Row {
                            Text(
                                text = "Notes: ",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                text = entry.notes, style = MaterialTheme.typography.labelMedium
                            )
                        }

                    }
                    Spacer(modifier = modifier.weight(1f))
                    IconButton(onClick = { openDialog = true }) {
                        Icon(
                            imageVector = Icons.Outlined.Delete, contentDescription = "Delete"
                        )
                    }
                    if (openDialog)
                        AlertDialog(
                            shape = RoundedCornerShape(25.dp),
                            onDismissRequest = { openDialog = false },
                            title = { Text(stringResource(R.string.delete_confirmation_title)) },
                            text = {
                                Text(
                                    stringResource(
                                        R.string.delete_confirmation_message
                                    ),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            confirmButton = {
                                Button(
                                    shape = RoundedCornerShape(25.dp),
                                    onClick = {
                                        coroutineScope.launch {
                                            viewModel.deleteEntry(entry.entryId)
                                        }
                                    },
                                ) {
                                    Text(
                                        stringResource(R.string.delete),
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
                }
            }
        }

    }
}
