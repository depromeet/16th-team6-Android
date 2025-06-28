package com.depromeet.team6.domain

object Network {
    const val INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR"
}

object Auth {
    const val APP_001 = "APP_001"

    const val ATH_001 = "ATH_001"
    const val ATH_002 = "ATH_002"
    const val ATH_003 = "ATH_003"

    const val TOK_001 = "TOK_001"
    const val TOK_002 = "TOK_002"

    const val USR_002 = "USR_002"
}

object RequestFormat {
    const val REQ_001 = "REQ_001"
    const val REQ_002 = "REQ_002"

    const val LOC_001 = "LOC_001"
    const val LOC_002 = "LOC_002"
    const val LOC_003 = "LOC_003"
    const val LOC_004 = "LOC_004"
}

object RouteMap {
    const val TRS_001 = "TRS_001"
    const val TRS_002 = "TRS_002"

    const val TRS_011 = "TRS_011"
    const val TRS_012 = "TRS_012"
    const val TRS_013 = "TRS_013"
    const val TRS_014 = "TRS_014"
    const val TRS_015 = "TRS_015"
    const val TRS_016 = "TRS_016"
    const val TRS_017 = "TRS_017"
    const val TRS_018 = "TRS_018"
    const val TRS_019 = "TRS_019"
}

object ToastMessage {
    const val UNKNOWN = "알 수 없음"

    const val NETWORK = "네트워크 오류가 발생했습니다. 잠시 후 다시 시도해주세요."

    const val LOGIN_DATA_EXPIRED = "로그인 정보가 만료되었습니다. 다시 로그인 해주세요."
    const val EXIST_USER = "이미 존재하는 회원정보 입니다."

    const val RANGE_LOCATION = "유효 범위를 벗어났습니다. 위치를 다시 설정해주세요"
    const val RANGE_CURRENT_LOCATION = "유효범위를 벗어났습니다. 현위치를 다시 확인해주세요."

    const val SHORT_DISTANCE= "출발지와 도착지가 너무 가깝습니다. 출발지를 다시 선택해 주세요."
    const val OUT_OF_RANGE="출발지가 수도권을 벗어났습니다. 출발지를 다시 선택해 주세요."

    const val BUS_LOCATION = "버스 위치정보를 가져올 수 없습니다."
    const val BUS_ROUTE = "버스 노선정보를 찾을 수 없습니다."
    const val BUS_ARRIVAL_INCORRECT = "버스 도착정보를 알 수 없습니다."
}
