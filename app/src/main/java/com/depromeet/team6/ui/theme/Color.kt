package com.depromeet.team6.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class Team6Colors(
    // Primary Colors
    val purple80: Color,
    val purpleGrey80: Color,
    val pink80: Color,

    val purple40: Color,
    val purpleGrey40: Color,
    val pink40: Color,

    // Semantic
    // Common
    val white: Color,
    val black: Color,
    val main: Color,

    // Text
    val greySecondaryLabel: Color,
    val greyTertiaryLabel: Color,
    val greyQuaternaryLabel: Color,
    val greyLink: Color,
    val disabledLabel: Color,

    // Background
    val greyElevatedBackground: Color,
    val greyWashBackground: Color,
    val greyDivider: Color,

    // Card
    val greyCard: Color,
    val greyElevatedCard: Color,

    // Button
    val greyButton: Color,
    val greyButtonOutline: Color,
    val kakaoLoginButton: Color,
    val disabledButton: Color,
    val defaultButton: Color,

    // Non-Semantic
    val systemGreen: Color,
    val systemRed: Color,
    val systemGray6: Color,

    // TextField Cursor
    val textFieldCursor: Color
)

val defaultTeam6Colors = Team6Colors(
    // Primary Colors
    purple80 = Color(0xFFD0BCFF),
    purpleGrey80 = Color(0xFFCCC2DC),
    pink80 = Color(0xFFEFB8C8),

    purple40 = Color(0xFF6650a4),
    purpleGrey40 = Color(0xFF625b71),
    pink40 = Color(0xFF7D5260),

    // Semantic
    // Common
    white = Color(0xFFFFFFFF),
    black = Color(0xFF000000),
    main = Color(0xFF99F977),

    // Text
    greySecondaryLabel = Color(0xFF999CA4),
    greyTertiaryLabel = Color(0xFF666970),
    greyQuaternaryLabel = Color(0xFF393C42),
    greyLink = Color(0xFF4C4D53),
    disabledLabel = Color(0xFF393C42),

    // Background
    greyElevatedBackground = Color(0xFF1C1C1D),
    greyWashBackground = Color(0xFF18181A),
    greyDivider = Color(0xFF4D4D4D),

    // Card
    greyCard = Color(0xFF1C1C1E),
    greyElevatedCard = Color(0xFF2C2C2E),

    // Button
    greyButton = Color(0xFF29292C),
    greyButtonOutline = Color(0xFF36363A),
    kakaoLoginButton = Color(0xFFFAE100),
    disabledButton = Color(0x662C2C30),
    defaultButton = Color(0xFF2C2C30),

    // Non-Semantic
    systemGreen = Color(0xFF99ED7B),
    systemRed = Color(0xFFFF5D5D),
    systemGray6 = Color(0xFF1C1C1E),

    // TextField Cursor
    textFieldCursor = Color(0xFF90E772)
)

val LocalTeam6Colors = staticCompositionLocalOf { defaultTeam6Colors }
