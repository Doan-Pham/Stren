package com.example.stren.feat.auth

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import com.example.stren.R
import com.example.stren.ui.theme.Gray90
import com.example.stren.ui.theme.Red40
import com.example.stren.ui.theme.Red50

private const val TAG = "LoginScreen"

@Composable
fun LoginScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
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
        var currentUserEmail by remember {
            mutableStateOf("")
        }

        var currentPassword by remember {
            mutableStateOf("")
        }

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 8.dp),
            value = currentUserEmail,
            onValueChange = { currentUserEmail = it },
            singleLine = true,
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
                IconButton(onClick = { currentUserEmail = "" }) {
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
            value = currentPassword,
            onValueChange = { currentPassword = it },
            singleLine = true,
            label = {
                Text(text = "Password")
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_password),
                    contentDescription = "User icon"
                )
            },
            trailingIcon = {
                IconButton(onClick = { currentPassword = "" }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_cancel_circle),
                        contentDescription = "User icon"
                    )
                }
            },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = dimensionResource(id = R.dimen.padding_large)),
            shape = RoundedCornerShape(15),
            onClick = { /*TODO*/ },
            contentPadding = PaddingValues(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .background(Brush.horizontalGradient(colors = listOf(Red40, Red50)))
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

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = dimensionResource(id = R.dimen.padding_small)),
            text = "or continue with",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.padding_medium)),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            OutlinedIconButton(
                onClick = { /*TODO*/ },
                modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_extra_large)),
                shape = RoundedCornerShape(10),
                border = BorderStroke(1.dp, Gray90)
            ) {
                Image(
                    modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_large)),
                    painter = painterResource(id = R.drawable.ic_facebook),
                    contentDescription = "Sign up with Facebook button"
                )
            }
            OutlinedIconButton(
                onClick = { /*TODO*/ },
                modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_extra_large)),
                shape = RoundedCornerShape(10),
                border = BorderStroke(1.dp, Gray90)
            ) {
                Image(
                    modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_large)),
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = "Sign up with Google button"
                )
            }
            OutlinedIconButton(
                onClick = { /*TODO*/ },
                modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_extra_large)),
                shape = RoundedCornerShape(10),
                border = BorderStroke(1.dp, Gray90)
            ) {
                Image(
                    modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_large)),
                    painter = painterResource(id = R.drawable.ic_phone),
                    contentDescription = "Sign up with phone number button"
                )
            }
        }

        val signUpAnnotatedString = buildAnnotatedString {
            withStyle(style = SpanStyle(color = Color.Black)) {
                append("Already have an account?")
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
                    start = offset,
                    end = offset
                ).firstOrNull()?.let {
                    //TODO: Signup click
                    Log.d(TAG, "Sign up clicked")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimensionResource(id = R.dimen.padding_extra_large)),
            style = MaterialTheme.typography.bodyMedium + TextStyle(textAlign = TextAlign.Center),
        )
    }
}