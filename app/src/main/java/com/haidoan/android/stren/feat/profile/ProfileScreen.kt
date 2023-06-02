package com.haidoan.android.stren.feat.profile

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

const val PROFILE_SCREEN_ROUTE = "profile_screen_route"

@Composable
internal fun ProfileRoute(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit,
) {
    var isAppBarConfigured by remember { mutableStateOf(false) }
    if (!isAppBarConfigured) {
        appBarConfigurationChangeHandler(AppBarConfiguration.NavigationAppBar())
        isAppBarConfigured = true
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ProfileScreen(
        modifier = modifier,
        uiState = uiState,
    )
}

@SuppressLint("NewApi")
@Composable
private fun ProfileScreen(
    modifier: Modifier = Modifier,
    uiState: ProfileUiState
) {
    when (uiState) {
        is ProfileUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                LoadingAnimation()
            }
        }
        is ProfileUiState.LoadComplete -> {
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
                            //TODO
                        }
                        .fillMaxWidth()
                        .padding(dimensionResource(id = R.dimen.padding_medium)),
                        iconResId = R.drawable.ic_profile,
                        iconDescription = "Icon Profile",
                        text = "Profile")

                    OptionItem(modifier = Modifier
                        .clickable {
                            //TODO
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
                        /*TODO*/
                    })
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