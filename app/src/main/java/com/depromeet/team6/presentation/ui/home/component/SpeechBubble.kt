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

class SpeechBubbleShape(private val cornerRadius: Dp = 16.dp) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(
            Path().apply {
                val radius = with(density) { cornerRadius.toPx() }
                val tailHeight = size.height * 0.2f

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

                lineTo(size.width * 0.12f, size.height - tailHeight)
                lineTo(size.width * 0.15f, size.height)
                lineTo(size.width * 0.18f, size.height - tailHeight)

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
    text: String,
    modifier: Modifier = Modifier,
    taxiCost: String? = null
) {
    val colors = LocalTeam6Colors.current
    val typography = LocalTeam6Typography.current

    Box(
        modifier = modifier
            .background(
                color = colors.greyElevatedBackground,
                shape = SpeechBubbleShape()
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .padding(top = 10.dp, bottom = 16.dp, start = 10.dp, end = 10.dp)
        ) {
            Text(
                text = text,
                color = colors.greySecondaryLabel,
                style = typography.bodyMedium12
            )

            Spacer(modifier = Modifier.width(2.dp))

            if (taxiCost != null) {
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = taxiCost,
                    color = colors.white,
                    style = typography.bodySemiBold12
                )
            }
        }
    }
}

@Preview
@Composable
fun SpeechBubblePreview() {
    SpeechBubble(
        text = "여기서 놓치면 택시비",
        taxiCost = "34,000원",
        modifier = Modifier
    )
}
