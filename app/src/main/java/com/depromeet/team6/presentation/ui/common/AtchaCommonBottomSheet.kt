package com.depromeet.team6.presentation.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.depromeet.team6.presentation.util.Dimens
import com.depromeet.team6.ui.theme.defaultTeam6Colors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AtchaCommonBottomSheet(
    mainContent: @Composable () -> Unit, // 뒷 배경에 표시될 화면
    sheetContent: @Composable () -> Unit, // BottomSheet에 표시될 화면
    modifier: Modifier = Modifier
) {
    val sheetState = rememberStandardBottomSheetState(initialValue = SheetValue.PartiallyExpanded)
    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = sheetState)
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier
    ) {
        BottomSheetScaffold(
            modifier = Modifier
                .fillMaxHeight()
            ,
            scaffoldState = scaffoldState,
            sheetContent = {
                sheetContent()
            },
            sheetPeekHeight = 300.dp, // 필요하면 기본 노출 높이 조정 가능
            sheetShape = RoundedCornerShape(
                topStart = Dimens.BottomSheetRoundCornerRadius,
                topEnd = Dimens.BottomSheetRoundCornerRadius
            ),
            sheetContainerColor = defaultTeam6Colors.greyWashBackground,
            sheetDragHandle = {
                DragHandle()
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = paddingValues.calculateTopPadding(),
                        start = paddingValues.calculateStartPadding(LocalLayoutDirection.current),
                        end = paddingValues.calculateEndPadding(LocalLayoutDirection.current),
                        bottom = paddingValues.calculateBottomPadding() - Dimens.BottomSheetRoundCornerRadius // 40dp만큼 겹치게 함                    )
                    )
            ) {
                mainContent()
            }
        }
    }
}

@Composable
fun DragHandle(
    modifier: Modifier = Modifier,
    width: Dp = 40.dp,
    height: Dp = 4.dp,
    shape: Shape = MaterialTheme.shapes.extraLarge,
    color: Color = defaultTeam6Colors.greyQuaternaryLabel
) {
    Surface(
        modifier = modifier
            .padding(top = 14.dp, bottom = 4.dp),
        color = color,
        shape = shape
    ) {
        Box(
            Modifier
                .size(
                    width = width,
                    height = height
                )
        )
    }
}

@Preview
@Composable
fun AtchaCommonBottomSheetPreview() {
    AtchaCommonBottomSheet(
        mainContent = {
            Box(
                modifier = Modifier
                    .background(defaultTeam6Colors.systemRed)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = "여기에 지도같은거 띄워주쎄용~~",
                    textAlign = TextAlign.Center
                )
            }
        },
        sheetContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = "여기에 표시될 정보도 입력해주쎄용~~~",
                    color = defaultTeam6Colors.white,
                    textAlign = TextAlign.Center
                )
            }
        }
    )
}
