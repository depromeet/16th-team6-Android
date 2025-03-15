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

    // Heading
    val heading1Bold34: TextStyle,
    val heading1SemiBold34: TextStyle,
    val heading2Bold26: TextStyle,
    val heading2SemiBold26: TextStyle,
    val heading3Bold22: TextStyle,
    val heading3SemiBold22: TextStyle,
    val heading4Bold20: TextStyle,
    val heading4SemiBold20: TextStyle,
    val heading4Medium20: TextStyle,
    val heading5Bold17: TextStyle,
    val heading5SemiBold17: TextStyle,
    val heading6Bold15: TextStyle,
    val heading6SemiBold15: TextStyle,

    // Body
    val bodyMedium17: TextStyle,
    val bodyRegular17: TextStyle,
    val bodyMedium15: TextStyle,
    val bodyRegular15: TextStyle,
    val bodySemiBold14: TextStyle,
    val bodyMedium14: TextStyle,
    val bodyRegular14: TextStyle,
    val bodySemiBold13: TextStyle,
    val bodyMedium13: TextStyle,
    val bodyRegular13: TextStyle,
    val bodySemiBold12: TextStyle,
    val bodyMedium12: TextStyle,
    val bodyRegular12: TextStyle,
    val bodySemiBold11: TextStyle,
    val bodyMedium11: TextStyle,
    val bodyRegular11: TextStyle,
    val bodySemiBold10: TextStyle,
    val bodyMedium10: TextStyle,
    val bodyRegular10: TextStyle,

    // Extra
    val extraBold44: TextStyle
)

// Pretendard FontFamilies (예: bold, semibold, etc.)
private val pretendardBold = FontFamily(Font(R.font.pretendard_bold))
private val pretendardSemiBold = FontFamily(Font(R.font.pretendard_semibold))
private val pretendardMedium = FontFamily(Font(R.font.pretendard_medium))
private val pretendardRegular = FontFamily(Font(R.font.pretendard_regular))
private val pretendardExtraBold = FontFamily(Font(R.font.pretendard_extrabold))

val defaultTeam6Typography = Team6Typography(
    // Heading
    heading1Bold34 = TextStyle(
        fontFamily = pretendardBold,
        fontSize = 34.sp,
        lineHeight = 41.sp
    ),
    heading1SemiBold34 = TextStyle(
        fontFamily = pretendardSemiBold,
        fontSize = 34.sp,
        lineHeight = 41.sp
    ),
    heading2Bold26 = TextStyle(
        fontFamily = pretendardBold,
        fontSize = 26.sp,
        lineHeight = 34.sp
    ),
    heading2SemiBold26 = TextStyle(
        fontFamily = pretendardSemiBold,
        fontSize = 26.sp,
        lineHeight = 34.sp
    ),
    heading3Bold22 = TextStyle(
        fontFamily = pretendardBold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    heading3SemiBold22 = TextStyle(
        fontFamily = pretendardSemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    heading4Bold20 = TextStyle(
        fontFamily = pretendardBold,
        fontSize = 20.sp,
        lineHeight = 25.sp
    ),
    heading4SemiBold20 = TextStyle(
        fontFamily = pretendardSemiBold,
        fontSize = 20.sp,
        lineHeight = 25.sp
    ),
    heading4Medium20 = TextStyle(
        fontFamily = pretendardMedium,
        fontSize = 20.sp,
        lineHeight = 25.sp
    ),
    heading5Bold17 = TextStyle(
        fontFamily = pretendardBold,
        fontSize = 17.sp,
        lineHeight = 24.sp
    ),
    heading5SemiBold17 = TextStyle(
        fontFamily = pretendardSemiBold,
        fontSize = 17.sp,
        lineHeight = 24.sp
    ),
    heading6Bold15 = TextStyle(
        fontFamily = pretendardBold,
        fontSize = 15.sp,
        lineHeight = 20.sp
    ),
    heading6SemiBold15 = TextStyle(
        fontFamily = pretendardSemiBold,
        fontSize = 15.sp,
        lineHeight = 20.sp
    ),

    // Body
    bodyMedium17 = TextStyle(
        fontFamily = pretendardMedium,
        fontSize = 17.sp,
        lineHeight = 24.sp
    ),
    bodyRegular17 = TextStyle(
        fontFamily = pretendardRegular,
        fontSize = 17.sp,
        lineHeight = 24.sp
    ),
    bodyMedium15 = TextStyle(
        fontFamily = pretendardMedium,
        fontSize = 15.sp,
        lineHeight = 20.sp
    ),
    bodyRegular15 = TextStyle(
        fontFamily = pretendardRegular,
        fontSize = 15.sp,
        lineHeight = 20.sp
    ),
    bodySemiBold14 = TextStyle(
        fontFamily = pretendardSemiBold,
        fontSize = 14.sp,
        lineHeight = 18.sp
    ),
    bodyMedium14 = TextStyle(
        fontFamily = pretendardMedium,
        fontSize = 14.sp,
        lineHeight = 18.sp
    ),
    bodyRegular14 = TextStyle(
        fontFamily = pretendardRegular,
        fontSize = 14.sp,
        lineHeight = 18.sp
    ),
    bodySemiBold13 = TextStyle(
        fontFamily = pretendardSemiBold,
        fontSize = 13.sp,
        lineHeight = 16.sp
    ),
    bodyMedium13 = TextStyle(
        fontFamily = pretendardMedium,
        fontSize = 13.sp,
        lineHeight = 16.sp
    ),
    bodyRegular13 = TextStyle(
        fontFamily = pretendardRegular,
        fontSize = 13.sp,
        lineHeight = 16.sp
    ),
    bodySemiBold12 = TextStyle(
        fontFamily = pretendardSemiBold,
        fontSize = 12.sp,
        lineHeight = 14.sp
    ),
    bodyMedium12 = TextStyle(
        fontFamily = pretendardMedium,
        fontSize = 12.sp,
        lineHeight = 14.sp
    ),
    bodyRegular12 = TextStyle(
        fontFamily = pretendardRegular,
        fontSize = 12.sp,
        lineHeight = 14.sp
    ),
    bodySemiBold11 = TextStyle(
        fontFamily = pretendardSemiBold,
        fontSize = 11.sp,
        lineHeight = 13.sp
    ),
    bodyMedium11 = TextStyle(
        fontFamily = pretendardMedium,
        fontSize = 11.sp,
        lineHeight = 13.sp
    ),
    bodyRegular11 = TextStyle(
        fontFamily = pretendardRegular,
        fontSize = 11.sp,
        lineHeight = 13.sp
    ),
    bodySemiBold10 = TextStyle(
        fontFamily = pretendardSemiBold,
        fontSize = 10.sp,
        lineHeight = 12.sp
    ),
    bodyMedium10 = TextStyle(
        fontFamily = pretendardMedium,
        fontSize = 10.sp,
        lineHeight = 12.sp
    ),
    bodyRegular10 = TextStyle(
        fontFamily = pretendardRegular,
        fontSize = 10.sp,
        lineHeight = 12.sp
    ),

    // Extra
    extraBold44 = TextStyle(
        fontFamily = pretendardExtraBold,
        fontSize = 44.sp,
        lineHeight = 54.sp
    )
)

val LocalTeam6Typography = staticCompositionLocalOf { defaultTeam6Typography }
