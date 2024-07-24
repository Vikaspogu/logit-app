package com.vikaspogu.logit.ui.entry

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
        if (entriesUiState.entries.isNotEmpty()) {
            Entries(
                entries = entriesUiState.entries,
                contentPadding = innerPadding,
                modifier = modifier.fillMaxSize(),
                navController,
                viewModel
            )
        } else {
            EmptyEntries(modifier = modifier)
        }

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

@Composable
fun EmptyEntries(modifier: Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "You don't have any entries", style = MaterialTheme.typography.headlineSmall)
    }
}

@Composable
private fun EntriesCard(
    entry: EntryType,
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: EntriesViewModel
) {
    var openDialog by rememberSaveable { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var expanded by remember {
        mutableStateOf(false)
    }
    Card(modifier = modifier,
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
            modifier = modifier.animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium
                )
            ), verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = modifier.weight(1f)
            ) {
                Text(
                    text = entry.type,
                    style = MaterialTheme.typography.headlineSmall,
                )
                Text(
                    text = stringResource(id = R.string.date),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = entry.entryDate.formatDate(), style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = stringResource(id = R.string.attending),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = entry.attendingName, style = MaterialTheme.typography.bodyMedium
                )
                if (expanded) {
                    Text(
                        text = stringResource(id = R.string.age),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = entry.age.toString(), style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = stringResource(id = R.string.quantity),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = entry.quantity.toString(),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = stringResource(id = R.string.notes),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = entry.notes, style = MaterialTheme.typography.bodyMedium
                    )
                }


            }
            Column(modifier = modifier) {
                IconButton(onClick = { openDialog = true }) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = stringResource(id = R.string.delete)
                    )
                }
                Spacer(modifier = modifier.weight(1f))
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp,
                        contentDescription = stringResource(id = R.string.expand_button_content_description),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            if (openDialog) AlertDialog(shape = RoundedCornerShape(25.dp),
                onDismissRequest = { openDialog = false },
                title = { Text(stringResource(R.string.delete_confirmation_title)) },
                text = {
                    Text(
                        stringResource(
                            R.string.delete_confirmation_message
                        ), style = MaterialTheme.typography.labelSmall
                    )
                },
                confirmButton = {
                    Button(
                        shape = RoundedCornerShape(25.dp),
                        onClick = {
                            coroutineScope.launch {
                                viewModel.deleteEntry(entry.entryId)
                            }
                            openDialog = false
                        },
                    ) {
                        Text(
                            stringResource(R.string.delete),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                },
                dismissButton = {
                    Button(shape = RoundedCornerShape(25.dp), onClick = {
                        openDialog = false
                    }) {
                        Text(
                            stringResource(R.string.cancel),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                })

        }


    }
}
