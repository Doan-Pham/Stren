package com.haidoan.android.stren.core.designsystem.component

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.haidoan.android.stren.R

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    text: String,
    placeholder: String,
    onTextChange: (String) -> Unit,
    onBackClicked: () -> Unit,
    onSearchClicked: (String) -> Unit,
) {
    var isBackPressHandled by remember {
        mutableStateOf(false)
    }
    BackHandler(enabled = !isBackPressHandled) {
        onBackClicked()
        isBackPressHandled = true
    }

    var shouldShowCancelIcon by remember {
        mutableStateOf(false)
    }

    val focusRequester = remember { FocusRequester() }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(end = 16.dp)
            .height(56.dp)
            .background(color = Color.White),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester)
                .onGloballyPositioned {
                    focusRequester.requestFocus() // IMPORTANT
                },
            value = text,
            onValueChange = {
                shouldShowCancelIcon = !(it.isBlank() || it.isEmpty())
                onTextChange(it)
            },
            placeholder = {
                Text(
                    text = placeholder,
                    color = Color.Gray
                )
            },
            textStyle = MaterialTheme.typography.bodyLarge,
            singleLine = true,
            leadingIcon = {
                IconButton(
                    onClick = {
                        onBackClicked()
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_left),
                        contentDescription = "Back Icon",
                    )
                }
            },
            trailingIcon = {
                if (shouldShowCancelIcon) {
                    IconButton(
                        onClick = {
                            shouldShowCancelIcon = false
                            onTextChange("")
                            onSearchClicked("")
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_cancel_circle),
                            contentDescription = "Search Icon",
                        )
                    }
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearchClicked(text)
                }
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                cursorColor = Color.Black
            ))

        IconButton(
            modifier = Modifier
                .size(dimensionResource(id = R.dimen.icon_size_medium)),
            onClick = {
                onSearchClicked(text)
            }
        ) {
            Icon(
                modifier = Modifier
                    .size(dimensionResource(id = R.dimen.icon_size_medium)),
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = "Search Icon",
            )
        }
    }


}

@Composable
@Preview
fun SearchAppBarPreview() {
    var text by remember { mutableStateOf("") }
    SearchBar(
        text = text,
        placeholder = "Search exercise",
        onBackClicked = {},
        onTextChange = { text = it },
        onSearchClicked = {}
    )
}
