package com.depromeet.team6.presentation.util.view

import android.util.SparseArray
import android.util.SparseIntArray
import androidx.compose.ui.graphics.Color
import com.depromeet.team6.R
import com.depromeet.team6.domain.model.course.TransportType
import com.depromeet.team6.ui.theme.defaultTeam6Colors

object TransportTypeUiMapper {

    // 리소스 ID 매핑 테이블 (key = type.ordinal * 1000 + index)
    private val iconTable = SparseIntArray().apply {
        // WALK: index 0
        put(key(TransportType.WALK), R.drawable.ic_walk)

        // BUS: index 0~N
        put(key(TransportType.BUS, 1), R.drawable.ic_bus_regular)
        put(key(TransportType.BUS, 2), R.drawable.ic_bus_main_line)
        put(key(TransportType.BUS, 3), R.drawable.ic_bus_town)
        put(key(TransportType.BUS, 4), R.drawable.ic_bus_wide_area)
        put(key(TransportType.BUS, 5), R.drawable.ic_bus_airport)
        put(key(TransportType.BUS, 6), R.drawable.ic_bus_wide_area)
        put(key(TransportType.BUS, 10), R.drawable.ic_bus_regular)
        put(key(TransportType.BUS, 11), R.drawable.ic_bus_main_line)
        put(key(TransportType.BUS, 12), R.drawable.ic_bus_regular)
        put(key(TransportType.BUS, 13), R.drawable.ic_bus_town)
        put(key(TransportType.BUS, 14), R.drawable.ic_bus_wide_area)
        put(key(TransportType.BUS, 15), R.drawable.ic_bus_wide_area)
        put(key(TransportType.BUS, 16), R.drawable.ic_bus_wide_area)
        put(key(TransportType.BUS, 17), R.drawable.ic_bus_airport)
        put(key(TransportType.BUS, 21), R.drawable.ic_bus_town)
        put(key(TransportType.BUS, 22), R.drawable.ic_bus_wide_area)
        put(key(TransportType.BUS, 23), R.drawable.ic_bus_wide_area)


        // SUBWAY: index 0~N
        put(key(TransportType.SUBWAY, 1), R.drawable.ic_subway_1)
        put(key(TransportType.SUBWAY, 2), R.drawable.ic_subway_2)
        put(key(TransportType.SUBWAY, 3), R.drawable.ic_subway_3)
        put(key(TransportType.SUBWAY, 4), R.drawable.ic_subway_4)
        put(key(TransportType.SUBWAY, 5), R.drawable.ic_subway_5)
        put(key(TransportType.SUBWAY, 6), R.drawable.ic_subway_6)
        put(key(TransportType.SUBWAY, 7), R.drawable.ic_subway_7)
        put(key(TransportType.SUBWAY, 8), R.drawable.ic_subway_8)
        put(key(TransportType.SUBWAY, 9), R.drawable.ic_subway_9)
        put(key(TransportType.SUBWAY, 10), R.drawable.ic_subway_2)
        put(key(TransportType.SUBWAY, 11), R.drawable.ic_subway_2)
        put(key(TransportType.SUBWAY, 100), R.drawable.ic_subway_suin_bundang)
        put(key(TransportType.SUBWAY, 101), R.drawable.ic_subway_airport)
        put(key(TransportType.SUBWAY, 104), R.drawable.ic_subway_gyeongui_jungang)
        put(key(TransportType.SUBWAY, 107), R.drawable.ic_subway_ever_line)
        put(key(TransportType.SUBWAY, 108), R.drawable.ic_subway_gyeongchun)
        put(key(TransportType.SUBWAY, 109), R.drawable.ic_subway_shin_bundang)
        put(key(TransportType.SUBWAY, 110), R.drawable.ic_subway_uijeongbu)
        put(key(TransportType.SUBWAY, 112), R.drawable.ic_subway_gyeonggang)
        put(key(TransportType.SUBWAY, 113), R.drawable.ic_subway_ui_sinseol)
        put(key(TransportType.SUBWAY, 114), R.drawable.ic_subway_seohae)
        put(key(TransportType.SUBWAY, 115), R.drawable.ic_subway_gimpo_gold)
        put(key(TransportType.SUBWAY, 116), R.drawable.ic_subway_sillim)
        put(key(TransportType.SUBWAY, 117), R.drawable.ic_subway_1)
        put(key(TransportType.SUBWAY, 118), R.drawable.ic_subway_1)
        put(key(TransportType.SUBWAY, 119), R.drawable.ic_subway_4)
        put(key(TransportType.SUBWAY, 120), R.drawable.ic_subway_9)
        put(key(TransportType.SUBWAY, 121), R.drawable.ic_subway_suin_bundang)
        put(key(TransportType.SUBWAY, 122), R.drawable.ic_subway_gyeongui_jungang)
        put(key(TransportType.SUBWAY, 123), R.drawable.ic_subway_gyeongchun)
        put(key(TransportType.SUBWAY, 124), R.drawable.ic_subway_airport)
        put(key(TransportType.SUBWAY, 125), R.drawable.ic_subway_gtx_a)
        put(key(TransportType.SUBWAY, 21), R.drawable.ic_subway_incheon_1)
        put(key(TransportType.SUBWAY, 22), R.drawable.ic_subway_incheon_2)

    }

    private val colorTable = SparseArray<Color>().apply {
        // WALK: index 0
        put(key(TransportType.WALK), defaultTeam6Colors.walkColor[0].second)

        // BUS: index 0~N
        put(key(TransportType.BUS, 1), defaultTeam6Colors.busColors[0].second)
        put(key(TransportType.BUS, 2), defaultTeam6Colors.busColors[2].second)
        put(key(TransportType.BUS, 3), defaultTeam6Colors.busColors[1].second)
        put(key(TransportType.BUS, 4), defaultTeam6Colors.busColors[3].second)
        put(key(TransportType.BUS, 5), defaultTeam6Colors.busColors[4].second)
        put(key(TransportType.BUS, 6), defaultTeam6Colors.busColors[3].second)
        put(key(TransportType.BUS, 10), defaultTeam6Colors.busColors[0].second)
        put(key(TransportType.BUS, 11), defaultTeam6Colors.busColors[2].second)
        put(key(TransportType.BUS, 12), defaultTeam6Colors.busColors[0].second)
        put(key(TransportType.BUS, 13), defaultTeam6Colors.busColors[1].second)
        put(key(TransportType.BUS, 14), defaultTeam6Colors.busColors[3].second)
        put(key(TransportType.BUS, 15), defaultTeam6Colors.busColors[3].second)
        put(key(TransportType.BUS, 16), defaultTeam6Colors.busColors[3].second)
        put(key(TransportType.BUS, 17), defaultTeam6Colors.busColors[4].second)
        put(key(TransportType.BUS, 21), defaultTeam6Colors.busColors[1].second)
        put(key(TransportType.BUS, 22), defaultTeam6Colors.busColors[3].second)
        put(key(TransportType.BUS, 23), defaultTeam6Colors.busColors[3].second)

        // SUBWAY: index 0~N
        put(key(TransportType.SUBWAY, 1), defaultTeam6Colors.subwayColors[0].second)
        put(key(TransportType.SUBWAY, 2), defaultTeam6Colors.subwayColors[1].second)
        put(key(TransportType.SUBWAY, 3), defaultTeam6Colors.subwayColors[2].second)
        put(key(TransportType.SUBWAY, 4), defaultTeam6Colors.subwayColors[3].second)
        put(key(TransportType.SUBWAY, 5), defaultTeam6Colors.subwayColors[4].second)
        put(key(TransportType.SUBWAY, 6), defaultTeam6Colors.subwayColors[5].second)
        put(key(TransportType.SUBWAY, 7), defaultTeam6Colors.subwayColors[6].second)
        put(key(TransportType.SUBWAY, 8), defaultTeam6Colors.subwayColors[7].second)
        put(key(TransportType.SUBWAY, 9), defaultTeam6Colors.subwayColors[8].second)
        put(key(TransportType.SUBWAY, 10), defaultTeam6Colors.subwayColors[1].second)
        put(key(TransportType.SUBWAY, 11), defaultTeam6Colors.subwayColors[1].second)
        put(key(TransportType.SUBWAY, 100), defaultTeam6Colors.subwayColors[12].second)
        put(key(TransportType.SUBWAY, 101), defaultTeam6Colors.subwayColors[9].second)
        put(key(TransportType.SUBWAY, 104), defaultTeam6Colors.subwayColors[10].second)
        put(key(TransportType.SUBWAY, 107), defaultTeam6Colors.subwayColors[18].second)
        put(key(TransportType.SUBWAY, 108), defaultTeam6Colors.subwayColors[11].second)
        put(key(TransportType.SUBWAY, 109), defaultTeam6Colors.subwayColors[13].second)
        put(key(TransportType.SUBWAY, 110), defaultTeam6Colors.subwayColors[19].second)
        put(key(TransportType.SUBWAY, 112), defaultTeam6Colors.subwayColors[14].second)
        put(key(TransportType.SUBWAY, 113), defaultTeam6Colors.subwayColors[20].second)
        put(key(TransportType.SUBWAY, 114), defaultTeam6Colors.subwayColors[15].second)
        put(key(TransportType.SUBWAY, 115), defaultTeam6Colors.subwayColors[21].second)
        put(key(TransportType.SUBWAY, 116), defaultTeam6Colors.subwayColors[22].second)
        put(key(TransportType.SUBWAY, 117), defaultTeam6Colors.subwayColors[0].second)
        put(key(TransportType.SUBWAY, 118), defaultTeam6Colors.subwayColors[0].second)
        put(key(TransportType.SUBWAY, 119), defaultTeam6Colors.subwayColors[3].second)
        put(key(TransportType.SUBWAY, 120), defaultTeam6Colors.subwayColors[8].second)
        put(key(TransportType.SUBWAY, 121), defaultTeam6Colors.subwayColors[12].second)
        put(key(TransportType.SUBWAY, 122), defaultTeam6Colors.subwayColors[10].second)
        put(key(TransportType.SUBWAY, 123), defaultTeam6Colors.subwayColors[11].second)
        put(key(TransportType.SUBWAY, 124), defaultTeam6Colors.subwayColors[9].second)
        put(key(TransportType.SUBWAY, 125), defaultTeam6Colors.subwayColors[23].second)
        put(key(TransportType.SUBWAY, 21), defaultTeam6Colors.subwayColors[16].second)
        put(key(TransportType.SUBWAY, 22), defaultTeam6Colors.subwayColors[17].second)
    }
    // 키 생성 헬퍼
    private fun key(type: TransportType, index: Int = 0): Int {
        return type.ordinal * 1000 + index
    }

    fun getColor(type: TransportType, subtypeIndex: Int): Color {
        val colorTableKey = key(type, subtypeIndex)
        return colorTable.get(colorTableKey) ?: defaultTeam6Colors.walkColor[0].second
    }

    fun getCourseInfoIconId(type: TransportType, subtypeIndex: Int): Int {
        val iconTableKey = key(type, subtypeIndex)
        return iconTable.get(iconTableKey)
    }
}