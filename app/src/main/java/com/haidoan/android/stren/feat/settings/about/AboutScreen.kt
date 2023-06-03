package com.haidoan.android.stren.feat.settings.about

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.haidoan.android.stren.R
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.app.navigation.IconButtonInfo
import com.mikepenz.aboutlibraries.ui.compose.LibrariesContainer


internal const val ABOUT_SCREEN_ROUTE = "about_screen_route"

@Composable
internal fun AboutRoute(
    modifier: Modifier = Modifier,
    onBackToPreviousScreen: () -> Unit,
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit,
) {
    val aboutAppBarConfiguration = AppBarConfiguration.NavigationAppBar(
        title = "About",
        navigationIcon = IconButtonInfo.BACK_ICON.copy(clickHandler = onBackToPreviousScreen),
    )

    var isAppBarConfigured by remember { mutableStateOf(false) }
    if (!isAppBarConfigured) {
        appBarConfigurationChangeHandler(aboutAppBarConfiguration)
        isAppBarConfigured = true
    }

    AboutScreen(
        modifier = modifier
    )
}

@SuppressLint("NewApi")
@Composable
private fun AboutScreen(
    modifier: Modifier = Modifier
) {


    LibrariesContainer(
        // modifier = Modifier.weight(1f),
        showLicenseBadges = false,
        header = {
            item {
                Column(
                    modifier = modifier
                        .padding(horizontal = dimensionResource(id = R.dimen.padding_medium)),
                    horizontalAlignment = CenterHorizontally
                )
                {
                    Image(
                        modifier = Modifier.size(128.dp),
                        painter = painterResource(id = R.drawable.ic_app_logo_no_padding),
                        contentDescription = "App logo"
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Stren",
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary
                    )
                    //TODO: Remove hardcoded version
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Version: 1.0.0",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Built with Android",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier
                            .padding(top = dimensionResource(id = R.dimen.padding_small))
                            .fillMaxWidth(),
                        text = "Licenses",
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
        }
    )
    //}
}