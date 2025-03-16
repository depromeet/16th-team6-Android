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

    // TODO : 디자인 시스템에 '임시' 부분 -> 디자이너님께 여쭤본 후 수정해야함
    val primaryRed: Color,
    val primaryGreen: Color,
    val primaryMain: Color,

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
    val greyDisabled: Color,

    // Background
    val greyElevatedBackground: Color,
    val greyWashBackground: Color,
    val greyDivider: Color,

    // Card
    val greyCard: Color,
    val greyElevatedCard: Color,

    // Button
    val greyButtonOutline: Color,
    val kakaoLoginButton: Color,
    val greyDefaultButton: Color,
    val greyButtonDisable: Color,

    val greenButtonOpacity: Color,

    // Non-Semantic
    val systemGreen: Color,
    val systemRed: Color,

    // System Grey
    val systemGrey1: Color,
    val systemGrey2: Color,
    val systemGrey3: Color,
    val systemGrey4: Color,
    val systemGrey5: Color,
    val systemGrey6: Color,

    // TextField Cursor
    val textFieldCursor: Color
)

val defaultTeam6Colors = Team6Colors(
    // Primary Colors
    purple80 = Color(0xFFD0BCFF),
    purpleGrey80 = Color(0xFFCCC2DC),
    pink80 = Color(0xFFEFB8C8),
    primaryRed = Color(0xFFFF5D5D),
    primaryGreen = Color(0xFF13D015),
    primaryMain = Color(0xFF99F977),

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
    greyDisabled = Color(0xFF393C42),

    // Background
    greyElevatedBackground = Color(0xFF1C1C1D),
    greyWashBackground = Color(0xFF18181A),
    greyDivider = Color(0xFF4D4D4D),

    // Card
    greyCard = Color(0xFF1C1C1E),
    greyElevatedCard = Color(0xFF2C2C2E),

    // Button
    greyButtonOutline = Color(0xFF36363A),
    greyDefaultButton = Color(0xFF2C2C30),
    greyButtonDisable = Color(0x662C2C30),
    kakaoLoginButton = Color(0xFFFAE100),

    greenButtonOpacity = Color(0x1F8AF265),

    // Non-Semantic
    systemGreen = Color(0xFF99ED7B),
    systemRed = Color(0xFFFF5D5D),

    // System Grey
    systemGrey1 = Color(0xFF7E7E8A),
    systemGrey2 = Color(0xFF5B5B63),
    systemGrey3 = Color(0xFF424249),
    systemGrey4 = Color(0xFF38383E),
    systemGrey5 = Color(0xFF27272B),
    systemGrey6 = Color(0xFF1F1F23),

    // TextField Cursor
    textFieldCursor = Color(0xFF90E772)
)

val LocalTeam6Colors = staticCompositionLocalOf { defaultTeam6Colors }
