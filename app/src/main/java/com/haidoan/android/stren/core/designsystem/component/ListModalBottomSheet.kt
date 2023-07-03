package com.haidoan.android.stren.core.designsystem.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.haidoan.android.stren.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListModalBottomSheet(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    bottomSheetState: SheetState = rememberModalBottomSheetState(),
    title: String,
    sheetItems: List<BottomSheetItem>
) {
    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        tonalElevation = 0.dp,
        containerColor = Color.White,
        sheetState = bottomSheetState,
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = dimensionResource(id = R.dimen.padding_medium),
                    bottom = dimensionResource(id = R.dimen.padding_medium)
                )
                .align(Alignment.CenterHorizontally),
            text = title,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Start
        )

        LazyColumn {
            items(sheetItems) {
                Row(
                    modifier = Modifier
                        .clickable { it.onClickHandler() }
                        .fillMaxWidth()
                        .padding(dimensionResource(id = R.dimen.padding_medium)),
                    verticalAlignment = CenterVertically
                ) {
                    Icon(
                        painter = painterResource(
                            id = it.imageResId ?: R.drawable.ic_app_logo_no_padding
                        ), contentDescription = "Icon"
                    )
                    Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.padding_small)))
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = it.text,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

data class BottomSheetItem(
    val imageResId: Int? = null,
    val text: String,
    val onClickHandler: () -> Unit
)