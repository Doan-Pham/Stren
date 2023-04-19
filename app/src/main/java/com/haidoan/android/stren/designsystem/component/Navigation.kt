package com.haidoan.android.stren.designsystem.component


import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.haidoan.android.stren.designsystem.theme.Gray60
import com.haidoan.android.stren.designsystem.theme.poppins
import com.haidoan.android.stren.navigation.TopLevelDestination

@Composable
fun BottomNavigationBar(
    navController: NavController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val destinations = TopLevelDestination.values().asList()

    NavigationBar(containerColor = Color.White) {
        destinations.forEach { destination ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painterResource(id = destination.iconDrawableId),
                        contentDescription = stringResource(id = destination.descriptionTextId)
                    )
                },
                label = {
                    Text(
                        text = stringResource(id = destination.titleTextId),
                        fontWeight = FontWeight.Bold,
                        fontFamily = poppins,
                        fontSize = 12.sp,
                        softWrap = false
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = Gray60,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = Gray60,
                    indicatorColor = Color.White,
                ),
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