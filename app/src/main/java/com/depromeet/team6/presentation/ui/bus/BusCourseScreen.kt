package com.depromeet.team6.presentation.ui.bus

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.depromeet.team6.R
import com.depromeet.team6.domain.model.course.TransportType
import com.depromeet.team6.presentation.model.bus.BusArrivalParameter
import com.depromeet.team6.presentation.ui.common.TransportVectorIconComposable
import com.depromeet.team6.presentation.ui.common.view.AtChaLoadingView
import com.depromeet.team6.presentation.util.modifier.noRippleClickable
import com.depromeet.team6.presentation.util.view.LoadState
import com.depromeet.team6.presentation.util.view.TransportTypeUiMapper
import com.depromeet.team6.ui.theme.LocalTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography

@Composable
fun BusCourseRoute(
    padding: PaddingValues,
    viewModel: BusCourseViewModel = hiltViewModel(),
    navigateToBackStack: () -> Unit,
    busArrivalParameter: BusArrivalParameter
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(viewModel.sideEffect, lifecycleOwner) {
        viewModel.sideEffect.flowWithLifecycle(lifecycle = lifecycleOwner.lifecycle)
            .collect { sideEffect ->
                when (sideEffect) {
                    is BusCourseContract.BusCourseSideEffect.NavigateToBackStack -> navigateToBackStack()
                }
            }
    }

    LaunchedEffect(Unit) {
        viewModel.initUiState(busArrivalParameter)
        viewModel.getBusArrival(
            routeName = busArrivalParameter.routeName,
            stationName = busArrivalParameter.stationName,
            lat = busArrivalParameter.lat,
            lon = busArrivalParameter.lon
        )
    }

    when (uiState.loadState) {
        LoadState.Loading -> AtChaLoadingView()
        LoadState.Success -> {
            BusCourseScreen(
                padding = padding,
                modifier = Modifier.fillMaxSize(),
                backButtonClicked = { viewModel.setSideEffect(BusCourseContract.BusCourseSideEffect.NavigateToBackStack) }
            )
        }

        else -> Unit
    }
}

@Composable
fun BusCourseScreen(
    padding: PaddingValues,
    modifier: Modifier = Modifier,
    uiState: BusCourseContract.BusCourseUiState = BusCourseContract.BusCourseUiState(),
    backButtonClicked: () -> Unit = {}
) {
    val busNumber = 350
    val busIconTint = defaultTeam6Colors.busColors[0].second
    val runningBusCount = 2

    val grey = defaultTeam6Colors.greySecondaryLabel
    val white = defaultTeam6Colors.white

    val fullText = stringResource(R.string.bus_course_running_bus_count, runningBusCount)
    val numberStart = fullText.indexOf(runningBusCount.toString())
    val numberEnd = numberStart + runningBusCount.toString().length

    val color = LocalTeam6Colors.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = color.greyElevatedBackground)
            .padding(padding)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_all_arrow_left_grey),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .padding(vertical = 18.dp, horizontal = 16.dp)
                    .noRippleClickable { backButtonClicked() }
                    .align(Alignment.CenterStart)
            )
            Row(
                modifier = Modifier.align(Alignment.Center),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                TransportVectorIconComposable(
                    type = TransportType.BUS,
                    color = TransportTypeUiMapper.getColor(TransportType.BUS, uiState.subtypeIdx),
                    isMarker = false,
                    modifier = Modifier
                        .size(18.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = busNumber.toString(),
                    style = defaultTeam6Typography.heading5SemiBold17,
                    color = defaultTeam6Colors.white
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.bus_course_info),
                style = defaultTeam6Typography.bodyRegular14,
                color = defaultTeam6Colors.white
            )
            Spacer(modifier = Modifier.width(3.dp))
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_bus_course_info_12),
                contentDescription = null,
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = buildAnnotatedString {
                    append(fullText.substring(0, numberStart))
                    withStyle(style = SpanStyle(color = white)) {
                        append(fullText.substring(numberStart, numberEnd))
                    }
                    append(fullText.substring(numberEnd))
                },
                style = defaultTeam6Typography.bodyRegular14.copy(color = grey)
            )
        }
    }
    LazyColumn(modifier = Modifier.fillMaxWidth().background(color = color.greyWashBackground)) {
    }
}

@Preview
@Composable
private fun BusCourseScreenPreview() {
    BusCourseScreen(padding = PaddingValues(0.dp))
}
