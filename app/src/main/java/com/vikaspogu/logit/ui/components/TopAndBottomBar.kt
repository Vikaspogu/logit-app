package com.vikaspogu.logit.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.vikaspogu.logit.R
import com.vikaspogu.logit.ui.NavigationDestinations
import com.vikaspogu.logit.ui.navigation.BottomNavigationItem
import com.vikaspogu.logit.ui.theme.SmallHeadingStyle
import com.vikaspogu.logit.ui.theme.TitleBarStyleLarge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    showBackNavigation: Boolean,
    navController: NavHostController,
    navDest: NavigationDestinations
) {
    CenterAlignedTopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = TitleBarStyleLarge
                )
                Image(
                    modifier = Modifier
                        .size(dimensionResource(id = R.dimen.image_size))
                        .padding(dimensionResource(id = R.dimen.padding_small)),
                    painter = painterResource(id = R.drawable.syringe_icon),
                    contentDescription = null
                )
            }
        },
        navigationIcon = {
            if (showBackNavigation) {
                IconButton(onClick = {
                    navController.navigate(navDest.name)
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.back)
                    )
                }
            }
        }
    )
}

@Composable
fun BottomBar(navController: NavHostController) {
    NavigationBar(modifier = Modifier.clip(shape = MaterialTheme.shapes.large)) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        BottomNavigationItem().bottomNavigationItems().forEachIndexed { _, navigationItem ->
            //iterating all items with their respective indexes
            NavigationBarItem(
                selected = currentRoute == navigationItem.route,
                label = {
                    Text(navigationItem.label, style = SmallHeadingStyle)
                },
                icon = {
                    Icon(
                        if(currentRoute == navigationItem.route){
                            navigationItem.selectedIcon
                        }else{
                            navigationItem.unselectedIcon
                        },
                        contentDescription = navigationItem.label,
                    )
                },
                colors = NavigationBarItemDefaults.colors(selectedTextColor = MaterialTheme.colorScheme.primary, unselectedTextColor = MaterialTheme.colorScheme.inverseSurface),
                onClick = {
                    navController.navigate(navigationItem.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
