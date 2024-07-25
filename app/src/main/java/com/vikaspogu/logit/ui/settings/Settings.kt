package com.vikaspogu.logit.ui.settings

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.documentfile.provider.DocumentFile
import androidx.navigation.NavHostController
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import com.vikaspogu.logit.BuildConfig
import com.vikaspogu.logit.R
import com.vikaspogu.logit.data.model.Entry
import com.vikaspogu.logit.data.model.EntryType
import com.vikaspogu.logit.ui.NavigationDestinations
import com.vikaspogu.logit.ui.components.BottomBar
import com.vikaspogu.logit.ui.components.TopBar
import com.vikaspogu.logit.ui.entry.formatDate
import com.vikaspogu.logit.ui.util.Constants
import kotlinx.coroutines.flow.forEach
import java.util.Locale


@Composable
fun Settings(navController: NavHostController, modifier: Modifier, viewModel: SettingsViewModel) {
    Scaffold(
        topBar = {
            TopBar(
                showBackNavigation = false,
                navController = navController,
                navDest = NavigationDestinations.Summary
            )
        }, bottomBar = {
            BottomBar(navController = navController)
        }
    ) { innerPadding ->
        SettingColumn(
            contentPadding = innerPadding,
            modifier = modifier.fillMaxSize(),
            navController,
            viewModel
        )
    }
}

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
    LazyColumn(
        modifier = modifier, contentPadding = contentPadding
    ) {
        item {
            Text(
                modifier = Modifier
                    .padding(16.dp),
                text = stringResource(id = R.string.settings),
                style = MaterialTheme.typography.titleLarge
            )
            SettingsBasicLinkItem(
                title = R.string.manage_types,
                icon = R.drawable.ic_code,
                onClick = {
                    navController.navigate(NavigationDestinations.Types.name)
                }
            )
            SettingsBasicLinkItem(
                title = R.string.export_csv,
                icon = R.drawable.ic_export,
                onClick = {
                    chooseDirectoryLauncher.launch(null)
                }
            )
        }
        item {
            Text(
                modifier = Modifier
                    .padding(16.dp),
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
    @StringRes
    title: Int,
    subtitle: String = "",
    @DrawableRes
    icon: Int,
    link: String = "",
    onClick: () -> Unit = {}
) {
    val context = LocalContext.current
    SettingsItemCard(
        onClick = {
            if (link.isNotBlank()) {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(link)
                context.startActivity(intent)
            } else onClick()
        }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = stringResource(id = title)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = stringResource(id = title),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
        )
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
    Card(
        modifier = modifier
            .padding(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        shape = MaterialTheme.shapes.large,
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(horizontal = hPadding, vertical = vPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            content = content
        )
    }
}

@Composable
fun SettingsSwitchCard(
    text: String,
    checked: Boolean,
    onCheck: (Boolean) -> Unit = {}
) {
    SettingsItemCard(
        onClick = {
            onCheck(!checked)
        },
        vPadding = 10.dp
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge
        )
        Switch(checked = checked, onCheckedChange = {
            onCheck(it)
        })
    }
}

private fun exportCSV(directoryUri: Uri, context: Context, entriesList: List<EntryType>){
    try {
        val fileName = "Log_Backup_${System.currentTimeMillis()}.csv"
        val pickedDir = DocumentFile.fromTreeUri(context, directoryUri)
        val destination = pickedDir!!.createFile("csv", fileName)
        csvWriter().open(destination?.let { context.contentResolver.openOutputStream(it.uri) }!!){
            writeRow(listOf("[type]", "[attending name]", "[date]","[age]","[quantity]"))
            entriesList.forEach {entry ->
                writeRow(listOf(entry.type,entry.attendingName,entry.entryDate.getFormattedDate(),entry.age,entry.quantity))
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