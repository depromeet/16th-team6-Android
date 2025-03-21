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
    val textFieldCursor: Color,

    // BUS
    val busRegular: Color,
    val busTown: Color,
    val busMainLine: Color,
    val busWideArea: Color,
    val busAirport: Color,

    // SUBWAY
    val subwayLine1: Color,
    val subwayLine2: Color,
    val subwayLine3: Color,
    val subwayLine4: Color,
    val subwayLine5: Color,
    val subwayLine6: Color,
    val subwayLine7: Color,
    val subwayLine8: Color,
    val subwayLine9: Color,
    val subwayAirport: Color,
    val subwayGyeongUiJungang: Color,
    val subwayGyeongChun: Color,
    val subwaySuinBundang: Color,
    val subwayShinBundang: Color,
    val subwayGyeongGang: Color,
    val subwaySeoHae: Color,
    val subwayIncheon1: Color,
    val subwayIncheon2: Color,
    val subwayEverLine: Color,
    val subwayUijeongbu: Color,
    val subwayUiSinseol: Color,
    val subwayGimpo: Color,
    val subwaySillim: Color,
    val subwayGTX_A: Color
)

val defaultTeam6Colors = Team6Colors(
    // Primary Colors
    purple80 = Color(0xFFD0BCFF),
    purpleGrey80 = Color(0xFFCCC2DC),
    pink80 = Color(0xFFEFB8C8),
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
    greyWashBackground = Color(0xFF131315),
    greyDivider = Color(0xFF4D4D4D),

    // Card
    greyCard = Color(0xFF1C1C1E),
    greyElevatedCard = Color(0xFF2C2C2E),

    // Button
    greyButtonOutline = Color(0xFF36363A),
    greyDefaultButton = Color(0xFF2C2C30),
    greyButtonDisable = Color(0x662C2C30),
    kakaoLoginButton = Color(0xFFFAE100),

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
    textFieldCursor = Color(0xFF90E772),

    // BUS
    busRegular = Color(0xFF24B847),
    busTown = Color(0xFF6FC53F),
    busMainLine = Color(0xFF1777FF),
    busWideArea = Color(0xFFF24747),
    busAirport = Color(0xFF5FBBF9),

    // SUBWAY
    subwayLine1 = Color(0xFF1777FF),
    subwayLine2 = Color(0xFF24B847),
    subwayLine3 = Color(0xFFED7B2A),
    subwayLine4 = Color(0xFF3EB1FF),
    subwayLine5 = Color(0xFF924FF6),
    subwayLine6 = Color(0xFFC86E31),
    subwayLine7 = Color(0xFF9BA81D),
    subwayLine8 = Color(0xFFF54B90),
    subwayLine9 = Color(0xFFD8A516),
    subwayAirport = Color(0xFF5CA9DB),
    subwayGyeongUiJungang = Color(0xFF3EADAD),
    subwayGyeongChun = Color(0xFF2BBA8B),
    subwaySuinBundang = Color(0xFFDDB421),
    subwayShinBundang = Color(0xFFBF3649),
    subwayGyeongGang = Color(0xFF396CC3),
    subwaySeoHae = Color(0xFF90E772),
    subwayIncheon1 = Color(0xFF71A4E6),
    subwayIncheon2 = Color(0xFFD59F5E),
    subwayEverLine = Color(0xFF66BA60),
    subwayUijeongbu = Color(0xFFE68E24),
    subwayUiSinseol = Color(0xFFBBB51C),
    subwayGimpo = Color(0xFF9F7A10),
    subwaySillim = Color(0xFF608CC4),
    subwayGTX_A = Color(0xFF8F5787)
)

val LocalTeam6Colors = staticCompositionLocalOf { defaultTeam6Colors }
