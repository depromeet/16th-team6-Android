package com.depromeet.team6.domain.model

import androidx.compose.ui.graphics.Color
import com.depromeet.team6.ui.theme.defaultTeam6Colors

enum class BusStatus(val string: String) {
    WAITING("출발 대기"),
    SOON("곧 도착"),
    OPERATING("운행 중"),
    END("운행 종료")
}

// 버스 혼잡도
enum class BusCongestion {
    LOW,
    MEDIUM,
    HIGH,
    VERY_HIGH,
    UNKNOWN
}

data class BusCongestionInfo(
    val label: String,
    val color: Color
)

fun BusCongestion.toInfo(): BusCongestionInfo {
    return when (this) {
        BusCongestion.LOW -> BusCongestionInfo("여유", defaultTeam6Colors.systemGreen)
        BusCongestion.MEDIUM -> BusCongestionInfo("보통", defaultTeam6Colors.systemBlue)
        BusCongestion.HIGH, BusCongestion.VERY_HIGH -> BusCongestionInfo("혼잡", defaultTeam6Colors.systemRed)
        BusCongestion.UNKNOWN -> BusCongestionInfo("", Color.Gray)
    }
}
