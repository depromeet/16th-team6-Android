package com.depromeet.team6.presentation.type

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.depromeet.team6.R

enum class LoginViewPagerType(
    @DrawableRes val imageRes: Int,
    @StringRes val mainTextRes: Int,
    @StringRes val subTextRes: Int
) {
    FIRST(
        imageRes = R.drawable.img_login_image1,
        mainTextRes = R.string.login_pager_main_text_second,
        subTextRes = R.string.login_pager_sub_text_first
    ),
    SECOND(
        imageRes = R.drawable.img_login_image2,
        mainTextRes = R.string.login_pager_main_text_second,
        subTextRes = R.string.login_pager_sub_text_second
    ),
    THIRD(
        imageRes = R.drawable.img_login_image3,
        mainTextRes = R.string.login_pager_main_text_third,
        subTextRes = R.string.login_pager_sub_text_third
    )
}
