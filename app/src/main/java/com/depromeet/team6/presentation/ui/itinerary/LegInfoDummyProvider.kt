package com.depromeet.team6.presentation.ui.itinerary

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.depromeet.team6.domain.model.Address
import com.depromeet.team6.domain.model.course.LegInfo
import com.depromeet.team6.domain.model.course.TransportType

class LegInfoDummyProvider : PreviewParameterProvider<List<LegInfo>> {
    override val values = sequenceOf(
        listOf(
            LegInfo(
                transportType = TransportType.WALK,
                routeName = "경기 : 302",
                sectionTime = 7,
                departureDateTime = "2023-06-06T23:17:00",
                startPoint = Address(
                    name = "화서역",
                    lat = 0.1,
                    lon = 0.1,
                    address = ""
                ),
                endPoint = Address(
                    name = "지하철2호선방배역",
                    lat = 0.0,
                    lon = 0.0,
                    address = ""
                ),
                distance = 10,
                passShape = "127.02481,37.504562 127.024666,37.50452"
            ),
            LegInfo(
                transportType = TransportType.BUS,
                routeName = "경기 : 152",
                subTypeIdx = 2,
                departureDateTime = "2023-06-06T23:54:00",
                sectionTime = 27,
                startPoint = Address(
                    name = "수원 KT위즈파크",
                    lat = 0.1,
                    lon = 0.1,
                    address = ""
                ),
                endPoint = Address(
                    name = "사당역 2호선",
                    lat = 0.0,
                    lon = 0.0,
                    address = ""
                ),
                distance = 57,
                passShape = "127.025675,37.501708 127.026528,37.499994 127.026856,37.499408 127.027367,37.498353 127.027525,37.498025 127.027589,37.497892 127.027717,37.497525 127.028253,37.496306 127.028436,37.495922 127.029794,37.493072 127.029858,37.492939 127.030497,37.491664 127.031522,37.489619 127.031586,37.489483 127.032458,37.487656 127.033683,37.485086 127.033819,37.484811 127.033928,37.484633 127.034036,37.484503 127.034319,37.484181 127.034628,37.483844 127.034800,37.483678 127.037200,37.481392 127.037383,37.481169 127.037661,37.480753 127.038047,37.480053 127.038289,37.479453 127.038389,37.479136"
            ),
            LegInfo(
                transportType = TransportType.SUBWAY,
                routeName = "성수(내선)행",
                subTypeIdx = 2,
                departureDateTime = "2023-06-06T22:23:00",
                sectionTime = 17,
                startPoint = Address(
                    name = "사당역 2호선",
                    lat = 0.1,
                    lon = 0.1,
                    address = ""
                ),
                endPoint = Address(
                    name = "강남역 신분당선",
                    lat = 0.0,
                    lon = 0.0,
                    address = ""
                ),
                distance = 37,
                passShape = "127.0381,37.479004 127.03806,37.4791 127.03799,37.479313"
            ),
            LegInfo(
                transportType = TransportType.WALK,
                sectionTime = 13,
                departureDateTime = "2023-06-06T23:39:00",
                startPoint = Address(
                    name = "강남역 신분당선",
                    lat = 0.1,
                    lon = 0.1,
                    address = ""
                ),
                endPoint = Address(
                    name = "할리스 커피 강남1호점",
                    lat = 0.0,
                    lon = 0.0,
                    address = ""
                ),
                distance = 10,
                passShape = "127.03799,37.479313 127.037636,37.479263 127.037445,37.47924"
            )
        )
    )
}
