package com.depromeet.team6.presentation.ui.coursesearch.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.depromeet.team6.ui.theme.defaultTeam6Colors

@Composable
fun TransportTabRow(
    tabs: List<String>,
    selectedTabIndex: Int,
    onTabClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    /**
     *  TabRowMinTab0dp 에서 사용하는 코드
     *  각 Tab에 포함된 텍스트의 길이만큼 동적으로 인디케이터 길이를 할당하기 위한 코드
     */
//    val density = LocalDensity.current
//    val tabWidths = remember {
//        val tabWidthStateList = mutableStateListOf<Dp>()
//        repeat(tabs.size) {
//            tabWidthStateList.add(0.dp)
//        }
//        tabWidthStateList
//    }
    TabRow(
        selectedTabIndex = selectedTabIndex,
        contentColor = defaultTeam6Colors.white,
        containerColor = defaultTeam6Colors.greyWashBackground,
        divider = {}, // 기본 밑줄 제거
        indicator = { tabPositions ->
            Box(
                modifier = Modifier
                    .transportTabIndicatorOffset(
                        currentTabPosition = tabPositions[selectedTabIndex],
                        tabWidth = tabPositions[selectedTabIndex].width
                    )
                    .height(2.dp) // Indicator 높이 조절
                    .background(color = defaultTeam6Colors.white)
            )
        }
    ) {
        tabs.forEachIndexed { tabIndex, tabName ->
            Tab(
                modifier = Modifier
                    .widthIn(min = 0.dp)
                    .height(40.dp)
                    .wrapContentWidth()
                    .padding(horizontal = 0.dp),
                selected = selectedTabIndex == tabIndex,
                onClick = { onTabClick(tabIndex) }
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(), // Tab 전체 크기
                    contentAlignment = Alignment.Center // 중앙 정렬
                ) {
                    Text(
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(vertical = 6.dp, horizontal = 14.dp),
                        text = tabName,
                        color = defaultTeam6Colors.white,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        onTextLayout = { textLayoutResult ->
                            /**
                             *  TabRowMinTab0dp 에서 사용하는 코드
                             *  각 Tab에 포함된 텍스트의 길이만큼 동적으로 인디케이터 길이를 할당하기 위한 코드
                             */
//                            tabWidths[tabIndex] =
//                                with(density) {
//                                    val textWidth = textLayoutResult.size.width.toDp()
//                                    val padding = 14.dp
//                                    textWidth + padding
//                                }
                        }
                    )
                }
            }
        }
    }
}

fun Modifier.transportTabIndicatorOffset(
    currentTabPosition: TabPosition,
    tabWidth: Dp
): Modifier = composed(
    inspectorInfo = debugInspectorInfo {
        name = "transportTabIndicatorOffset"
        value = currentTabPosition
    }
) {
    val currentTabWidth by animateDpAsState(
        targetValue = tabWidth,
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
    )
    val indicatorOffset by animateDpAsState(
        // 1dp -> 인디케이터가 정중앙에 오도록 보정
//        targetValue = ((currentTabPosition.left + currentTabPosition.right - tabWidth) / 2) + 1.dp,
//        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
        targetValue = currentTabPosition.left,
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
    )
    fillMaxWidth()
        .wrapContentSize(Alignment.BottomStart)
        .offset(x = indicatorOffset)
        .width(currentTabWidth)
}

@Preview
@Composable
fun TransportTabRowPreview() {
    val tabs = listOf("전체", "버스", "지하철")
    var selectedTabIndex by remember { mutableStateOf(0) }

    TransportTabRow(
        tabs = tabs,
        selectedTabIndex = selectedTabIndex,
        onTabClick = { tabIndex ->
            selectedTabIndex = tabIndex
        }
    )
}
