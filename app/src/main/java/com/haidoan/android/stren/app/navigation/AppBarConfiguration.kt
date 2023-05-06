package com.haidoan.android.stren.app.navigation

import androidx.compose.runtime.MutableState
import com.haidoan.android.stren.R

/**
 *
 */
sealed interface AppBarConfiguration {
    data class NavigationAppBar(
        val title: String = "Stren",
        val navigationIcon: IconButtonInfo = IconButtonInfo.APP_ICON,
        val actionIcons: List<IconButtonInfo> = emptyList()
    ) : AppBarConfiguration

    data class SearchAppBar(
        val text: MutableState<String>,
        val placeholder: String,
        val onTextChange: (String) -> Unit,
        val onSearchClicked: (String) -> Unit,
    ) : AppBarConfiguration
}

/**
 * Wrapper class for IconButton's info
 */
data class IconButtonInfo(
    val isEnabled: Boolean = true,
    val drawableResourceId: Int,
    val description: String,
    val clickHandler: () -> Unit
) {
    companion object {
        val APP_ICON = IconButtonInfo(
            isEnabled = false,
            drawableResourceId = R.drawable.ic_app_logo_no_padding,
            description = "App icon",
            clickHandler = {})

        val BACK_ICON = IconButtonInfo(
            isEnabled = true,
            drawableResourceId = R.drawable.ic_arrow_left,
            description = "Back Arrow Icon",
            clickHandler = {})
    }
}