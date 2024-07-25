package com.vikaspogu.logit.ui

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vikaspogu.logit.ui.entry.AddEditEntry
import com.vikaspogu.logit.ui.entry.AddEntryViewModel
import com.vikaspogu.logit.ui.entry.EntriesScreen
import com.vikaspogu.logit.ui.entry.EntriesViewModel
import com.vikaspogu.logit.ui.home.SummaryScreen
import com.vikaspogu.logit.ui.home.SummaryViewModel
import com.vikaspogu.logit.ui.settings.Settings
import com.vikaspogu.logit.ui.settings.SettingsViewModel
import com.vikaspogu.logit.ui.type.ManageType
import com.vikaspogu.logit.ui.type.ManageTypeViewModel

enum class NavigationDestinations {
    Summary,
    Entries,
    Settings,
    Types
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LogItApp(navController: NavHostController = rememberNavController(), modifier: Modifier) {

    Scaffold {
        NavHost(
            navController = navController,
            startDestination = NavigationDestinations.Summary.name,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(700)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(700)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(700)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(700)
                )
            }
        ) {
            composable(route = NavigationDestinations.Summary.name) {
                SummaryScreen(
                    navController = navController,
                    modifier = modifier,
                    viewModel(factory = SummaryViewModel.factory)
                )
            }
            composable(route = NavigationDestinations.Entries.name) {
                EntriesScreen(
                    navController = navController,
                    modifier,
                    viewModel(factory = EntriesViewModel.factory),
                )
            }
            composable(route = "addEdit/{action}/{entryId}") {
                AddEditEntry(
                    navController = navController,
                    viewModel(factory = AddEntryViewModel.factory),
                    modifier
                )
            }
            composable(route = NavigationDestinations.Settings.name) {
                Settings(
                    navController = navController,
                    modifier,
                    viewModel(factory = SettingsViewModel.factory),
                )
            }
            composable(route = NavigationDestinations.Types.name) {
                ManageType(
                    navController = navController,
                    viewModel(factory = ManageTypeViewModel.factory),
                    modifier
                )
            }
        }
    }
}