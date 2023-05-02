package com.haidoan.android.stren.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.haidoan.android.stren.core.designsystem.theme.Red40
import com.haidoan.android.stren.core.designsystem.theme.Red50

@Composable
fun StrenTextButton(
    modifier: Modifier = Modifier,
    text: String,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
    onClickHandler: () -> Unit
) {
    TextButton(modifier = modifier, onClick = onClickHandler, shape = RectangleShape) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.primary,
            style = textStyle
        )
    }

}

@Composable
fun StrenFilledButton(
    modifier: Modifier = Modifier,
    text: String,
    onClickHandler: () -> Unit,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium
) {
    Button(
        modifier = modifier,
        shape = RoundedCornerShape(15),
        onClick = onClickHandler,
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
                text = text,
                color = Color.White,
                style = textStyle
            )
        }
    }
}

