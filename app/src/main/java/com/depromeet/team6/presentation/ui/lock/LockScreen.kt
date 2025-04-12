import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.depromeet.team6.R
import com.depromeet.team6.presentation.ui.lock.LockContract
import com.depromeet.team6.presentation.ui.lock.LockViewModel
import com.depromeet.team6.ui.theme.LocalTeam6Colors
import com.depromeet.team6.ui.theme.LocalTeam6Typography
import com.depromeet.team6.ui.theme.Team6Theme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun LockRoute(
    padding: PaddingValues,
    viewModel: LockViewModel,
    onDepartureClick: () -> Unit,
    onLateClick: () -> Unit,
    onTimerFinish: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val systemUiController = rememberSystemUiController()

    LaunchedEffect(Unit) {
        viewModel.loadTaxiCost()
    }

    SideEffect {
        systemUiController.setStatusBarColor(
            color = Color.Black
        )

        systemUiController.setNavigationBarColor(
            color = Color.Black
        )
    }

    LockScreen(
        uiState = uiState,
        padding = padding,
        onTimerFinish = onTimerFinish,
        onDepartureClick = { onDepartureClick() },
        onLateClick = { onLateClick() }
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

    val formattedCost = NumberFormat.getNumberInstance(Locale.US).format(uiState.taxiCost)

    // Lottie 애니메이션 설정
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.lock_background)
    )

    // 애니메이션 상태 제어
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

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

    Box(
        modifier = Modifier.fillMaxSize()
            .paint(
                painter = BitmapPainter(ImageBitmap.imageResource(R.drawable.img_login_background)),
                contentScale = ContentScale.Crop
            )
            .padding(padding)
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.fillMaxSize()
        )

        // 그라데이션 배경
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            colors.black,
                            colors.black.copy(alpha = 0.0f)
                        ),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.padding(vertical = 60.dp))

            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_lock_character),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(36.dp)
            )

            Spacer(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                text = stringResource(R.string.lock_screen_taxi_text),
                color = colors.white,
                style = typography.heading3SemiBold22,
                modifier = Modifier.padding(vertical = 10.dp),
                textAlign = TextAlign.Center
            )

            Text(
                text = "-$formattedCost",
                color = colors.systemRed,
                style = typography.heading1ExtraBold56,
                modifier = Modifier.padding(vertical = 6.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    onTimerFinish()
                    onDepartureClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.main
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = stringResource(R.string.lock_screen_start_btn),
                    color = colors.black,
                    style = typography.heading5Bold17,
                    modifier = Modifier.padding(vertical = 14.dp)
                )
            }

            Button(
                onClick = {
                    onTimerFinish()
                    onLateClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 32.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.greenLockButton
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = stringResource(R.string.lock_screen_late_btn),
                    color = colors.main,
                    style = typography.bodyMedium17,
                    modifier = Modifier.padding(vertical = 14.dp)
                )
            }
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
