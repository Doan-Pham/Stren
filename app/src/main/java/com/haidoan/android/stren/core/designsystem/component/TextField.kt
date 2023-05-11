package com.haidoan.android.stren.core.designsystem.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.haidoan.android.stren.R

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
