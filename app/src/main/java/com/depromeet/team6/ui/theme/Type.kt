package com.depromeet.team6.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.depromeet.team6.R

@Immutable
data class Team6Typography(

    // Display
    val display1_D1EB56: TextStyle,
    val display2_D2EB48: TextStyle,
    val display3_D3EB40: TextStyle,
    val display4_D4SB28: TextStyle,

    // Heading
    val heading1_H1B22: TextStyle,
    val heading2_H2B20: TextStyle,
    val heading3_H3SB17: TextStyle,

    // Body
    val body1_B1R17: TextStyle,
    val body2_B2SB15: TextStyle,
    val body3_B3M15: TextStyle,
    val body4_B4R15: TextStyle,
    val body5_B5SB14: TextStyle,
    val body6_B6R14: TextStyle,
    val body7_B7M13: TextStyle,
    val detail1_R12: TextStyle,
    val detail2_M11: TextStyle,
    val detail3_M9: TextStyle

)

// Pretendard FontFamilies (예: bold, semibold, etc.)
private val pretendardBold = FontFamily(Font(R.font.pretendard_bold))
private val pretendardSemiBold = FontFamily(Font(R.font.pretendard_semibold))
private val pretendardMedium = FontFamily(Font(R.font.pretendard_medium))
private val pretendardRegular = FontFamily(Font(R.font.pretendard_regular))
private val pretendardExtraBold = FontFamily(Font(R.font.pretendard_extrabold))

val defaultTeam6Typography = Team6Typography(
    // Heading
    display1_D1EB56 = TextStyle(
        fontFamily = pretendardExtraBold,
        fontSize = 56.sp,
        lineHeight = 67.sp
    ),
    display2_D2EB48 = TextStyle(
        fontFamily = pretendardExtraBold,
        fontSize = 48.sp,
        lineHeight = 54.sp
    ),
    display3_D3EB40 = TextStyle(
        fontFamily = pretendardExtraBold,
        fontSize = 40.sp,
        lineHeight = 42.sp
    ),
    display4_D4SB28 = TextStyle(
        fontFamily = pretendardSemiBold,
        fontSize = 28.sp,
        lineHeight = 34.sp
    ),
    heading1_H1B22 = TextStyle(
        fontFamily = pretendardBold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    heading2_H2B20 = TextStyle(
        fontFamily = pretendardBold,
        fontSize = 20.sp,
        lineHeight = 25.sp
    ),
    heading3_H3SB17 = TextStyle(
        fontFamily = pretendardSemiBold,
        fontSize = 17.sp,
        lineHeight = 24.sp
    ),

    body2_B2SB15 = TextStyle(
        fontFamily = pretendardSemiBold,
        fontSize = 15.sp,
        lineHeight = 20.sp
    ),

    // Body
    body1_B1R17 = TextStyle(
        fontFamily = pretendardRegular,
        fontSize = 17.sp,
        lineHeight = 24.sp
    ),
    body3_B3M15 = TextStyle(
        fontFamily = pretendardMedium,
        fontSize = 15.sp,
        lineHeight = 20.sp
    ),
    body4_B4R15 = TextStyle(
        fontFamily = pretendardRegular,
        fontSize = 15.sp,
        lineHeight = 20.sp
    ),
    body5_B5SB14 = TextStyle(
        fontFamily = pretendardSemiBold,
        fontSize = 14.sp,
        lineHeight = 18.sp
    ),
    body6_B6R14 = TextStyle(
        fontFamily = pretendardRegular,
        fontSize = 14.sp,
        lineHeight = 18.sp
    ),
    body7_B7M13 = TextStyle(
        fontFamily = pretendardMedium,
        fontSize = 13.sp,
        lineHeight = 16.sp
    ),
    detail1_R12 = TextStyle(
        fontFamily = pretendardRegular,
        fontSize = 12.sp,
        lineHeight = 14.sp
    ),
    detail2_M11 = TextStyle(
        fontFamily = pretendardMedium,
        fontSize = 11.sp,
        lineHeight = 13.sp
    ),
    detail3_M9 = TextStyle(
        fontFamily = pretendardMedium,
        fontSize = 10.sp,
        lineHeight = 12.sp
    )
)

val LocalTeam6Typography = staticCompositionLocalOf { defaultTeam6Typography }
