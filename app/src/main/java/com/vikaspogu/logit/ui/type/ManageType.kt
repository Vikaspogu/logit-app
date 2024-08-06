package com.vikaspogu.logit.ui.type

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.vikaspogu.logit.R
import com.vikaspogu.logit.data.model.Type
import com.vikaspogu.logit.ui.NavigationDestinations
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageType(
    navController: NavHostController,
    modifier: Modifier,
    viewModel: ManageTypeViewModel = hiltViewModel(),
) {
    val typeUiState by viewModel.typeUiState.collectAsState()
    var openDialog by remember {
        mutableStateOf(false)
    }
    var procedureType by remember {
        mutableStateOf("")
    }
    val coroutineScope = rememberCoroutineScope()
    // Visibility for FAB, could be saved in viewModel
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
                    procedureType = ""
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.add)
                )
            }
        }
        if (openDialog)
            AlertDialog(
                shape = RoundedCornerShape(25.dp),
                onDismissRequest = { openDialog = false },
                title = {
                    Text(
                        stringResource(R.string.add_new_type_confirmation_title),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.inverseSurface
                    )
                },
                text = {
                    OutlinedTextField(
                        value = procedureType,
                        onValueChange = { procedureType = it },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = true,
                        label = {
                            Text(
                                text = stringResource(id = R.string.procedure_type),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.inverseSurface
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
    }) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            modifier = modifier.nestedScroll(nestedScrollConnection)
        ) {
            item {
                TypeDetails(
                    typeUiState.typeList,
                    modifier,
                    viewModel
                )
            }
        }

    }
}

@Composable
fun TypeDetails(typeList: List<Type>, modifier: Modifier, viewModel: ManageTypeViewModel) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.Center) {
        Text(
            modifier = Modifier
                .padding(16.dp),
            text = stringResource(id = R.string.manage_types),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.inverseSurface
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
    val context = LocalContext.current
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.primaryContainer,
        shadowElevation = 5.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 5.dp, bottom = 5.dp, start = 10.dp, end = 10.dp)
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (!editable) {
                Text(
                    text = type.type,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = modifier.weight(1f),
                    color = MaterialTheme.colorScheme.inverseSurface
                )
                IconButton(onClick = { editable = true }) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = stringResource(R.string.edit)
                    )
                }
                IconButton(onClick = {
                    coroutineScope.launch {
                        try {
                            viewModel.deleteType(type.id)
                        } catch (e: Exception) {
                            Toast.makeText(
                                context,
                                "Procedure Type cannot be deleted.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = stringResource(R.string.delete)
                    )
                }
            }
            AnimatedVisibility(editable) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = true,
                    label = {
                        Text(
                            text = stringResource(id = R.string.manage_types),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
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

