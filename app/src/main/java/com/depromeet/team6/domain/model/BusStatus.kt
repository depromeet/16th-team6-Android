package com.depromeet.team6.domain.model

enum class BusStatus {
    WAITING, // 출발 대기
    SOON, // 곧 도착
    OPERATING, // 운행 중
    END, // 운행 종료
    UNKNOWN
}

// 버스 혼잡도
enum class BusCongestion {
    LOW,
    MEDIUM,
    HIGH,
    VERY_HIGH,
    UNKNOWN
}
