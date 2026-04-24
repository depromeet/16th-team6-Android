package com.depromeet.team6.presentation.type

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.depromeet.team6.R

enum class LoginViewPagerType(
    @DrawableRes val imageRes: Int,
    @StringRes val textRes: Int
) {
    FIRST(
        imageRes = R.drawable.img_login_image1,
        textRes = R.string.login_pager_text_first
    ),
    SECOND(
        imageRes = R.drawable.img_login_image2,
        textRes = R.string.login_pager_text_second
    ),
    THIRD(
        imageRes = R.drawable.img_login_image3,
        textRes = R.string.login_pager_text_third
    ),
    FOURTH(
        imageRes = R.drawable.img_login_image4,
        textRes = R.string.login_pager_text_fourth
    )
}
