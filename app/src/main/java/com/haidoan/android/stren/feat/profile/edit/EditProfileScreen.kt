package com.haidoan.android.stren.feat.profile.edit

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.haidoan.android.stren.R
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.app.navigation.IconButtonInfo
import com.haidoan.android.stren.app.ui.LocalSnackbarHostState
import com.haidoan.android.stren.core.designsystem.component.*
import com.haidoan.android.stren.core.model.User
import com.haidoan.android.stren.core.utils.ListUtils.replaceWith
import kotlinx.coroutines.launch
import timber.log.Timber

internal const val EDIT_PROFILE_SCREEN_ROUTE = "edit_profile_screen_route"
internal const val USER_ID_EDIT_PROFILE_NAV_ARG = "USER_ID_EDIT_PROFILE_NAV_ARG"

@Composable
internal fun EditProfileRoute(
    modifier: Modifier = Modifier,
    viewModel: EditProfileViewModel = hiltViewModel(),
    onBackToPreviousScreen: () -> Unit,
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var isError by remember {
        mutableStateOf(false)
    }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = LocalSnackbarHostState.current

    val editProfileAppBarConfiguration = AppBarConfiguration.NavigationAppBar(
        title = "Edit Profile",
        navigationIcon = IconButtonInfo.BACK_ICON.copy(clickHandler = onBackToPreviousScreen),
        actionIcons = listOf(
            IconButtonInfo(
                drawableResourceId = R.drawable.ic_check_mark,
                description = "MenuItem Check Mark",
                clickHandler = {
                    if (isError) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Please fill out all the fields",
                                withDismissAction = true,
                                duration = SnackbarDuration.Short
                            )
                        }
                    } else {
                        viewModel.saveProfile()
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Profile updated!",
                                withDismissAction = true,
                                duration = SnackbarDuration.Short
                            )
                        }
                        onBackToPreviousScreen()
                    }
                })
        )
    )

    var isAppBarConfigured by remember { mutableStateOf(false) }
    if (!isAppBarConfigured) {
        appBarConfigurationChangeHandler(editProfileAppBarConfiguration)
        isAppBarConfigured = true
    }

    EditProfileScreen(
        modifier = modifier,
        uiState = uiState,
        onUiStateChange = viewModel::modifyUiState,
        onErrorStatusChange = { isError = true }
    )
}

@SuppressLint("NewApi")
@Composable
private fun EditProfileScreen(
    modifier: Modifier = Modifier,
    uiState: EditProfileUiState,
    onErrorStatusChange: (Boolean) -> Unit,
    onUiStateChange: (User) -> Unit
) {
    when (uiState) {
        is EditProfileUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                LoadingAnimation()
            }
        }
        is EditProfileUiState.LoadComplete -> {
            Timber.d("user: ${uiState.currentUser}")
            val currentUser = uiState.currentUser
            val age = currentUser.age
            val displayName = currentUser.displayName
            val biometrics = currentUser.biometricsRecords
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(dimensionResource(id = R.dimen.padding_medium)),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
            ) {
                StrenOutlinedTextField(
                    text = displayName,
                    onTextChange = {
                        onUiStateChange(currentUser.copy(displayName = it))
                    },
                    label = "Name",
                    isError = displayName.isBlank(),
                    errorText = "Field can't be empty"
                )

                ExposedDropDownMenuTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            bottom = dimensionResource(id = R.dimen.padding_small)
                        ),
                    textFieldLabel = "Sex",
                    selectedText = currentUser.sex,
                    menuItemsTextAndClickHandler = uiState.sexes.associate {
                        it.name to { onUiStateChange(currentUser.copy(sex = it.name)) }
                    }
                )

                OutlinedNumberTextField(
                    modifier = Modifier.fillMaxWidth(),
                    number = age,
                    onValueChange = {
                        onUiStateChange(currentUser.copy(age = it.toLong()))
                    },
                    label = "Age",
                    isError = age == 0L,
                    errorText = "Field can't be empty"
                )
                biometrics.forEach { record ->
                    OutlinedNumberTextField(
                        modifier = Modifier.fillMaxWidth(),
                        number = record.value,
                        onValueChange = { value ->
                            val newRecords =
                                biometrics.replaceWith(record.copy(value = value.toFloat())) {
                                    it.biometricsId == record.biometricsId
                                }
                            onUiStateChange(currentUser.copy(biometricsRecords = newRecords))
                        },
                        label = record.biometricsName,
                        suffixText = record.measurementUnit,
                        isError = record.value == 0F,
                        errorText = "Field can't be empty"
                    )
                }

                val isError =
                    displayName.isBlank() || biometrics.any { it.value == 0f } || age == 0L
                onErrorStatusChange(isError)
            }
        }
    }
}