package com.haidoan.android.stren.feat.settings

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.haidoan.android.stren.R
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.core.designsystem.component.*

const val SETTINGS_SCREEN_ROUTE = "settings_screen_route"

@Composable
internal fun SettingsRoute(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit,
    onNavigateToEditProfile: (userId: String) -> Unit,
    onNavigateToAbout: () -> Unit,
) {
    var isAppBarConfigured by remember { mutableStateOf(false) }
    if (!isAppBarConfigured) {
        appBarConfigurationChangeHandler(AppBarConfiguration.NavigationAppBar())
        isAppBarConfigured = true
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    SettingsScreen(
        modifier = modifier,
        uiState = uiState,
        onProfileOptionClick = onNavigateToEditProfile,
        onLogoutButtonClick = viewModel::logOut,
        onAboutOptionClick = onNavigateToAbout
    )
}

@SuppressLint("NewApi")
@Composable
private fun SettingsScreen(
    modifier: Modifier = Modifier,
    uiState: SettingsUiState,
    onProfileOptionClick: (userId: String) -> Unit,
    onAboutOptionClick: () -> Unit,
    onLogoutButtonClick: () -> Unit
) {
    when (uiState) {
        is SettingsUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                LoadingAnimation()
            }
        }
        is SettingsUiState.LoadComplete -> {
            val currentUser = uiState.currentUser
            var shouldShowLogOutDialog by remember {
                mutableStateOf(false)
            }

            Column(modifier = modifier.fillMaxSize()) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(dimensionResource(id = R.dimen.padding_medium)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_large)),
                            painter = painterResource(id = R.drawable.ic_user),
                            contentDescription = "Icon User",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.padding_medium)))
                        Text(
                            text = uiState.currentUser.displayName,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    OptionItem(modifier = Modifier
                        .clickable {
                            onProfileOptionClick(currentUser.id)
                        }
                        .fillMaxWidth()
                        .padding(dimensionResource(id = R.dimen.padding_medium)),
                        iconResId = R.drawable.ic_profile,
                        iconDescription = "Icon Profile",
                        text = "Profile")

                    OptionItem(modifier = Modifier
                        .clickable {
                            onAboutOptionClick()
                        }
                        .fillMaxWidth()
                        .padding(dimensionResource(id = R.dimen.padding_medium)),
                        iconResId = R.drawable.ic_circle_question_mark,
                        iconDescription = "Icon About",
                        text = "About")

                }
                StrenOutlinedButton(
                    modifier = Modifier
                        .padding(dimensionResource(id = R.dimen.padding_medium))
                        .fillMaxWidth(),
                    text = "Logout",
                    leadingIconResId = null,
                    onClick = {
                        shouldShowLogOutDialog = true
                    })
            }

            if (shouldShowLogOutDialog) {
                SimpleConfirmationDialog(onDismissDialog = {
                    shouldShowLogOutDialog = false
                }, title = "Logout", body = "Logging out of this account?") {
                    onLogoutButtonClick()
                }
            }
        }
    }
}

@Composable
private fun OptionItem(
    modifier: Modifier = Modifier,
    iconResId: Int,
    iconDescription: String,
    text: String
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = iconDescription
        )
        Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.padding_medium)))
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium
        )
    }
}