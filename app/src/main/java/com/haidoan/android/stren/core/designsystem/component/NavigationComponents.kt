package com.haidoan.android.stren.core.designsystem.component


import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.haidoan.android.stren.R
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.app.navigation.IconButtonInfo
import com.haidoan.android.stren.app.navigation.TopLevelDestination
import com.haidoan.android.stren.core.designsystem.theme.Gray60
import com.haidoan.android.stren.core.designsystem.theme.poppins

const val TEST_TAG_BOTTOM_NAV = "Navigation-Bottom-Bar"
const val TEST_TAG_TOP_BAR = "Navigation-Top-Bar"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StrenSmallTopAppBar(
    modifier: Modifier = Modifier,
    appBarConfiguration: AppBarConfiguration.NavigationAppBar
) {
    val navigationIconColor =
        if (appBarConfiguration.navigationIcon == IconButtonInfo.APP_ICON) MaterialTheme.colorScheme.primary else Color.Black
    val titleTextStyle =
        if (appBarConfiguration.navigationIcon == IconButtonInfo.APP_ICON) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium

    TopAppBar(
        modifier = modifier, title = {
            Row {
                Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.padding_medium)))
                Text(appBarConfiguration.title, style = titleTextStyle)
            }
        },
        navigationIcon = {
            IconButton(
                onClick = appBarConfiguration.navigationIcon.clickHandler,
                enabled = appBarConfiguration.navigationIcon.isEnabled
            ) {
                Icon(
                    painter = painterResource(id = appBarConfiguration.navigationIcon.drawableResourceId),
                    contentDescription = appBarConfiguration.navigationIcon.description,
                    tint = navigationIconColor,
                    modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_large))
                )
            }
        }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
        actions = {
            appBarConfiguration.actionIcons.forEach {
                if (it.shouldShowBadge.value) {
                    BadgedBox(badge = {
                        Badge(
                            modifier = Modifier
                                .size(12.dp)
                                .offset(
                                    y = (14).dp,
                                    x = (-14).dp
                                )
                        )
                    }) {
                        IconButton(onClick = it.clickHandler) {
                            Icon(
                                painter = painterResource(it.drawableResourceId),
                                contentDescription = it.description
                            )
                        }
                    }
                } else {
                    IconButton(onClick = it.clickHandler) {
                        Icon(
                            painter = painterResource(it.drawableResourceId),
                            contentDescription = it.description
                        )
                    }
                }

            }

        }
    )
}

@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    destinations: List<TopLevelDestination>,
    currentTopLevelDestination: TopLevelDestination?,
    onNavigateToDestination: (TopLevelDestination) -> Unit,
) {

    NavigationBar(
        modifier = modifier.testTag(TEST_TAG_BOTTOM_NAV),
        containerColor = Color.Transparent
    ) {
        destinations.forEach { destination ->
            val isSelected = destination.route == currentTopLevelDestination?.route
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
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Gray60,
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
                selected = isSelected,
                onClick = { onNavigateToDestination(destination) }
            )
        }
    }
}