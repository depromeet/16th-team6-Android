package com.depromeet.team6.presentation.type

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.depromeet.team6.R

enum class OnboardingPermissionType(
    @StringRes val titleStringRes: Int,
    @StringRes val typeStringRes: Int,
    @DrawableRes val iconRes: Int,
    @StringRes val subTitleStringRes: Int
) {
    LOCATION(
        titleStringRes = R.string.onboarding_location_permission_bottom_sheet_title,
        typeStringRes = R.string.onboarding_location_permission_bottom_sheet_type,
        iconRes = R.drawable.ic_onboarding_bottom_sheet_gps_16,
        subTitleStringRes = R.string.onboarding_location_permission_bottom_sheet_sub_title
    ),
    NOTIFICATION(
        titleStringRes = R.string.onboarding_notification_permission_bottom_sheet_title,
        typeStringRes = R.string.onboarding_notification_permission_bottom_sheet_type,
        iconRes = R.drawable.ic_onboarding_bottom_sheet_bell_16,
        subTitleStringRes = R.string.onboarding_notification_permission_bottom_sheet_sub_title
    )
}
