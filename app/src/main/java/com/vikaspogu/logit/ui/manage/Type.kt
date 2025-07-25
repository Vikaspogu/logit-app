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
import com.vikaspogu.logit.data.model.Type
import com.vikaspogu.logit.ui.NavigationDestinations
import com.vikaspogu.logit.ui.theme.SmallHeadingStyle
import com.vikaspogu.logit.ui.theme.TitleBarStyle
import com.vikaspogu.logit.ui.theme.TitleStyle
import com.vikaspogu.logit.ui.util.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageType(
    navController: NavHostController,
    modifier: Modifier,
    viewModel: ManageTypeViewModel = hiltViewModel(),
) {
    val typeUiState by viewModel.typeUiState.collectAsStateWithLifecycle()
    var openDialog by remember {
        mutableStateOf(false)
    }
    var procedureType by remember {
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
                    procedureType = ""
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
            openDialog -> {
                DialogManageTypes(
                    onDismissRequest = { openDialog = false },
                    onConfirmation = { openDialog = false },
                    type = Type(0,""),
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
fun DialogManageTypes(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    type: Type,
    actionType: String,
    viewModel: ManageTypeViewModel
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        var text by remember {
            mutableStateOf(type.type)
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
                    text = stringResource(R.string.manage_types),
                    style = TitleBarStyle
                )
                Spacer(modifier = Modifier.height(25.dp))
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = true,
                    label = {
                        Text(
                            text = stringResource(id = R.string.type),
                            style = SmallHeadingStyle
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
                        onClick = { onDismissRequest() },
                    ) {
                        Text(
                            stringResource(R.string.cancel),
                            style = SmallHeadingStyle
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        shape = RoundedCornerShape(25.dp),
                        onClick = {
                            onConfirmation()
                            if (actionType == Constants.ADD) {
                                viewModel.addType(text)
                            } else {
                                viewModel.updateType(type.id, text)
                            }

                        },
                    ) {
                        Text(
                            if (actionType == Constants.ADD) {
                                stringResource(R.string.save)
                            } else {
                                stringResource(R.string.update)
                            },
                            style = SmallHeadingStyle
                        )
                    }
                }
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
            style = TitleStyle,
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
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = type.type,
                style = TitleBarStyle,
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
                    viewModel.deleteType(type.id)
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
            when {
                openDialog -> {
                    DialogManageTypes(
                        onDismissRequest = { openDialog = false },
                        onConfirmation = { openDialog = false },
                        type = Type(id = type.id, type = type.type),
                        actionType = Constants.EDIT,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

