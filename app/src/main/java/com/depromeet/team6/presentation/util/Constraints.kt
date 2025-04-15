package com.depromeet.team6.presentation.util

object Login {
    const val PLATFORM = "KAKAO"
}

object Token {
    const val BEARER = "Bearer "
}

object Provider {
    const val KAKAO = 0
}

object DefaultLatLng {
    const val DEFAULT_LAT = 37.5665
    const val DEFAULT_LNG = 126.9780
}

object WebViewUrl {
    const val PRIVACY_POLICY_URL =
        "https://mammoth-cheese-88e.notion.site/1008a99e3bbe80e88468c11f09c5a2dc?pvs=4"
}

object BusOperationInfo {
    const val WEEKDAY = "WEEKDAY"
    const val SATURDAY = "SATURDAY"
    const val HOLIDAY = "HOLIDAY"

    const val WEEKDAY_KR = "평일"
    const val SATURDAY_KR = "토요일"
    const val HOLIDAY_KR = "공휴일"
    const val UNKNOWN_KR = "알 수 없음"
}

object AmplitudeCommon {
    const val SCREEN_NAME = "screen_name"
    const val USER_ID = "USER_ID"
}

object OnboardingAmplitude {
    const val USER_ALARM_FREQUENCIES = "user_alarm_frequencies"
    const val HOME_REGISTER_LOCATION_PERMISSION_CHECK = "home_register_location_permission_clicked"
    const val HOME_REGISTER_COMPLETE_CLICKED = "home_register_complete_clicked"
    const val ALARM_SETTING_ALARM_PERMISSION_CLICKED = "alarm_setting_alarm_permission_clicked"
    const val HOME_REGISTER = "집 등록(온보딩)"
    const val ALARM_REGISTER = "알림 등록(온보딩)"
}

object HomeAmplitude {
    const val HOME = "홈"
    const val HOME_COURSESEARCH_ENTERED_DIRECT = "home_coursesearch_entered_direct"
    const val HOME_COURSESEARCH_ENTERED_WITH_MAP_DRAG = "home_coursesearch_entered_with_map_drag"
    const val HOME_COURSESEARCH_ENTERED_WITH_CURRENT_LOCATION = "home_coursesearch_entered_with_current_location"
    const val HOME_COURSESEARCH_ENTERED_WITH_INPUT = "home_coursesearch_entered_with_input"
    const val HOME_DESTINATION_CLICKED = "home_destination_clicked"
    const val HOME_DEPARTURE_TIME_CLICKED = "home_departure_time_clicked"
    const val HOME_DEPARTURE_TIME_SUGGESTION_CLICKED = "home_departure_time_suggestion_clicked"
    const val HOME_ROUTE_CLICKED = "home_route_clicked"
    const val POPUP = "팝업"
    const val ALERT_END_POPUP_1 = "alert_end_popup_1"
    const val ALERT_END_POPUP_2 = "alert_end_popup_2"
}

object LockAmplitude {
    const val LOCK = "잠금화면"
    const val LOCK_BUTTON = "lock_button"
    const val LOCK_BUTTON_START = "lock_button_start"
    const val LOCK_BUTTON_LATER_ROUTE = "lock_button_later_route"
}

object CourseSearchAmplitude {
    const val COURSE_SEARCH = "경로 탐색"
    const val COURSE_SEARCH_EVENT_CARD_CLICKED = "courssesearch_card"
    const val COURSE_SEARCH_EVENT_ITEM_TOGGLED = "coursesearch_toggle"
    const val COURSE_SEARCH_EVENT_DURATION = "coursesearch_view_duration"
    const val COURSE_SEARCH_EVENT_ALARM_REGISTERED = "alert_button"
    const val COURSE_SEARCH_TOGGLE_DISABLED = "coursesearch_toggle"
    const val COURSE_SEARCH_ITEM_CARD_CLICKED = "courssesearch_card_expand"
    const val COURSE_SEARCH_ITEM_DETAIL_TEXT_CLICKED = "courssesearch_card_viewdetails"
    const val COURSE_SEARCH_STAY_TIME = "coursesearch_view_duration"
    const val COURSE_SEARCH_ALARM_REGISTERED = "alert_button"
}

object ItineraryAmplitude {
    const val ITINERARY = "상세경로"
}