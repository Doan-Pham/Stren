package com.haidoan.android.stren.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun DropDownMenuScaffold(
    menuItemsTextAndClickHandler: Map<String, () -> Unit>,
    content: @Composable (onExpandMenu: () -> Unit) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .wrapContentSize(Alignment.TopStart)
    ) {
        content { expanded = true }
        DropdownMenu(
            modifier = Modifier.background(Color.White),
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            menuItemsTextAndClickHandler.forEach {
                DropdownMenuItem(
                    modifier = Modifier.background(Color.White),
                    text = { Text(it.key) },
                    onClick = {
                        it.value()
                        expanded = false
                    }
                )
            }
        }
    }

}