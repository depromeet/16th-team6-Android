import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.depromeet.team6.R
import com.depromeet.team6.domain.model.RouteLocation
import com.depromeet.team6.presentation.ui.lock.LockContract
import com.depromeet.team6.presentation.ui.lock.LockViewModel
import com.depromeet.team6.ui.theme.LocalTeam6Colors
import com.depromeet.team6.ui.theme.LocalTeam6Typography
import com.depromeet.team6.ui.theme.Team6Theme
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import kotlinx.coroutines.delay
import retrofit2.http.Tag
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun LockRoute(
    padding: PaddingValues,
    viewModel: LockViewModel,
    onTimerFinish: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadTaxiCost()
    }


    LockScreen(
        uiState = uiState,
        padding = padding,
        onTimerFinish = onTimerFinish,
        onDepartureClick = { viewModel.setEvent(LockContract.LockEvent.OnDepartureClick) },
        onLateClick = { viewModel.setEvent(LockContract.LockEvent.OnLateClick) }
    )
}

@Composable
fun LockScreen(
    padding: PaddingValues,
    uiState: LockContract.LockUiState = LockContract.LockUiState(),
    onTimerFinish: () -> Unit,
    onDepartureClick: () -> Unit,
    onLateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalTeam6Colors.current
    val typography = LocalTeam6Typography.current

    var timeLeft by remember { mutableIntStateOf(60) }

    var currentTime by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("a hh:mm", Locale.KOREAN)
            )
            delay(1000L)
        }
    }

    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000L)
            timeLeft--
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = defaultTeam6Colors.greyWashBackground),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = modifier.padding(vertical = 104.dp))

        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_lock_character),
            contentDescription = null,
            tint = Color.Unspecified
        )

        Spacer(modifier = modifier.padding(vertical = 12.dp))

        Text(
            text = stringResource(R.string.lock_screen_taxi_text),
            color = colors.white,
            style = typography.heading3SemiBold22,
            modifier = Modifier.padding(vertical = 16.dp),
            textAlign = TextAlign.Center
        )

        Text(
            text = "-"+uiState.taxiCost.toString(),
            color = colors.systemRed,
            style = typography.heading1ExtraBold56,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                onTimerFinish()
                onDepartureClick() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.main
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = stringResource(R.string.lock_screen_start_btn),
                color = colors.black,
                style = typography.heading5Bold17,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Button(
            onClick = {
                onTimerFinish()
                onLateClick()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .padding(bottom = 20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.greenButtonOpacity
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = stringResource(R.string.lock_screen_late_btn),
                color = colors.main,
                style = typography.bodyMedium17,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun LockScreenPreview() {
    Team6Theme {
        LockScreen(
            padding = PaddingValues(0.dp),
            onTimerFinish = {},
            uiState = TODO(),
            onDepartureClick = TODO(),
            onLateClick = TODO(),
            modifier = TODO()
        )
    }
}
