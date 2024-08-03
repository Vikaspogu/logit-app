package com.vikaspogu.logit.ui.entry

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
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
//            ItemEntryCard(entry = item,modifier)
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

@SuppressLint("UnusedContentLambdaTargetStateParameter")
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
    var showMoreButton by remember {
        mutableStateOf(false)
    }
    var hideOverFlowingText by remember {
        mutableStateOf(false)
    }

    Surface(shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.primaryContainer,
        shadowElevation = 5.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 5.dp, bottom = 5.dp, start = 10.dp, end = 10.dp)
            .clickable {
                navController.navigate(
                    "addEdit/{action}/{entryId}"
                        .replace(
                            oldValue = "{entryId}", newValue = entry.entryId.toString()
                        )
                        .replace(oldValue = "{action}", newValue = Constants.EDIT)
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
                    modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 0.dp),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.inverseSurface
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    Icon(
                        imageVector = Icons.Filled.Face,
                        contentDescription = stringResource(id = R.string.attending)
                    )
                    Text(
                        text = entry.attendingName,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp, 12.dp, 12.dp, 0.dp),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Row(verticalAlignment = Alignment.Bottom) {
                    Icon(
                        imageVector = Icons.Outlined.DateRange, contentDescription = stringResource(
                            id = R.string.date
                        )
                    )
                    Text(
                        text = buildString {
                            append(entry.entryDate.formatDate())
                        },
                        modifier = Modifier.padding(5.dp, 12.dp, 12.dp, 5.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.inverseSurface
                    )
                }
                Text(
                    text = buildString {
                        append(entry.age)
                        append(" yrs | ")
                        append(entry.gender)
                        append(" | ")
                        append(entry.quantity)
                        append(" quantity")
                    },
                    modifier = Modifier.padding(5.dp, 5.dp, 12.dp, 10.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.inverseSurface
                )
                if (!hideOverFlowingText) {
                    Text(
                        text = entry.notes,
                        maxLines = 2,
                        onTextLayout = {
                            if (it.hasVisualOverflow) {
                                showMoreButton = true
                            }
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(5.dp, 5.dp, 0.dp, 0.dp),
                        color = MaterialTheme.colorScheme.inverseSurface
                    )
                }

                if (showMoreButton) {
                    AnimatedVisibility(expanded) {
                        Text(
                            text = entry.notes,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(5.dp, 5.dp, 0.dp, 0.dp),
                            color = MaterialTheme.colorScheme.inverseSurface
                        )
                    }
                    Box(
                        modifier = Modifier
                            .wrapContentWidth()
                            .padding(top = 10.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.inversePrimary)
                            .clickable(onClick = {
                                expanded = !expanded
                                hideOverFlowingText = !hideOverFlowingText
                            }),
                    ) {
                        Text(
                            text = if (!expanded) stringResource(id = R.string.show_notes) else stringResource(
                                id = R.string.hide_notes
                            ),
                            modifier = Modifier.padding(12.dp, 6.dp, 12.dp, 6.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.inverseSurface
                        )
                    }
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

            }
            if (openDialog) AlertDialog(shape = RoundedCornerShape(25.dp),
                onDismissRequest = { openDialog = false },
                title = { Text(stringResource(R.string.delete_confirmation_title)) },
                text = {
                    Text(
                        stringResource(
                            R.string.delete_confirmation_message
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.inverseSurface
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
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.inverseSurface
                        )
                    }
                },
                dismissButton = {
                    Button(shape = RoundedCornerShape(25.dp), onClick = {
                        openDialog = false
                    }) {
                        Text(
                            stringResource(R.string.cancel),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.inverseSurface
                        )
                    }
                })

        }

    }
}
