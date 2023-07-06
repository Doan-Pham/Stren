package com.haidoan.android.stren.feat.auth.login

import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.haidoan.android.stren.R
import com.haidoan.android.stren.app.LocalFacebookCallbackManager
import com.haidoan.android.stren.app.ui.LocalSnackbarHostState
import com.haidoan.android.stren.core.designsystem.component.OutlinedTextAndIconButton
import com.haidoan.android.stren.core.designsystem.component.PasswordTextField
import com.haidoan.android.stren.core.designsystem.component.StrenOutlinedTextField
import com.haidoan.android.stren.core.designsystem.theme.Red40
import com.haidoan.android.stren.core.designsystem.theme.Red50
import com.stevdzasan.onetap.OneTapSignInWithGoogle
import com.stevdzasan.onetap.rememberOneTapSignInState
import kotlinx.coroutines.launch
import timber.log.Timber

internal const val LOGIN_SCREEN_ROUTE = "login_screen_route"
const val TEST_TAG_SCREEN_LOGIN = "Screen-Login"

@Composable
internal fun LoginScreen(
    onSignupClick: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState
    val snackbarHostState = LocalSnackbarHostState.current
    val coroutineScope = rememberCoroutineScope()

    if (uiState.isAuthFailed) {
        LaunchedEffect(key1 = true) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = uiState.errorMessage,
                    withDismissAction = true,
                    duration = SnackbarDuration.Short
                )
                viewModel.resetAuthState()
            }
        }
    } else if (uiState.isAuthSuccess) {
        LaunchedEffect(key1 = true) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = "Login success!",
                    withDismissAction = true,
                    duration = SnackbarDuration.Short
                )
                viewModel.resetAuthState()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp, bottom = 12.dp, start = 24.dp, end = 24.dp)
            .testTag(TEST_TAG_SCREEN_LOGIN)
    ) {
        Text(
            modifier = Modifier
                .padding(top = 32.dp)
                .fillMaxWidth(),
            text = stringResource(R.string.login_welcome_subtitle),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.login_welcome_title),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        StrenOutlinedTextField(
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
            text = uiState.email,
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_user),
                    contentDescription = "User icon"
                )
            },
            onTextChange = { viewModel.onEmailChange(it) },
            label = "Email",
            isError = false,
            errorText = "",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        PasswordTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            value = uiState.password,
            onValueChange = { viewModel.onPasswordChange(it) })

        if (uiState.isLoading) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 1.dp,
                modifier = Modifier
                    .size(24.dp)
                    .padding(vertical = dimensionResource(id = R.dimen.padding_large))
                    .align(Alignment.CenterHorizontally)
            )
        } else {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = dimensionResource(id = R.dimen.padding_large)),
                shape = RoundedCornerShape(15),
                onClick = {
                    viewModel.onSignInClick()
                },
                contentPadding = PaddingValues(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Red40, Red50
                                )
                            )
                        )
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Login",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = dimensionResource(id = R.dimen.padding_small)),
            text = "Or",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.padding_medium)),
        ) {
//            FacebookSignInButton(
//                onAuthSuccess = { result ->
//                    viewModel.onSignInWithFacebookClick(result.accessToken)
//                    Timber.d("facebook:onSuccess:$result")
//                }, modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(vertical = 4.dp)
//            )

            GoogleSignInButton(
                onTokenIdReceived = { tokenId ->
                    viewModel.onSignInWithGoogleClick(tokenId)
                }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )

            val signUpAnnotatedString = buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color.Black)) {
                    append("Don't have an account?")
                }
                //Start of the pushing annotation which you want to color and make them clickable later
                pushStringAnnotation(
                    tag = "SignUp",// provide tag which will then be provided when you click the text
                    annotation = "SignUp"
                )

                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.secondary)) {
                    append(" Sign up")
                }
                // when pop is called it means the end of annotation with current tag
                pop()
            }
            ClickableText(
                text = signUpAnnotatedString,
                onClick = { offset ->
                    signUpAnnotatedString.getStringAnnotations(
                        tag = "SignUp",// tag which you used in the buildAnnotatedString
                        start = offset, end = offset
                    ).firstOrNull()?.let {
                        onSignupClick()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimensionResource(id = R.dimen.padding_extra_large)),
                style = MaterialTheme.typography.bodyMedium + TextStyle(textAlign = TextAlign.Center),
            )
        }
    }
}

@Composable
private fun GoogleSignInButton(onTokenIdReceived: (String) -> Unit, modifier: Modifier = Modifier) {
    val oneTapSignInState = rememberOneTapSignInState()
    OneTapSignInWithGoogle(state = oneTapSignInState,
        clientId = stringResource(id = R.string.GOOGLE_WEB_CLIENT_ID),
        onTokenIdReceived = onTokenIdReceived,
        onDialogDismissed = { message ->
            Timber.d("One tap dialog dismissed - message: $message")
        })

    OutlinedTextAndIconButton(
        modifier = modifier,
        imageResourceId = R.drawable.ic_google,
        onClick = {
            oneTapSignInState.open()
        },
        text = "Sign in with Google",
        imageDescription = "Sign in with Google button"
    )
}

@Composable
private fun FacebookSignInButton(
    onAuthSuccess: (LoginResult) -> Unit,
    modifier: Modifier = Modifier
) {
    val callbackManager = LocalFacebookCallbackManager.current
    DisposableEffect(Unit) {
        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    onAuthSuccess(result)
                }

                override fun onCancel() {
                    Timber.d("facebook:onCancel")
                }

                override fun onError(error: FacebookException) {
                    Timber.d("facebook:onError", error)
                }
            })
        onDispose {
            LoginManager.getInstance().unregisterCallback(callbackManager)
        }
    }

    val activityResultRegistryOwner = LocalActivityResultRegistryOwner.current
    OutlinedTextAndIconButton(
        modifier = modifier,
        imageResourceId = R.drawable.ic_facebook,
        onClick = {
            if (activityResultRegistryOwner != null) {
                LoginManager.getInstance().logInWithReadPermissions(
                    activityResultRegistryOwner,
                    callbackManager,
                    listOf("email", "public_profile")
                )
            }
        },
        text = "Sign in with Facebook",
        imageDescription = "Sign up with Facebook button"
    )
}