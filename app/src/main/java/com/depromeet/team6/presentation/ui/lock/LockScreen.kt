import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.depromeet.team6.R
import com.depromeet.team6.ui.theme.Team6Theme
import com.depromeet.team6.ui.theme.Team6Theme.colors
import com.depromeet.team6.ui.theme.Team6Theme.typography
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun LockScreen(onTimerFinish: () -> Unit) {
    var timeLeft by remember { mutableIntStateOf(60) }
    val progress by remember(timeLeft) {
        mutableFloatStateOf(timeLeft / 60f)
    }

    val progressColor = if (timeLeft <= 10) colors.systemRed else colors.systemGreen

    val timerTextColor = if (timeLeft <= 10) colors.systemRed else colors.systemGreen

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.black)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.lock_screen_start_alarm_text),
                color = colors.white,
                style = typography.heading5SemiBold17,
                modifier = Modifier.padding(top = 22.dp)
            )

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.size(270.dp),
                    color = Color.DarkGray,
                    strokeWidth = 8.dp
                )

                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.size(270.dp),
                    color = progressColor,
                    strokeWidth = 8.dp
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = String.format("%02d:%02d", timeLeft / 60, timeLeft % 60),
                        color = timerTextColor,
                        fontSize = 60.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_all_alarm_bell_white),
                            contentDescription = null,
                            tint = colors.white,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = currentTime,
                            color = colors.greySecondaryLabel,
                            style = typography.heading5SemiBold17
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.padding(bottom = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = defaultTeam6Colors.greyElevatedBackground
                ) {
                    Text(
                        text = buildAnnotatedString {
                            append("지금 출발하면 택시비 ")
                            withStyle(
                                style = SpanStyle(
                                    color = defaultTeam6Colors.systemGreen
                                )
                            ) {
                                append("34,000원")
                            }
                            append(" 세이브!")
                        },
                        color = defaultTeam6Colors.white,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(13.dp, 11.dp),
                        style = typography.bodyMedium13
                    )
                }

                Button(
                    onClick = onTimerFinish,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
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
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LockScreenPreview() {
    Team6Theme {
        LockScreen {
        }
    }
}
