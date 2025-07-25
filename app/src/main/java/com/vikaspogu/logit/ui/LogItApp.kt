package com.vikaspogu.logit.ui

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vikaspogu.logit.ui.entry.AddEditEntry
import com.vikaspogu.logit.ui.entry.EntriesScreen
import com.vikaspogu.logit.ui.home.SummaryDetailsScreen
import com.vikaspogu.logit.ui.home.SummaryScreen
import com.vikaspogu.logit.ui.manage.ManagePersons
import com.vikaspogu.logit.ui.manage.ManageType
import com.vikaspogu.logit.ui.settings.Settings
import com.vikaspogu.logit.ui.startup.StartupScreen

enum class NavigationDestinations {
    Summary, Entries, Settings, Types, Persons, Startup
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LogItApp(navController: NavHostController = rememberNavController(), modifier: Modifier, username: String) {
    Scaffold {
        NavHost(
            navController = navController,
            startDestination = if (username.isNullOrBlank()) NavigationDestinations.Startup.name else NavigationDestinations.Summary.name,
        ) {
            composable(route = NavigationDestinations.Startup.name) {
                StartupScreen(
                    navController = navController,
                    modifier = modifier,
                )
            }
            composable(route = NavigationDestinations.Summary.name) {
                SummaryScreen(
                    navController = navController,
                    modifier = modifier,
                )
            }
            composable(route = NavigationDestinations.Entries.name) {
                EntriesScreen(
                    navController = navController,
                    modifier,
                )
            }
            composable(route = "addEdit/{action}/{entryId}") {
                AddEditEntry(
                    navController = navController,
                    modifier
                )
            }
            composable(route = NavigationDestinations.Settings.name) {
                Settings(
                    navController = navController,
                    modifier,
                )
            }
            composable(route = NavigationDestinations.Types.name, enterTransition = {
                when (initialState.destination.route) {
                    NavigationDestinations.Settings.name -> slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Up,
                        animationSpec = tween(700)
                    )

                    else -> null
                }
            }, exitTransition = {
                when (targetState.destination.route) {
                    NavigationDestinations.Settings.name -> slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Down,
                        animationSpec = tween(700)
                    )

                    else -> null
                }
            }) {
                ManageType(
                    navController = navController,
                    modifier
                )
            }
            composable(route = NavigationDestinations.Persons.name, enterTransition = {
                when (initialState.destination.route) {
                    NavigationDestinations.Settings.name -> slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Up,
                        animationSpec = tween(700)
                    )

                    else -> null
                }
            }, exitTransition = {
                when (targetState.destination.route) {
                    NavigationDestinations.Settings.name -> slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Down,
                        animationSpec = tween(700)
                    )

                    else -> null
                }
            }) {
                ManagePersons(
                    navController = navController,
                    modifier
                )
            }
            composable(route = "summaryDetails/{typeId}", enterTransition = {
                when (initialState.destination.route) {
                    NavigationDestinations.Summary.name -> slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Up,
                        animationSpec = tween(700)
                    )

                    else -> null
                }
            }, exitTransition = {
                when (targetState.destination.route) {
                    NavigationDestinations.Summary.name -> slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Down,
                        animationSpec = tween(700)
                    )

                    else -> null
                }
            }) {
                SummaryDetailsScreen(
                    navController = navController,
                    modifier = modifier,
                )
            }
        }
    }
}