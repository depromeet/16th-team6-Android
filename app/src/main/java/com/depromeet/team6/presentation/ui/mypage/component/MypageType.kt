package com.depromeet.team6.presentation.ui.mypage.component

import androidx.annotation.StringRes
import com.depromeet.team6.R

enum class MypageType(
    @StringRes val titleStringRes: Int,
    @StringRes val subTitleStringRes: Int
) {
    ADDRESS(
        titleStringRes = R.string.onboarding_home_enroll_title,
        subTitleStringRes = R.string.onboarding_home_enroll_sub_title
    ),
    ALARM(
        titleStringRes = R.string.onboarding_alarm_enroll_title,
        subTitleStringRes = R.string.onboarding_alarm_enroll_sub_title
    )
}