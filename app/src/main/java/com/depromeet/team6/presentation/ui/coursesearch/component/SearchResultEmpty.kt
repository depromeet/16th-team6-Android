package com.depromeet.team6.presentation.ui.coursesearch.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.R
import com.depromeet.team6.presentation.ui.coursesearch.CourseSearchContract
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography

@Composable
fun SearchResultEmpty(
    modifier: Modifier = Modifier,
    dataLoadState: CourseSearchContract.CourseSearchDataState = CourseSearchContract.CourseSearchDataState.NoResult,
    isMidNight: Boolean = false
) {
    var pageMessage = when (dataLoadState) {
        CourseSearchContract.CourseSearchDataState.NoResult -> {
            stringResource(R.string.course_search_result_empty)
        }
        CourseSearchContract.CourseSearchDataState.ServiceEnded -> {
            stringResource(R.string.course_search_result_empty)
        }
        else -> ""
    }
    if (isMidNight) pageMessage = stringResource(R.string.course_search_result_midnight)
    Box(
        modifier = modifier
            .background(defaultTeam6Colors.gray950)
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center)
        ) {
            Image(
                modifier = Modifier
                    .size(64.dp)
                    .align(Alignment.CenterHorizontally),
                imageVector = ImageVector.vectorResource(R.drawable.ic_atcha_character_grey),
                contentDescription = "no result character"
            )
            Spacer(
                modifier = Modifier.height(16.dp)
            )
            Text(
                text = pageMessage,
                textAlign = TextAlign.Center,
                color = defaultTeam6Colors.gray400,
                style = defaultTeam6Typography.bodyRegular15
            )
        }
    }
}

@Preview
@Composable
fun SearchResultEmptyPreview() {
    SearchResultEmpty()
}
