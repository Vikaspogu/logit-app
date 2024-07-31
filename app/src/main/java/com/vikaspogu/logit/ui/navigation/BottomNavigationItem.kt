package com.vikaspogu.logit.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.vikaspogu.logit.ui.NavigationDestinations

data class BottomNavigationItem(
    val label: String = "",
    val selectedIcon: ImageVector = Icons.Filled.Home,
    val unselectedIcon: ImageVector = Icons.Outlined.Home,
    val route: String = ""
) {

    //function to get the list of bottomNavigationItems
    fun bottomNavigationItems(): List<BottomNavigationItem> {
        return listOf(
            BottomNavigationItem(
                label = NavigationDestinations.Summary.name,
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home,
                route = NavigationDestinations.Summary.name,
            ),
            BottomNavigationItem(
                label = NavigationDestinations.Entries.name,
                selectedIcon = Icons.AutoMirrored.Filled.List,
                unselectedIcon = Icons.AutoMirrored.Outlined.List,
                route = NavigationDestinations.Entries.name
            ),
            BottomNavigationItem(
                label = NavigationDestinations.Settings.name,
                selectedIcon = Icons.Filled.Settings,
                unselectedIcon = Icons.Outlined.Settings,
                route = NavigationDestinations.Settings.name
            ),
        )
    }
}