package com.haidoan.android.stren.core.designsystem.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SimpleConfirmationDialog(
    onDismissDialog: () -> Unit,
    title: String,
    body: String,
    onConfirmClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissDialog,
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmClick()
                    onDismissDialog()
                }
            ) {
                Text(text = "Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissDialog
            ) {
                Text(text = "Dismiss")
            }
        },
        title = {
            Text(text = title)
        },
        text = {
            Text(text = body)
        },
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        containerColor = Color.White
    )
}