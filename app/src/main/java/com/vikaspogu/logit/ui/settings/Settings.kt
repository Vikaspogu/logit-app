package com.vikaspogu.logit.ui.settings

import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.documentfile.provider.DocumentFile
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import com.vikaspogu.logit.BuildConfig
import com.vikaspogu.logit.R
import com.vikaspogu.logit.data.model.EntryType
import com.vikaspogu.logit.ui.NavigationDestinations
import com.vikaspogu.logit.ui.components.BottomBar
import com.vikaspogu.logit.ui.components.TopBar
import com.vikaspogu.logit.ui.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Locale


@Composable
fun Settings(
    navController: NavHostController,
    modifier: Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    Scaffold(topBar = {
        TopBar(
            showBackNavigation = false,
            navController = navController,
            navDest = NavigationDestinations.Summary
        )
    }, bottomBar = {
        BottomBar(navController = navController)
    }) { innerPadding ->
        SettingColumn(
            contentPadding = innerPadding,
            modifier = modifier.fillMaxSize(),
            navController,
            viewModel
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingColumn(
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: SettingsViewModel
) {
    val context = LocalContext.current
    val entriesList by viewModel.entriesUiState.collectAsState()
    val chooseDirectoryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        uri?.let {
            exportCSV(it, context, entriesList.entries)
        }
    }
    val selectedDays by viewModel.reminderDays.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var openDialog by remember {
        mutableStateOf(false)
    }

    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
    ) {
        item {
            Text(
                modifier = Modifier.padding(16.dp),
                text = stringResource(id = R.string.settings),
                style = MaterialTheme.typography.titleLarge
            )
            SettingsBasicLinkItem(title = R.string.manage_types,
                icon = R.drawable.ic_code,
                onClick = {
                    navController.navigate(NavigationDestinations.Types.name)
                })
            SettingsBasicLinkItem(title = R.string.export_csv,
                icon = R.drawable.ic_export,
                onClick = {
                    chooseDirectoryLauncher.launch(null)
                })
            SettingsBasicLinkItem(title = R.string.reminders,
                icon = R.drawable.ic_notification,
                onClick = {
                    openDialog = true
                })

            when {
                openDialog -> {
                    DialogWithReminders(
                        onDismissRequest = { openDialog = false },
                        days = selectedDays.reminderDaysSet,
                        onConfirmation = { openDialog = false },
                        viewModel = viewModel,
                        coroutineScope = coroutineScope
                    )
                }
            }
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp, top = 5.dp),
            ) {
                SegmentedButton(selected = !viewModel.selectedTheme.value, onClick = {
                    viewModel.updateSelectedTheme(!viewModel.selectedTheme.value)
                    coroutineScope.launch { viewModel.saveThemePreferences(viewModel.selectedTheme.value) }
                }, shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp), label = {
                    Text(
                        text = stringResource(id = R.string.light_theme),
                        style = MaterialTheme.typography.bodyLarge
                    )
                })
                SegmentedButton(selected = viewModel.selectedTheme.value, onClick = {
                    viewModel.updateSelectedTheme(!viewModel.selectedTheme.value)
                    coroutineScope.launch { viewModel.saveThemePreferences(viewModel.selectedTheme.value) }
                }, shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp), label = {
                    Text(
                        text = stringResource(id = R.string.dark_theme),
                        style = MaterialTheme.typography.bodyLarge
                    )
                })
            }
        }
        item {
            Text(
                modifier = Modifier.padding(16.dp),
                text = stringResource(id = R.string.about),
                style = MaterialTheme.typography.titleLarge
            )
            SettingsBasicLinkItem(
                title = R.string.project_on_github,
                icon = R.drawable.github_icon,
                link = Constants.PROJECT_GITHUB_LINK
            )
        }
        item {
            SettingsBasicLinkItem(
                title = R.string.app_version,
                icon = R.drawable.ic_code,
                subtitle = BuildConfig.VERSION_NAME,
                link = Constants.GITHUB_RELEASES_LINK
            )
        }
    }
}

@Composable
fun SettingsBasicLinkItem(
    @StringRes title: Int,
    subtitle: String = "",
    @DrawableRes icon: Int,
    link: String = "",
    onClick: () -> Unit = {}
) {
    val context = LocalContext.current
    SettingsItemCard(onClick = {
        if (link.isNotBlank()) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(link)
            context.startActivity(intent)
        } else onClick()
    }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = stringResource(id = title)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = stringResource(id = title),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.inverseSurface
            )
        }
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.inverseSurface
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogWithReminders(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    days: Set<String>,
    viewModel: SettingsViewModel,
    coroutineScope: CoroutineScope
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {

        val newDays = days.toMutableSet()
        val weekDays =
            listOf(
                "Sunday",
                "Monday",
                "Tuesday",
                "Wednesday",
                "Thursday",
                "Friday",
                "Saturday"
            )
        val currentTime =
            Calendar.getInstance().apply { timeInMillis = viewModel.selectedTime.value }
        val timePickerState = rememberTimePickerState(
            initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
            initialMinute = currentTime.get(Calendar.MINUTE),
            is24Hour = false,
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(650.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.select_days_for_reminder),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(10.dp))
                weekDays.forEachIndexed { _, day ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = days.contains(day),
                            onCheckedChange = {

                                if (it) {
                                    newDays.add(day)
                                } else {
                                    newDays.remove(day)
                                }
                                coroutineScope.launch {
                                    viewModel.saveReminderDaysPreferences(
                                        newDays
                                    )
                                }

                            }
                        )
                        Text(text = day)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))

                TimeInput(
                    state = timePickerState,
                )
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
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        shape = RoundedCornerShape(25.dp),
                        onClick = { onConfirmation()
                            coroutineScope.launch {
                                viewModel.saveReminderTimePreferences(getTimeInMillis(timePickerState))
                                viewModel.saveScheduleReminder(newDays,getTimeInMillis(timePickerState))
                            } },
                    ) {
                        Text(
                            stringResource(R.string.save),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsItemCard(
    modifier: Modifier = Modifier,
    hPadding: Dp = 12.dp,
    vPadding: Dp = 16.dp,
    onClick: () -> Unit = {},
    content: @Composable RowScope.() -> Unit,
) {
    Surface(
        modifier = modifier.padding(top = 5.dp, bottom = 5.dp, start = 10.dp, end = 10.dp),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.primaryContainer,
        shadowElevation = 5.dp
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(horizontal = hPadding, vertical = vPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            content = content)
    }
}

private fun exportCSV(directoryUri: Uri, context: Context, entriesList: List<EntryType>) {
    try {
        val fileName = "Log_Backup_${System.currentTimeMillis()}.csv"
        val pickedDir = DocumentFile.fromTreeUri(context, directoryUri)
        val destination = pickedDir!!.createFile("csv", fileName)
        csvWriter().open(destination?.let { context.contentResolver.openOutputStream(it.uri) }!!) {
            writeRow(listOf("[type]", "[attending name]", "[date]", "[age]", "[quantity]"))
            entriesList.forEach { entry ->
                writeRow(
                    listOf(
                        entry.type,
                        entry.attendingName,
                        entry.entryDate.getFormattedDate(),
                        entry.age,
                        entry.quantity
                    )
                )
            }
        }
        Toast.makeText(context, "Exported SuccessFully.", Toast.LENGTH_LONG).show()
    } catch (sqlEx: Exception) {
        Toast.makeText(context, "Cannot export CSV.", Toast.LENGTH_LONG).show()
    }
}

private fun Long.getFormattedDate(): String {
    val sdf = SimpleDateFormat("EEE, MMM dd, yyyy", Locale.getDefault())
    return sdf.format(this)
}

@OptIn(ExperimentalMaterial3Api::class)
fun getTimeInMillis(state: TimePickerState): Long {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, state.hour)
        set(Calendar.MINUTE, state.minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return calendar.timeInMillis
}
