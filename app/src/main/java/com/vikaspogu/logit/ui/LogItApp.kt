package com.vikaspogu.logit.ui

import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vikaspogu.logit.ui.entry.AddEntry
import com.vikaspogu.logit.ui.entry.AddEntryViewModel
import com.vikaspogu.logit.ui.entry.EditEntry
import com.vikaspogu.logit.ui.entry.EditEntryViewModel
import com.vikaspogu.logit.ui.entry.EntriesScreen
import com.vikaspogu.logit.ui.entry.EntriesViewModel
import com.vikaspogu.logit.ui.home.SummaryScreen
import com.vikaspogu.logit.ui.home.SummaryViewModel
import com.vikaspogu.logit.ui.settings.Settings

enum class NavigationDestinations() {
    Summary,
    Entries,
    Add,
    Edit,
    Settings
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LogItApp(navController: NavHostController = rememberNavController(), modifier: Modifier) {

    Scaffold {
        NavHost(
            navController = navController,
            startDestination = NavigationDestinations.Summary.name
        ) {
            composable(route = NavigationDestinations.Summary.name) {
                SummaryScreen(
                    navController = navController,
                    modifier = modifier,
                    viewModel(factory = SummaryViewModel.factory)
                )
            }
            composable(route = NavigationDestinations.Add.name) {
                AddEntry(
                    navController = navController,
                    viewModel(factory = AddEntryViewModel.factory),
                    modifier
                )
            }
            composable(route = NavigationDestinations.Entries.name) {
                EntriesScreen(
                    navController = navController,
                    modifier,
                    viewModel(factory = EntriesViewModel.factory),
                )
            }
            composable(route = "edit/{entryId}") {
                EditEntry(
                    navController = navController,
                    viewModel(factory = EditEntryViewModel.factory),
                    modifier
                )
            }
            composable(route = NavigationDestinations.Settings.name) {
                Settings(
                    navController = navController,
                    modifier
                )
            }
        }
    }
}