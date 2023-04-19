package com.haidoan.android.stren.designsystem.component

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.haidoan.android.stren.designsystem.theme.Gray60
import com.haidoan.android.stren.navigation.TopLevelDestination

@Composable
fun BottomNavigationBar(
    navController: NavController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val destinations = TopLevelDestination.values().asList()

    BottomNavigation(backgroundColor = Color.White, contentColor = MaterialTheme.colors.primary) {
        destinations.forEach { destination ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        painterResource(id = destination.iconDrawableId),
                        contentDescription = stringResource(id = destination.descriptionTextId)
                    )
                },
                label = { Text(text = stringResource(id = destination.titleTextId)) },
                selectedContentColor = MaterialTheme.colors.primary,
                unselectedContentColor = Gray60,
                alwaysShowLabel = true,
                selected = currentRoute == destination.route,
                onClick = {
                    navController.navigate(destination.route) {
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}