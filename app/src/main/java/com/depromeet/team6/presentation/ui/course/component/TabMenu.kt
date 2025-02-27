package com.depromeet.team6.presentation.ui.course.component

import android.graphics.fonts.FontStyle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import kotlinx.coroutines.launch

@Composable
fun TabMenu(
    modifier: Modifier = Modifier
) {
    val tabItems = listOf("전체", "버스", "지하철")

    Column(
        modifier = modifier
            .background(defaultTeam6Colors.greyElevatedBackground),
        verticalArrangement = Arrangement.Center
    ) {
        val pagerState = rememberPagerState {
            tabItems.size
        }
        val coroutineScope = rememberCoroutineScope()

        // TabRow
        TransportTabRow(
            tabs = tabItems,
            selectedTabIndex = pagerState.currentPage,
            onTabClick = { tabIndex ->
                coroutineScope.launch {
                    pagerState.animateScrollToPage(tabIndex)
                }
            }
        )

        // Tab Content
        HorizontalPager(state = pagerState) { page ->
            when (page) {
                0 -> Text("This is Tab 1 content")
                1 -> Text("This is Tab 2 content")
                2 -> Text("This is Tab 3 content")
            }
        }
    }
}

@Preview
@Composable
fun PreviewTabMenu() {
    TabMenu()
}