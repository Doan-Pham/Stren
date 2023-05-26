package com.haidoan.android.stren.core.designsystem.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.haidoan.android.stren.R
import com.haidoan.android.stren.core.designsystem.theme.Gray60

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StrenHorizontalPager(
    modifier: Modifier,
    pagerState: PagerState = rememberPagerState(),
    contents: List<@Composable () -> Unit>
) {
    val pageCount = contents.size
    Column {
        HorizontalPager(
            pageCount = pageCount,
            state = pagerState,
            verticalAlignment = Alignment.Top,
            modifier = modifier
        ) { page ->
            // Our page content
            contents[page]()
        }
        Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.padding_small)))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pageCount) { iteration ->
                val color =
                    if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary
                    else Gray60
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(
                            dimensionResource(
                                id = R.dimen.icon_size_extra_small
                            )
                        )

                )
            }
        }
    }

}