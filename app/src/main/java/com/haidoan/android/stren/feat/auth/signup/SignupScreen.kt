package com.haidoan.android.stren.feat.auth.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.haidoan.android.stren.R
import com.haidoan.android.stren.app.ui.LocalSnackbarHostState
import com.haidoan.android.stren.core.designsystem.component.PasswordTextField
import com.haidoan.android.stren.core.designsystem.component.StrenOutlinedTextField
import com.haidoan.android.stren.core.designsystem.theme.Red40
import com.haidoan.android.stren.core.designsystem.theme.Red50
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal const val SIGNUP_SCREEN_ROUTE = "signup_screen_route"

@Composable
internal fun SignupScreen(
    onCreateAccountSuccess: () -> Unit,
    onSignInClick: () -> Unit,
    viewModel: SignupViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState
    val snackbarHostState = LocalSnackbarHostState.current
    val coroutineScope = rememberCoroutineScope()

    if (uiState.isSignupFailed) {
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
    } else if (uiState.isSignupSuccess) {
        LaunchedEffect(key1 = true) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = "A verification email has been sent!",
                    withDismissAction = true,
                    duration = SnackbarDuration.Long
                )
                viewModel.resetAuthState()
            }
            delay(1000)
            onCreateAccountSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            modifier = Modifier
                .padding(top = 32.dp)
                .fillMaxWidth(),
            text = stringResource(R.string.signup_welcome_subtitle),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.signup_welcome_title),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        StrenOutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 8.dp),
            text = uiState.email,
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_user),
                    contentDescription = "User icon"
                )
            },
            onTextChange = { viewModel.onEmailChange(it) },
            label = "Email",
            isError = !uiState.isEmailValid,
            errorText = "Invalid email",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        PasswordTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 16.dp),
            value = uiState.password,
            isError = !uiState.isPasswordValid,
            errorText = "Password must contain at least 8 characters",
            onValueChange = { viewModel.onPasswordChange(it) })

        PasswordTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 16.dp),
            label = "Confirm password",
            value = uiState.repeatPassword,
            isError = !uiState.isRepeatPasswordValid,
            errorText = "Passwords don't match",
            onValueChange = { viewModel.onRepeatPasswordChange(it) })

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
                enabled = uiState.isInputValid,
                onClick = {
                    viewModel.onSignUpClick()
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
                            ), alpha = if (uiState.isInputValid) 1f else 0.3f
                        )
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Sign Up",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }

        val signUpAnnotatedString = buildAnnotatedString {
            withStyle(style = SpanStyle(color = Color.Black)) {
                append("Already have an account?")
            }
            //Start of the pushing annotation which you want to color and make them clickable later
            pushStringAnnotation(
                tag = "SignIn",// provide tag which will then be provided when you click the text
                annotation = "SignIn"
            )

            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.secondary)) {
                append(" Sign in")
            }
            // when pop is called it means the end of annotation with current tag
            pop()
        }
        ClickableText(
            text = signUpAnnotatedString,
            onClick = { offset ->
                signUpAnnotatedString.getStringAnnotations(
                    tag = "SignIn",// tag which you used in the buildAnnotatedString
                    start = offset, end = offset
                ).firstOrNull()?.let {
                    onSignInClick()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimensionResource(id = R.dimen.padding_extra_large)),
            style = MaterialTheme.typography.bodyMedium + TextStyle(textAlign = TextAlign.Center),
        )
    }
}