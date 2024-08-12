package com.vikaspogu.logit.ui.manage

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.vikaspogu.logit.R
import com.vikaspogu.logit.data.model.Attending
import com.vikaspogu.logit.ui.NavigationDestinations
import com.vikaspogu.logit.ui.util.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagePersons(
    navController: NavHostController,
    modifier: Modifier,
    viewModel: ManagePersonsViewModel = hiltViewModel(),
) {

    val personUiState by viewModel.personUiState.collectAsStateWithLifecycle()
    var openDialog by remember {
        mutableStateOf(false)
    }
    var person by remember {
        mutableStateOf("")
    }
    var isVisible by remember { mutableStateOf(true) }

    // Nested scroll for control FAB
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // Hide FAB
                if (available.y < -1) {
                    isVisible = false
                }

                // Show FAB
                if (available.y > 1) {
                    isVisible = true
                }

                return Offset.Zero
            }
        }
    }

    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            title = {},
            navigationIcon = {
                IconButton(onClick = {
                    navController.navigate(NavigationDestinations.Settings.name)
                }) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(id = R.string.back)
                    )
                }
            }
        )
    }, floatingActionButton = {
        AnimatedVisibility(
            visible = isVisible, enter = slideInVertically(initialOffsetY = { it * 2 }),
            exit = slideOutVertically(targetOffsetY = { it * 2 }),
        ) {
            FloatingActionButton(
                onClick = {
                    openDialog = true
                    person = ""
                },
                containerColor = MaterialTheme.colorScheme.inverseSurface,
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.add)
                )
            }
        }
        when {
            (openDialog) -> {
                DialogManagePersons(
                    onDismissRequest = { openDialog = false },
                    onConfirmation = { openDialog = false },
                    attending = Attending(0, ""),
                    actionType = Constants.ADD,
                    viewModel = viewModel
                )
            }
        }


    }) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            modifier = modifier.nestedScroll(nestedScrollConnection)
        ) {
            item {
                PersonDetails(
                    personUiState.personList,
                    modifier,
                    viewModel
                )
            }
        }

    }
}

@Composable
fun DialogManagePersons(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    attending: Attending,
    actionType: String,
    viewModel: ManagePersonsViewModel
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        var text by remember {
            mutableStateOf(attending.name)
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.manage_names),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(25.dp))
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = true,
                    label = {
                        Text(
                            text = stringResource(id = R.string.manage_names),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                )
                Spacer(modifier = Modifier.height(25.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Button(
                        shape = RoundedCornerShape(25.dp),
                        onClick = {
                            onConfirmation()
                            if (actionType == Constants.ADD) {
                                viewModel.addPersons(text)
                            } else {
                                viewModel.updatePersons(attending.id, text)
                            }

                        },
                    ) {
                        Text(
                            if (actionType == Constants.ADD) {
                                stringResource(R.string.save)
                            } else {
                                stringResource(R.string.update)
                            },
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        shape = RoundedCornerShape(25.dp),
                        onClick = { onDismissRequest() },
                    ) {
                        Text(
                            stringResource(R.string.cancel),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PersonDetails(
    personList: List<Attending>,
    modifier: Modifier,
    viewModel: ManagePersonsViewModel
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.Center) {
        Text(
            modifier = Modifier
                .padding(16.dp),
            text = stringResource(id = R.string.manage_names),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.inverseSurface
        )
        for (person in personList) {
            PersonCard(
                person, modifier, viewModel
            )
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun PersonCard(person: Attending, modifier: Modifier, viewModel: ManagePersonsViewModel) {
    var openDialog by remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.primaryContainer,
        shadowElevation = 5.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 5.dp, bottom = 5.dp, start = 10.dp, end = 10.dp)
    ) {
        when {
            (openDialog) -> {
                DialogManagePersons(
                    onDismissRequest = { openDialog = false },
                    onConfirmation = { openDialog = false },
                    attending = Attending(person.id, person.name),
                    actionType = Constants.EDIT,
                    viewModel = viewModel
                )
            }
        }
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = person.name,
                style = MaterialTheme.typography.bodyLarge,
                modifier = modifier.weight(1f),
                color = MaterialTheme.colorScheme.inverseSurface
            )
            IconButton(onClick = { openDialog = true }) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = stringResource(R.string.edit)
                )
            }
            IconButton(onClick = {
                try {
                    viewModel.deletePersons(person.id)
                } catch (e: Exception) {
                    Toast.makeText(
                        context,
                        "Procedure Type cannot be deleted.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = stringResource(R.string.delete)
                )
            }
        }
    }
}

