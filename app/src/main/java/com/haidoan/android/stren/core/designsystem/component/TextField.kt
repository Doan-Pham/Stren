package com.haidoan.android.stren.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.haidoan.android.stren.R
import com.haidoan.android.stren.core.designsystem.theme.Gray95

@Composable
fun StrenOutlinedTextField(
    text: String,
    onTextChange: (String) -> Unit,
    label: String,
    isError: Boolean,
    errorText: String
) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 8.dp),
        value = text,
        onValueChange = onTextChange,
        singleLine = true,
        label = {
            Text(text = label)
        },
        trailingIcon = {
            IconButton(onClick = { onTextChange("") }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_cancel_circle),
                    contentDescription = "Cancel icon"
                )
            }
        },
        isError = isError,
        supportingText = {
            if (isError) Text(text = errorText)
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
) {
    val interactionSource = remember { MutableInteractionSource() }
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
        modifier = modifier
            .background(
                color = Gray95,
                shape = RoundedCornerShape(25.dp)
            )
            .height(40.dp),
        singleLine = true,
        interactionSource = interactionSource,
        keyboardOptions = keyboardOptions
    ) {
        TextFieldDefaults.DecorationBox(
            enabled = true,
            value = value,
            innerTextField = it,
            singleLine = true,
            visualTransformation = VisualTransformation.None,
            contentPadding = TextFieldDefaults.contentPaddingWithoutLabel(
                top = 0.dp,
                bottom = 0.dp,
            ),
            interactionSource = interactionSource,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Gray95,
                unfocusedContainerColor = Gray95,
                focusedTextColor = Black,
                focusedIndicatorColor = Color.Transparent, //hide the indicator
                unfocusedIndicatorColor = Color.Transparent
            ),
        )
    }
}

/**
 * A Dropdown menu that show a TextField's selected item
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedDropDownMenuTextField(
    textFieldLabel: String,
    defaultSelectedText: String,
    menuItemsTextAndClickHandler: Map<String, () -> Unit>,
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(defaultSelectedText) }
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            OutlinedTextField(
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = White,
                    unfocusedContainerColor = White
                ),
                label = { Text(text = textFieldLabel) },
                value = selectedText,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                modifier = Modifier.background(White),
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                menuItemsTextAndClickHandler.forEach {
                    DropdownMenuItem(
                        modifier = Modifier.background(White),
                        text = { Text(it.key) },
                        onClick = {
                            selectedText = it.key
                            it.value()
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}