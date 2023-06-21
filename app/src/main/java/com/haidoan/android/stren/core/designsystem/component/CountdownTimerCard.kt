package com.haidoan.android.stren.core.designsystem.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.haidoan.android.stren.R
import com.haidoan.android.stren.core.designsystem.theme.Gray90

@Composable
fun CountdownTimerCard(
    durationDecrementAmountInSeconds: Long,
    durationIncrementAmountInSeconds: Long,
    totalDurationInSeconds: Long,
    remainingDurationInSeconds: Long,
    onDecrementDurationClick: () -> Unit,
    onIncrementDurationClick: () -> Unit,
    onSkipClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = (1.5).dp, color = Gray90, shape = RoundedCornerShape(15.dp))
            .clip(RoundedCornerShape(15.dp))
            .padding(dimensionResource(id = R.dimen.padding_medium)),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = dimensionResource(id = R.dimen.padding_small)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        onDecrementDurationClick()
                    },
                textAlign = TextAlign.Center,
                text = "-$durationDecrementAmountInSeconds sec",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                text = remainingDurationInSeconds.toDurationString() +
                        "/" + totalDurationInSeconds.toDurationString(),
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        onIncrementDurationClick()
                    },
                textAlign = TextAlign.Center,
                text = "+$durationIncrementAmountInSeconds sec",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }

        Spacer(Modifier.size(4.dp))
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp),
            strokeCap = StrokeCap.Round,
            progress = remainingDurationInSeconds.toFloat() / totalDurationInSeconds,
            color = MaterialTheme.colorScheme.primary,
            trackColor = Gray90
        )

        Spacer(Modifier.size(4.dp))
        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            contentPadding = PaddingValues(
                vertical = dimensionResource(id = R.dimen.padding_small),
                horizontal = 0.dp
            ),
            onClick = onSkipClick
        ) {
            Text("Skip", color = Color.White)
        }
    }
}

private fun Long.toDurationString() =
    if (this % 60 > 10) {
        "${this / 60}:${this % 60}"
    } else {
        "${this / 60}:0${this % 60}"
    }
