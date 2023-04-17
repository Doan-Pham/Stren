package com.example.stren.feat.auth.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ContentAlpha
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.stren.R
import com.example.stren.app.LocalSnackbarHostState
import com.example.stren.ui.theme.Red40
import com.example.stren.ui.theme.Red50
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SignupScreen(
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
                    message = "Creating new account success!",
                    withDismissAction = true,
                    duration = SnackbarDuration.Short
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
            text = stringResource(R.string.signup_welcom_title),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 8.dp),
            value = uiState.email,
            onValueChange = { viewModel.onEmailChange(it) },
            singleLine = true,
            isError = !uiState.isEmailValid,
            supportingText = {
                if (!uiState.isEmailValid)
                    Text("Invalid email")
            },
            label = {
                Text(text = "Email")
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_user),
                    contentDescription = "User icon"
                )
            },
            trailingIcon = {
                IconButton(onClick = { viewModel.onEmailChange("") }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_cancel_circle),
                        contentDescription = "User icon"
                    )
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 16.dp),
            value = uiState.password,
            onValueChange = { viewModel.onPasswordChange(it) },
            singleLine = true,
            isError = !uiState.isPasswordValid,
            supportingText = {
                if (!uiState.isPasswordValid)
                    Text("Password must contain at least 8 characters")
            },
            label = {
                Text(text = "Password")
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_password),
                    contentDescription = "Password icon"
                )
            },
            trailingIcon = {
                IconButton(onClick = { viewModel.onPasswordChange("") }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_cancel_circle),
                        contentDescription = "Delete icon"
                    )
                }
            },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 16.dp),
            value = uiState.repeatPassword,
            onValueChange = { viewModel.onRepeatPasswordChange(it) },
            singleLine = true,
            isError = !uiState.isRepeatPasswordValid,
            supportingText = {
                if (!uiState.isRepeatPasswordValid) {
                    Text("Passwords don't match")
                }
            },
            label = {
                Text(text = "Confirm password")
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_password),
                    contentDescription = "Password icon"
                )
            },
            trailingIcon = {
                IconButton(onClick = { viewModel.onRepeatPasswordChange("") }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_cancel_circle),
                        contentDescription = "Delete icon"
                    )
                }
            },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
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
                            ), alpha = if (uiState.isInputValid) 1f else ContentAlpha.disabled
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