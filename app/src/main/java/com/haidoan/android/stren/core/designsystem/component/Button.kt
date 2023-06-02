package com.haidoan.android.stren.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.haidoan.android.stren.R
import com.haidoan.android.stren.core.designsystem.theme.Gray60
import com.haidoan.android.stren.core.designsystem.theme.Red40
import com.haidoan.android.stren.core.designsystem.theme.Red50

@Composable
fun StrenTextButton(
    modifier: Modifier = Modifier,
    text: String,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
    enabled: Boolean = true,
    onClickHandler: () -> Unit
) {
    TextButton(
        modifier = modifier,
        onClick = onClickHandler,
        shape = RectangleShape,
        enabled = enabled
    ) {
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
    enabled: Boolean = true,
    onClickHandler: () -> Unit,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,

    ) {
    val backgroundBrush = if (enabled) {
        Brush.horizontalGradient(colors = listOf(Red40, Red50))
    } else {
        Brush.linearGradient(colors = listOf(Gray60, Color.Gray))
    }
    Button(
        modifier = modifier,
        shape = RoundedCornerShape(15),
        onClick = onClickHandler,
        contentPadding = PaddingValues(),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        enabled = enabled
    ) {
        Box(
            modifier = Modifier
                .background(backgroundBrush)
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

@Composable
fun StrenOutlinedButton(
    modifier: Modifier = Modifier,
    text: String,
    leadingIconResId: Int?,
    color: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit,
    textStyle: TextStyle = MaterialTheme.typography.bodySmall
) {
    OutlinedButton(
        modifier = modifier,
        shape = RoundedCornerShape(40),
        onClick = onClick,
        border = BorderStroke(width = (1.5).dp, color = color),
        contentPadding = PaddingValues(horizontal = dimensionResource(id = R.dimen.padding_small))
    ) {
        if (leadingIconResId != null) {
            Icon(
                painter = painterResource(id = leadingIconResId),
                contentDescription = "",
                tint = color
            )
            Spacer(modifier = Modifier.size(4.dp))
        }
        Text(text = text, style = textStyle, color = color)
    }
}
