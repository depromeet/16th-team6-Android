package com.depromeet.team6.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.depromeet.team6.R

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)

// Heading Font Setting
val heading1Bold34 = TextStyle(
    fontFamily = FontFamily(Font(R.font.pretendard_bold)),
    fontSize = 34.sp,
    lineHeight = 41.sp
)

val heading1SemiBold34 = TextStyle(
    fontFamily = FontFamily(Font(R.font.pretendard_semibold)),
    fontSize = 34.sp,
    lineHeight = 41.sp
)

val heading2Bold26 = TextStyle(
    fontFamily = FontFamily(Font(R.font.pretendard_bold)),
    fontSize = 26.sp,
    lineHeight = 34.sp
)

val heading2SemiBold26 = TextStyle(
    fontFamily = FontFamily(Font(R.font.pretendard_semibold)),
    fontSize = 26.sp,
    lineHeight = 34.sp
)

val heading3Bold22 = TextStyle(
    fontFamily = FontFamily(Font(R.font.pretendard_bold)),
    fontSize = 22.sp,
    lineHeight = 28.sp
)

val heading3SemiBold22 = TextStyle(
    fontFamily = FontFamily(Font(R.font.pretendard_semibold)),
    fontSize = 22.sp,
    lineHeight = 28.sp
)

val heading4Bold20 = TextStyle(
    fontFamily = FontFamily(Font(R.font.pretendard_bold)),
    fontSize = 20.sp,
    lineHeight = 25.sp
)

val heading4SemiBold20 = TextStyle(
    fontFamily = FontFamily(Font(R.font.pretendard_semibold)),
    fontSize = 20.sp,
    lineHeight = 25.sp
)

val heading5Bold17 = TextStyle(
    fontFamily = FontFamily(Font(R.font.pretendard_bold)),
    fontSize = 17.sp,
    lineHeight = 24.sp
)

val heading5SemiBold17 = TextStyle(
    fontFamily = FontFamily(Font(R.font.pretendard_semibold)),
    fontSize = 17.sp,
    lineHeight = 24.sp
)

val heading6Bold15 = TextStyle(
    fontFamily = FontFamily(Font(R.font.pretendard_bold)),
    fontSize = 15.sp,
    lineHeight = 20.sp
)

val heading6SemiBold15 = TextStyle(
    fontFamily = FontFamily(Font(R.font.pretendard_semibold)),
    fontSize = 15.sp,
    lineHeight = 20.sp
)