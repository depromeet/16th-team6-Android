package com.depromeet.team6.presentation.ui.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.depromeet.team6.ui.theme.LocalTeam6Colors
import com.depromeet.team6.ui.theme.LocalTeam6Typography

class SpeechBubbleShape(
    private val cornerRadius: Dp = 16.dp,
    private val tailExist: Boolean
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(
            Path().apply {
                val radius = with(density) { cornerRadius.toPx() }
                val tailHeight = size.height * 0.2f
                val tailWidth = size.width * 0.06f
                val tailCenterX = size.width * 0.11f

                reset()
                moveTo(radius, 0f)

                arcTo(
                    Rect(
                        Offset(size.width - radius, 0f),
                        Offset(size.width, radius)
                    ),
                    -90f,
                    90f,
                    false
                )

                arcTo(
                    Rect(
                        Offset(size.width - radius, size.height - radius - tailHeight),
                        Offset(size.width, size.height - tailHeight)
                    ),
                    0f,
                    90f,
                    false
                )

                if (tailExist) {
                    lineTo(tailCenterX + tailWidth / 2.2f, size.height - tailHeight)

                    val tailTipY = size.height + (tailHeight * 0.45f) // 높이는 유지
                    val controlX = tailCenterX                      // 중간 제어점은 중앙
                    val tipEndX = tailCenterX - tailWidth / 2.3f    // 좌우 폭을 좁힘 (더 날카롭게)

                    // 꼭짓점 둥글게
                    quadraticTo(
                        controlX, tailTipY,
                        tipEndX, size.height - tailHeight
                    )

                }

                arcTo(
                    Rect(
                        Offset(0f, size.height - radius - tailHeight),
                        Offset(radius, size.height - tailHeight)
                    ),
                    90f,
                    90f,
                    false
                )

                arcTo(
                    Rect(
                        Offset(0f, 0f),
                        Offset(radius, radius)
                    ),
                    180f,
                    90f,
                    false
                )

                close()
            }
        )
    }
}

@Composable
fun SpeechBubble(
    prefix: String,
    modifier: Modifier = Modifier,
    emphasisText: String? = null,
    suffix: String? = null,
    tailExist: Boolean
) {
    val colors = LocalTeam6Colors.current
    val typography = LocalTeam6Typography.current

    Box(
        modifier = modifier
            .background(
                color = colors.greyElevatedBackground,
                shape = SpeechBubbleShape(tailExist = tailExist)
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .padding(top = 10.dp, bottom = 16.dp, start = 12.dp, end = 12.dp)
        ) {
            Text(
                text = prefix,
                color = colors.greyOneLabel,
                style = typography.bodyMedium12
            )

            if (emphasisText != null) {
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = emphasisText,
                    color = colors.white,
                    style = typography.bodySemiBold12
                )
            }

            if (suffix != null) {
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = suffix,
                    color = colors.greyOneLabel,
                    style = typography.bodyMedium12
                )
            }
        }
    }
}

@Preview
@Composable
fun SpeechBubblePreview() {
    SpeechBubble(
        prefix = "여기서 놓치면 택시비",
        emphasisText = "34,000원",
        modifier = Modifier,
        tailExist = true
    )
}
