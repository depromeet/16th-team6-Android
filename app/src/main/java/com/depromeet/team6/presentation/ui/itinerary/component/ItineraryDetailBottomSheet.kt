package com.depromeet.team6.presentation.ui.itinerary.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
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
import com.depromeet.team6.presentation.ui.home.component.TMapViewCompose
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.google.android.gms.maps.model.LatLng

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
            .fillMaxHeight()
            .background(defaultTeam6Colors.black),
        scaffoldState = scaffoldState,
        sheetContent = {
            // BottomSheetContent()
            Text(
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.CenterHorizontally),
                text = "Itinerary Sub Content")
        },
        sheetPeekHeight = 300.dp, // 필요하면 기본 노출 높이 조정 가능
    ) { paddingValues ->
        TMapViewCompose(
            modifier = Modifier.padding(paddingValues),
            currentLocation = LatLng(37.5665, 126.9780)
        )
    }
}

@Preview
@Composable
fun ItineraryDetailBottomSheetPreview() {

    ItineraryDetailBottomSheet()
}