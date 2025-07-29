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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.vikaspogu.logit.R
import com.vikaspogu.logit.data.model.EntryType
import com.vikaspogu.logit.ui.NavigationDestinations
import com.vikaspogu.logit.ui.components.BottomBar
import com.vikaspogu.logit.ui.components.TopBar
import com.vikaspogu.logit.ui.home.ChipView
import com.vikaspogu.logit.ui.theme.HeadingStyle
import com.vikaspogu.logit.ui.theme.SmallHeadingStyle
import com.vikaspogu.logit.ui.theme.TitleBarStyle
import com.vikaspogu.logit.ui.util.Constants

@Composable
fun EntriesScreen(
    navController: NavHostController,
    modifier: Modifier,
    viewModel: EntriesViewModel = hiltViewModel()
) {
    val entriesUiState by viewModel.entriesUiState.collectAsStateWithLifecycle()
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
        Text(text = "You don't have any entries", style = HeadingStyle)
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
    var expanded by remember {
        mutableStateOf(false)
    }
    var showMoreButton by remember {
        mutableStateOf(false)
    }
    var hideOverFlowingText by remember {
        mutableStateOf(false)
    }

    Surface(
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.primaryContainer,
        shadowElevation = 5.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 5.dp, start = 10.dp, end = 10.dp)
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
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small)),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    entry.type?.let {
                        Text(
                            text = it,
                            modifier = Modifier.padding(end = 0.dp),
                            fontWeight = FontWeight.Bold,
                            style = HeadingStyle,
                            color = MaterialTheme.colorScheme.inverseSurface,
                        )
                    }
                    Text(
                        text = entry.entryDate.formatDate(),
                        modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 0.dp),
                        style = SmallHeadingStyle,
                    )
                }
                Row {
                    if (viewModel.residentView.value){
                        ChipView(entry.gender, colorResource(id = R.color.blue))
                        Spacer(modifier = Modifier.padding(5.dp))
                    }
                    if (entry.clinical == "Yes") {
                        ChipView(stringResource(R.string.aline), colorResource(id = R.color.teal_700))
                    }
                    Spacer(modifier = Modifier.padding(5.dp))
                    if (entry.cvc == "Yes") {
                        ChipView(stringResource(R.string.cvc), colorResource(id = R.color.red))
                    }
                }
                if (viewModel.residentView.value) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Face,
                            contentDescription = stringResource(id = R.string.attending)
                        )
                        entry.attendingName?.let {
                            Text(
                                text = it,
                                modifier = Modifier.padding(5.dp, 0.dp, 12.dp, 0.dp),
                                style = TitleBarStyle,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.inverseSurface,
                            )
                        }
                    }
                }

                Text(
                    text = buildString {
                        if (viewModel.residentView.value) {
                            append("Age: ")
                            append(entry.age)
                            appendLine()
                            append("Quantity: ")
                            append(entry.quantity)

                        } else {
                            append("ASA: ")
                            append(entry.asa)
                        }
                    },
                    modifier = Modifier.padding(5.dp, 0.dp, 12.dp, 0.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.inverseSurface,
                )
                if (entry.regionalType != null){
                    Text(
                        text = buildString {
                            append("Regional Type: ")
                            append(entry.regionalType)
                        },
                        modifier = Modifier.padding(5.dp, 0.dp, 12.dp, 0.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.inverseSurface,
                    )
                }
                if (!hideOverFlowingText) {
                    Text(
                        text = buildString {
                            append("Notes: ")
                            append(entry.notes)
                        },
                        maxLines = 2,
                        onTextLayout = {
                            if (it.hasVisualOverflow) {
                                showMoreButton = true
                            }
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(5.dp, 0.dp, 0.dp, 0.dp),
                        color = MaterialTheme.colorScheme.inverseSurface
                    )
                }

                if (showMoreButton) {
                    AnimatedVisibility(expanded) {
                        Text(
                            text = buildString {
                                append("Notes: ")
                                append(entry.notes)
                            },
                            style = SmallHeadingStyle,
                            modifier = Modifier.padding(5.dp, 0.dp, 0.dp, 0.dp),
                            color = MaterialTheme.colorScheme.inverseSurface
                        )
                    }
                    Box(
                        modifier = Modifier
                            .wrapContentWidth()
                            .padding(top = 5.dp)
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
                            style = SmallHeadingStyle,
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
            if (openDialog) AlertDialog(
                shape = RoundedCornerShape(25.dp),
                onDismissRequest = { openDialog = false },
                title = { Text(stringResource(R.string.delete_confirmation_title)) },
                text = {
                    Text(
                        stringResource(
                            R.string.delete_confirmation_message
                        ),
                        style = SmallHeadingStyle,
                        color = MaterialTheme.colorScheme.inverseSurface
                    )
                },
                confirmButton = {
                    Button(
                        shape = RoundedCornerShape(25.dp),
                        onClick = {
                            viewModel.deleteEntry(entry.entryId)
                            openDialog = false
                        },
                    ) {
                        Text(
                            stringResource(R.string.delete),
                            style = SmallHeadingStyle,
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
                            style = SmallHeadingStyle,
                            color = MaterialTheme.colorScheme.inverseSurface
                        )
                    }
                })

        }

    }
}
