package com.haidoan.android.stren.app.navigation

import com.haidoan.android.stren.R

/**
 *
 */
sealed interface AppBarConfiguration {
    data class NavigationAppBar(
        val screenRoute: String = "",
        val titleResourceId: Int = R.string.app_name,
        val navigationButtonEnabled: Boolean = false,
        val navigationIcon: IconButtonInfo = IconButtonInfo(
            drawableResourceId = R.drawable.ic_app_logo_no_padding,
            description = "App icon",
            clickHandler = {}),
        val actionIcons: List<IconButtonInfo> = emptyList()
    ) : AppBarConfiguration

    data class SearchAppBar(
        val text: String,
        val placeholder: String,
        val onTextChange: (String) -> Unit,
        val onSearchClicked: (String) -> Unit,
    ) : AppBarConfiguration
}

/**
 * Wrapper class for IconButton's info
 */
data class IconButtonInfo(
    val drawableResourceId: Int,
    val description: String,
    val clickHandler: () -> Unit
)