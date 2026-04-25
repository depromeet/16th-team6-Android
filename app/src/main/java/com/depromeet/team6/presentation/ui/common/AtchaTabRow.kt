package com.depromeet.team6.presentation.ui.common

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography

@Composable
fun AtchaTabRow(
    tabs: List<String>,
    selectedTabIndex: Int,
    onTabClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    TabRow(
        selectedTabIndex = selectedTabIndex,
        contentColor = defaultTeam6Colors.white,
        containerColor = defaultTeam6Colors.gray950,
        divider = {}, // 기본 밑줄 제거
        indicator = { tabPositions ->
            Box(
                modifier = Modifier
                    .transportTabIndicatorOffset(
                        currentTabPosition = tabPositions[selectedTabIndex],
                        tabWidth = tabPositions[selectedTabIndex].width
                    )
                    .height(1.dp) // Indicator 높이 조절
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
                    val textColor = if (selectedTabIndex == tabIndex) {
                        defaultTeam6Colors.white
                    } else {
                        defaultTeam6Colors.gray400
                    }
                    val textStyle = if (selectedTabIndex == tabIndex) {
                        defaultTeam6Typography.body5_B5SB14
                    } else {
                        defaultTeam6Typography.body4_B4R15
                    }
                    Text(
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(vertical = 6.dp, horizontal = 14.dp),
                        text = tabName,
                        color = textColor,
                        style = textStyle,
                        textAlign = TextAlign.Center,
                        onTextLayout = { textLayoutResult ->
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AtchaTabRowV2(
    tabs: List<String>,
    selectedTabIndex: Int,
    onTabClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(defaultTeam6Colors.gray950)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 12.dp), // 전체 시작 여백
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEachIndexed { index, title ->
                val isSelected = selectedTabIndex == index
                val textColor = if (isSelected) {
                    defaultTeam6Colors.white
                } else {
                    defaultTeam6Colors.gray400
                }
                val textStyle = if (isSelected) {
                    defaultTeam6Typography.body5_B5SB14
                } else {
                    defaultTeam6Typography.body4_B4R15
                }

                Column(
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null // 클릭 시 물결 효과 제거 (깔끔하게)
                        ) { onTabClick(index) },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier
                            .wrapContentSize()
                            .drawBehind {
                                if (!isSelected) return@drawBehind
                                val strokeWidth = 1.dp.toPx()
                                val y = size.height - strokeWidth / 2
                                drawLine(
                                    color = defaultTeam6Colors.white, // 줄의 색상
                                    start = Offset(0f, y),
                                    end = Offset(size.width, y),
                                    strokeWidth = strokeWidth
                                )
                            }
                            .padding(vertical = 6.dp, horizontal = 14.dp),
                        text = title,
                        color = textColor,
                        style = textStyle,
                        textAlign = TextAlign.Center,
                        onTextLayout = { textLayoutResult ->
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
fun AtchaTabRowPreview() {
    val tabs = listOf("전체", "버스", "지하철")
    var selectedTabIndex by remember { mutableStateOf(0) }

    AtchaTabRow(
        tabs = tabs,
        selectedTabIndex = selectedTabIndex,
        onTabClick = { tabIndex ->
            selectedTabIndex = tabIndex
        }
    )
}

@Preview
@Composable
fun AtchaTabRowPreview2() {
    val tabs = listOf("전체", "버스", "지하철")
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Box() {
        AtchaTabRowV2(
            tabs = tabs,
            selectedTabIndex = selectedTabIndex,
            onTabClick = { tabIndex ->
                selectedTabIndex = tabIndex
            }
        )
    }
}
