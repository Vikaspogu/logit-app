package com.vikaspogu.logit.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.vikaspogu.logit.ui.NavigationDestinations

data class BottomNavigationItem(
    val label: String = "",
    val icon: ImageVector = Icons.Filled.Home,
    val route: String = ""
) {

    //function to get the list of bottomNavigationItems
    fun bottomNavigationItems(): List<BottomNavigationItem> {
        return listOf(
            BottomNavigationItem(
                label = NavigationDestinations.Summary.name,
                icon = Icons.Filled.Home,
                route = NavigationDestinations.Summary.name
            ),
            BottomNavigationItem(
                label = NavigationDestinations.Entries.name,
                icon = Icons.AutoMirrored.Filled.List,
                route = NavigationDestinations.Entries.name
            ),
            BottomNavigationItem(
                label = NavigationDestinations.Settings.name,
                icon = Icons.Filled.Settings,
                route = NavigationDestinations.Settings.name
            ),
        )
    }
}