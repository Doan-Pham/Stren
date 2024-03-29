package com.haidoan.android.stren.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.haidoan.android.stren.R
import com.haidoan.android.stren.core.designsystem.theme.Gray95
import com.haidoan.android.stren.core.utils.NumberUtils.castTo
import com.haidoan.android.stren.core.utils.ValidationUtils
import timber.log.Timber
import java.text.DecimalFormat

@Composable
fun StrenOutlinedTextField(
    modifier: Modifier = Modifier,
    text: String,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    onTextChange: (String) -> Unit,
    leadingIcon: (@Composable () -> Unit)? = null,
    label: String,
    trailingIcon: (@Composable () -> Unit)? = {
        IconButton(onClick = { onTextChange("") }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_cancel_circle),
                contentDescription = "Cancel icon"
            )
        }
    },
    isError: Boolean? = null,
    singleLine: Boolean = true,
    errorText: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
) {
    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth(),
        enabled = enabled,
        readOnly = readOnly,
        value = text,
        onValueChange = onTextChange,
        singleLine = singleLine,
        textStyle = LocalTextStyle.current.copy(lineHeight = 20.sp),
        label = {
            Text(text = label)
        },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        isError = isError == true,
        supportingText = {
            if (isError == true) Text(text = errorText)
        },
        colors = TextFieldDefaults.colors(
            errorContainerColor = Transparent,
            unfocusedContainerColor = Transparent,
            focusedContainerColor = Transparent,
            disabledTextColor = MaterialTheme.colorScheme.onBackground,
            disabledContainerColor = Transparent,
            disabledIndicatorColor = MaterialTheme.colorScheme.onBackground,
            disabledLabelColor = MaterialTheme.colorScheme.onBackground,
        ),
        keyboardOptions = keyboardOptions
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
                color = Gray95, shape = RoundedCornerShape(25.dp)
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
                focusedIndicatorColor = Transparent, //hide the indicator
                unfocusedIndicatorColor = Transparent
            ),
        )
    }
}

@Composable
inline fun <reified NumberType : Number> SimpleNumberTextField(
    modifier: Modifier,
    value: NumberType,
    crossinline onValueChange: (NumberType) -> Unit
) {
    NumberTextFieldWrapper(value = value, onValueChange = onValueChange) { text, onTextChange ->
        SimpleTextField(
            modifier = modifier,
            value = text,
            onValueChange = onTextChange
        )
    }
}

@Composable
inline fun <reified NumberType : Number> OutlinedNumberTextField(
    modifier: Modifier,
    number: NumberType,
    label: String,
    crossinline onValueChange: (NumberType) -> Unit,
    suffixText: String? = null,
    isError: Boolean, errorText: String
) {
    NumberTextFieldWrapper(
        value = number,
        onValueChange = onValueChange,
        numberTextFieldComposable = { text, onTextChange ->
            OutlinedTextField(
                modifier = modifier,
                label = { Text(text = label) },
                value = text,
                onValueChange = onTextChange,
                suffix = {
                    if (!suffixText.isNullOrBlank()) {
                        Text(text = suffixText)
                    }
                },
                isError = isError,
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Transparent,
                    focusedContainerColor = Transparent,
                    errorContainerColor = Transparent,
                    disabledTextColor = MaterialTheme.colorScheme.onBackground,
                    disabledContainerColor = Transparent,
                    disabledIndicatorColor = MaterialTheme.colorScheme.onBackground,
                    disabledLabelColor = MaterialTheme.colorScheme.onBackground,
                ),
                supportingText = {
                    if (isError) Text(text = errorText)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        })
}

/**
 * Encapsulate the logic for sanitizing textfield's number input (Must have "inline" modifier to use "reified" type parameter)
 * @param NumberType The type of number input, must be subclass of [Number], has "reified" modifier to be accessed as parameters inside function body
 *
 * Reference:
 */
@Composable
inline fun <reified NumberType : Number> NumberTextFieldWrapper(
    value: NumberType,
    crossinline onValueChange: (NumberType) -> Unit,
    digitCountBehindDecimalPoint: Int = 2,
    numberTextFieldComposable: @Composable
        (text: String, onTextChange: (String) -> Unit) -> Unit
) {
    when (NumberType::class) {
        Byte::class, Short::class, Long::class, Int::class -> {
            val textFieldValue = if (value.toByte().compareTo(0) == 0) "" else value.toString()
            numberTextFieldComposable(text = textFieldValue, onTextChange = {
                val newTextFieldValue = it.filter { char -> char.isDigit() }
                val newNumberValue =
                    if (newTextFieldValue.isEmpty() || newTextFieldValue.isBlank()) 0L
                    else newTextFieldValue.toLong()

                @Suppress("RemoveExplicitTypeArguments")
                onValueChange(newNumberValue.castTo<NumberType>())
            })
        }
        Double::class, Float::class -> {
            /**
             * TextField should only show decimal point in 2 cases:
             * - The value behind decimal point is not 0
             * - That decimal point is input from user
             *
             * So "hasDecimalPoint" var solves the 2nd case and "previousTextFieldValue" helps
            when user deletes the decimal point and reset "hasDecimalPoint"
             */
            var hasDecimalPoint by remember { mutableStateOf(false) }
            var previousTextFieldValue by remember { mutableStateOf("") }
            var isUserTyping by remember { mutableStateOf(false) }

            val decimalFormatPattern = buildString {
                append("#.")
                append("#".repeat(digitCountBehindDecimalPoint))
            }
            val df = DecimalFormat(decimalFormatPattern)
            val numberAfterCasting = df.format(value).toDouble()

            val textFieldValue =
                if (numberAfterCasting == 0.0 && !isUserTyping) ""
                else if (numberAfterCasting.rem(1) == 0.0) {
                    if (hasDecimalPoint) value.toLong().toString() + "."
                    else numberAfterCasting.toLong().toString()
                } else numberAfterCasting.toString()

            numberTextFieldComposable(text = textFieldValue, onTextChange = { newTextFieldValue ->
                /**
                 *  This block solves the decimal point input problem:
                - When user adds decimal point, it should be shown,
                - When user deletes decimal point, it should be gone and the value become
                the digits before decimal point
                Without this block, a "40" value will be shown as "40.0" automatically
                which is confusing, and a "40.0" value when deleting the decimal point
                will become "400" not "40", since the zero behind decimal point is not deleted
                 */
                var decimalSanitizedTextFieldValue: String

                if (newTextFieldValue.contains('.')) {
                    hasDecimalPoint = true
                    previousTextFieldValue = newTextFieldValue
                    decimalSanitizedTextFieldValue = newTextFieldValue
                } else {
                    if (hasDecimalPoint) {
                        decimalSanitizedTextFieldValue =
                            previousTextFieldValue.substringBefore('.')
                        hasDecimalPoint = false
                    } else {
                        decimalSanitizedTextFieldValue = newTextFieldValue
                    }
                }

                if (decimalSanitizedTextFieldValue.contains('.') &&
                    decimalSanitizedTextFieldValue.substringAfter('.').length > digitCountBehindDecimalPoint
                ) {
                    decimalSanitizedTextFieldValue = decimalSanitizedTextFieldValue.dropLast(1)
                }
                Timber.d("decimalSanitizedTextFieldValue: $decimalSanitizedTextFieldValue")
                val sanitizedTextFieldValue =
                    ValidationUtils.validateDouble(decimalSanitizedTextFieldValue)

                isUserTyping =
                    sanitizedTextFieldValue.isNotEmpty() && sanitizedTextFieldValue.isNotBlank()

                val newNumberValue =
                    if (sanitizedTextFieldValue.isEmpty() || sanitizedTextFieldValue.isBlank()) 0.0 else sanitizedTextFieldValue.toDouble()

                @Suppress("RemoveExplicitTypeArguments")
                onValueChange(df.format(newNumberValue).toDouble().castTo<NumberType>())
            })

        }
    }
}


/**
 * A Dropdown menu that show a TextField's selected item
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedDropDownMenuTextField(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textFieldLabel: String,
    selectedText: String,
    menuItemsTextAndClickHandler: Map<String, () -> Unit>,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = modifier
    ) {
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = {
            expanded = !expanded
        }) {
            OutlinedTextField(
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = White, unfocusedContainerColor = White
                ),
                label = { Text(text = textFieldLabel) },
                value = selectedText,
                onValueChange = {},
                enabled = enabled,
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(modifier = Modifier.background(White),
                expanded = expanded,
                onDismissRequest = { expanded = false }) {
                menuItemsTextAndClickHandler.forEach {
                    DropdownMenuItem(modifier = Modifier.background(White),
                        text = { Text(it.key) },
                        onClick = {
                            it.value()
                            expanded = false
                        })
                }
            }
        }
    }
}

@Composable
fun PasswordTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
    label: String = "Password",
    errorText: String = "",
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        label = {
            Text(text = label)
        },
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_password),
                contentDescription = "User icon"
            )
        },
        trailingIcon = {
            val image = if (passwordVisible) painterResource(id = R.drawable.ic_visibility_on)
            else painterResource(id = R.drawable.ic_visibility_off)

            val description = if (passwordVisible) "Hide password" else "Show password"

            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(painter = image, description)
            }
        },
        isError = isError,
        supportingText = {
            if (isError) Text(text = errorText)
        },
        colors = TextFieldDefaults.colors(
            errorContainerColor = Transparent,
            unfocusedContainerColor = Transparent,
            focusedContainerColor = Transparent,
            disabledTextColor = MaterialTheme.colorScheme.onBackground,
            disabledContainerColor = Transparent,
            disabledIndicatorColor = MaterialTheme.colorScheme.onBackground,
            disabledLabelColor = MaterialTheme.colorScheme.onBackground,
        ),
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation()
    )
}