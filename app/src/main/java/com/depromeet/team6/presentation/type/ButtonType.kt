package com.depromeet.team6.presentation.type

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.depromeet.team6.ui.theme.defaultTeam6Colors
import com.depromeet.team6.ui.theme.defaultTeam6Typography

enum class ButtonType(
    val backGroundColor: Color,
    val backgroundAlpha: Float = 1f,
    val textColor: Color,
    val stroke: Dp,
    val strokeColor: Color?
) {
    PRIMARY(
        backGroundColor = defaultTeam6Colors.primaryMain,
        textColor = defaultTeam6Colors.black,
        stroke = 0.dp,
        strokeColor = null
    ),
    WHITE(
        backGroundColor = defaultTeam6Colors.white,
        textColor = defaultTeam6Colors.black,
        stroke = 0.dp,
        strokeColor = null
    ),
    GRAY(
        backGroundColor = defaultTeam6Colors.gray910,
        textColor = defaultTeam6Colors.white,
        stroke = 0.dp,
        strokeColor = null
    ),
    TONAL(
        backGroundColor = defaultTeam6Colors.primaryMain,
        backgroundAlpha = 0.12f,
        textColor = defaultTeam6Colors.primaryMain,
        stroke = 0.dp,
        strokeColor = null
    ),
    DISABLED(
        backGroundColor = Color(0xFF2C2C30),
        backgroundAlpha = 0.4f,
        textColor = defaultTeam6Colors.gray700,
        stroke = 0.dp,
        strokeColor = null
    ),
    OUTLINE(
        backGroundColor = defaultTeam6Colors.white,
        textColor = defaultTeam6Colors.white,
        stroke = 1.dp,
        strokeColor = defaultTeam6Colors.gray910,
        backgroundAlpha = 0f
    ),
    OUTLINE_DISABLED(
        backGroundColor = defaultTeam6Colors.white,
        textColor = defaultTeam6Colors.gray700,
        stroke = 1.dp,
        strokeColor = defaultTeam6Colors.gray800,
        backgroundAlpha = 0f

    )
}

enum class ButtonSize(
    val verticalPadding: Dp,
    val roundPadding: Dp,
    val textStyle: TextStyle
) {
    LARGE(
        verticalPadding = 16.dp,
        roundPadding = 10.dp,
        textStyle = defaultTeam6Typography.heading3_H3SB17
    ),
    MEDIUM(
        verticalPadding = 14.dp,
        roundPadding = 10.dp,
        textStyle = defaultTeam6Typography.body2_B2SB15

    ),
    SMALL(
        verticalPadding = 13.dp,
        roundPadding = 10.dp,
        textStyle = defaultTeam6Typography.body5_B5SB14

    ),
    EXTRA_SMALL(
        verticalPadding = 8.dp,
        roundPadding = 8.dp,
        textStyle = defaultTeam6Typography.body5_B5SB14
    );
}
