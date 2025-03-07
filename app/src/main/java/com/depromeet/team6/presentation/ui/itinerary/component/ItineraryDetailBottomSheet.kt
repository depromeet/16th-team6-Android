package com.depromeet.team6.presentation.ui.itinerary.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItineraryDetailBottomSheet(
    modifier : Modifier = Modifier
) {
    val sheetState = rememberStandardBottomSheetState(initialValue = SheetValue.PartiallyExpanded)
    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = sheetState)
    val coroutineScope = rememberCoroutineScope()

    BottomSheetScaffold(
        modifier = modifier
            .fillMaxSize(),
        scaffoldState = scaffoldState,
        sheetContent = {
            // BottomSheetContent()
            Text(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                text = "Itinerary Sub Content")
        },
        sheetPeekHeight = 300.dp, // 필요하면 기본 노출 높이 조정 가능
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text("Itinerary Main Content")
        }
    }
}

@Preview
@Composable
fun ItineraryDetailBottomSheetPreview() {

    ItineraryDetailBottomSheet()
}