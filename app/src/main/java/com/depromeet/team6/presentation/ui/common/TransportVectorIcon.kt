package com.depromeet.team6.presentation.ui.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depromeet.team6.R
import com.depromeet.team6.domain.model.course.TransportType
import com.depromeet.team6.presentation.util.Dimens
import com.depromeet.team6.ui.theme.defaultTeam6Colors

/**
 * 대중교통 아이콘을 그리는 Composable
 * type : 대중교통 타입 (WALK, BUS, SUBWAY)
 * color : 아이콘 색상 (대중교통에 해당하는 색상으로 넣어주세요)
 *          ex) defaultTeam6Colors.subwayColors[3].second
 * isMarker : 원형 background 유무.
 *            true -> 원형 background에 색상 적용
 *            false -> 대중교통 아이콘에 색상 적용
 * modifier : size는 modifier를 통해 지정해주세요.
 */
@Composable
fun TransportVectorIconComposable(
    type: TransportType,
    color: Color,
    isMarker: Boolean,
    modifier: Modifier = Modifier
) {
    // Use ImageVector
    val customVector = transportVectorBuilder(type, color, isMarker)

    Image(
        modifier = modifier,
        painter = rememberVectorPainter(customVector),
        contentDescription = "Custom Icon"
    )

    // Use Bitmap
//    val iconBitmap = TransportVectorIconBitmap(
//        context = LocalContext.current,
//        type = type,
//        fillColor = color,
//        isMarker = isMarker,
//        sizePx = 28.dp.toPx().toInt()
//    )
//    val painter = BitmapPainter(iconBitmap.asImageBitmap())
//
//    Image(
//        modifier = modifier,
//        painter = painter,
//        contentDescription = "Custom Icon"
//    )
}

fun TransportVectorIconBitmap(
    context: Context,
    type: TransportType,
    fillColor: Color,
    isMarker: Boolean,
    sizePx: Int
): Bitmap {
    val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)
    val baseSize = if (isMarker) Dimens.TransportVectorMarkerDefaultSize else Dimens.TransportVectorIconDefaultSize

    val scale = sizePx / baseSize
    canvas.save()
    canvas.scale(scale, scale) // 전체 path에 적용됨

    if (isMarker) {
        // 첫 번째 path (마커 배경)
        val strokeSvgPath = androidx.core.graphics.PathParser.createPathFromPathData(context.getString(R.string.vector_builder_node_transport_marker_outline))
        val strokeFillPaint = Paint().apply {
            style = Paint.Style.STROKE
            color = defaultTeam6Colors.white.toArgb()
            strokeWidth = 2f
            isAntiAlias = true
        }
        val backgroundSvgPath = androidx.core.graphics.PathParser.createPathFromPathData(context.getString(R.string.vector_builder_node_transport_marker_outline))
        val backgroundFillPaint = Paint().apply {
            style = Paint.Style.FILL
            color = fillColor.toArgb()
            strokeWidth = 2f
            isAntiAlias = true
        }
        canvas.drawPath(backgroundSvgPath, backgroundFillPaint)
        canvas.drawPath(strokeSvgPath, strokeFillPaint)

        // 두 번째 path (흰색)
        when (type) {
            TransportType.WALK -> {
                val walkSvgPath = androidx.core.graphics.PathParser.createPathFromPathData(context.getString(R.string.vector_builder_node_transport_walk_marker))
                walkSvgPath.fillType = android.graphics.Path.FillType.EVEN_ODD
                val walkFillPaint = Paint().apply {
                    style = Paint.Style.FILL
                    color = defaultTeam6Colors.white.toArgb()
                    isAntiAlias = true
                }
                canvas.drawPath(walkSvgPath, walkFillPaint)
            }
            TransportType.BUS -> {
                val busSvgPath = androidx.core.graphics.PathParser.createPathFromPathData(context.getString(R.string.vector_builder_node_transport_bus_marker))
                val busFillPaint = Paint().apply {
                    style = Paint.Style.FILL
                    color = defaultTeam6Colors.white.toArgb()
                    isAntiAlias = true
                }
                canvas.drawPath(busSvgPath, busFillPaint)
            }
            TransportType.SUBWAY -> {
                val subwaySvgPath = androidx.core.graphics.PathParser.createPathFromPathData(context.getString(R.string.vector_builder_node_transport_subway_marker))
                subwaySvgPath.fillType = android.graphics.Path.FillType.EVEN_ODD
                val subwayFillPaint = Paint().apply {
                    style = Paint.Style.FILL
                    color = defaultTeam6Colors.white.toArgb()
                    isAntiAlias = true
                }
                canvas.drawPath(subwaySvgPath, subwayFillPaint)
            }
        }
    } else {
        // 배경없는 대중교통 아이콘
        when (type) {
            TransportType.WALK -> {
                val walkSvgPath = androidx.core.graphics.PathParser.createPathFromPathData(context.getString(R.string.vector_builder_node_transport_walk))
                walkSvgPath.fillType = android.graphics.Path.FillType.EVEN_ODD
                val walkFillPaint = Paint().apply {
                    style = Paint.Style.FILL
                    color = fillColor.toArgb()
                    isAntiAlias = true
                }
                canvas.drawPath(walkSvgPath, walkFillPaint)
            }
            TransportType.BUS -> {
                val busSvgPath = androidx.core.graphics.PathParser.createPathFromPathData(context.getString(R.string.vector_builder_node_transport_bus))
                val busFillPaint = Paint().apply {
                    style = Paint.Style.FILL
                    color = fillColor.toArgb()
                    isAntiAlias = true
                }
                canvas.drawPath(busSvgPath, busFillPaint)
            }
            TransportType.SUBWAY -> {
                val subwaySvgPath = androidx.core.graphics.PathParser.createPathFromPathData(context.getString(R.string.vector_builder_node_transport_subway))
                subwaySvgPath.fillType = android.graphics.Path.FillType.EVEN_ODD
                val subwayFillPaint = Paint().apply {
                    style = Paint.Style.FILL
                    color = fillColor.toArgb()
                    isAntiAlias = true
                }
                canvas.drawPath(subwaySvgPath, subwayFillPaint)
            }
        }
    }
    return bitmap
}

@Composable
private fun transportVectorBuilder(
    type: TransportType,
    color: Color,
    isMarker: Boolean
): ImageVector {
    val context = LocalContext.current
    val baseSize = if (isMarker) Dimens.TransportVectorMarkerDefaultSize else Dimens.TransportVectorIconDefaultSize

    val customVector = remember {
        ImageVector.Builder(
            defaultWidth = baseSize.dp,
            defaultHeight = baseSize.dp,
            viewportWidth = baseSize,
            viewportHeight = baseSize
        ).apply {
            if (isMarker) {
                // 첫 번째 path (마커 배경)
                addPath(
                    pathData = PathParser().parsePathString(context.getString(R.string.vector_builder_node_transport_marker_outline)).toNodes(),
                    fill = SolidColor(color),
                    stroke = SolidColor(defaultTeam6Colors.white),
                    strokeLineWidth = 2f
                )
                // 두 번째 path (흰색)
                when (type) {
                    TransportType.WALK -> {
                        addPath(
                            pathData = PathParser().parsePathString(context.getString(R.string.vector_builder_node_transport_walk_marker)).toNodes(),
                            fill = SolidColor(defaultTeam6Colors.white),
                            pathFillType = PathFillType.EvenOdd
                        )
                    }
                    TransportType.BUS -> {
                        addPath(
                            pathData = PathParser().parsePathString(context.getString(R.string.vector_builder_node_transport_bus_marker)).toNodes(),
                            fill = SolidColor(defaultTeam6Colors.white)
                        )
                    }
                    TransportType.SUBWAY -> {
                        addPath(
                            pathData = PathParser().parsePathString(context.getString(R.string.vector_builder_node_transport_subway_marker)).toNodes(),
                            fill = SolidColor(defaultTeam6Colors.white),
                            pathFillType = PathFillType.EvenOdd
                        )
                    }
                }
            } else {
                // 배경없는 대중교통 아이콘
                when (type) {
                    TransportType.WALK -> {
                        addPath(
                            pathData = androidx.compose.ui.graphics.vector.PathParser().parsePathString(context.getString(R.string.vector_builder_node_transport_walk)).toNodes(),
                            fill = SolidColor(color),
                            pathFillType = PathFillType.EvenOdd
                        )
                    }
                    TransportType.BUS -> {
                        addPath(
                            pathData = PathParser().parsePathString(context.getString(R.string.vector_builder_node_transport_bus)).toNodes(),
                            fill = SolidColor(color)
                        )
                    }
                    TransportType.SUBWAY -> {
                        addPath(
                            pathData = PathParser().parsePathString(context.getString(R.string.vector_builder_node_transport_subway)).toNodes(),
                            fill = SolidColor(color),
                            pathFillType = PathFillType.EvenOdd
                        )
                    }
                }
            }
        }.build()
    }

    return customVector
}

@Preview(name = "red bus marker")
@Composable
fun preview1() {
    TransportVectorIconComposable(
        modifier = Modifier.size(28.dp),
        type = TransportType.BUS,
        color = defaultTeam6Colors.systemRed,
        isMarker = true
    )
}

@Preview(name = "green subway marker")
@Composable
fun preview2() {
    TransportVectorIconComposable(
        modifier = Modifier.size(16.dp),
        type = TransportType.SUBWAY,
        color = defaultTeam6Colors.systemGreen,
        isMarker = true
    )
}

@Preview(name = "blue bus")
@Composable
fun preview3() {
    TransportVectorIconComposable(
        modifier = Modifier.size(15.dp),
        type = TransportType.BUS,
        color = defaultTeam6Colors.subwayColors[3].second,
        isMarker = false
    )
}

@Preview(name = "orange subway")
@Composable
fun preview4() {
    TransportVectorIconComposable(
        modifier = Modifier.size(32.dp),
        type = TransportType.SUBWAY,
        color = defaultTeam6Colors.subwayColors[12].second,
        isMarker = false
    )
}

@Preview(name = "walk marker")
@Composable
fun preview5() {
    TransportVectorIconComposable(
        modifier = Modifier.size(32.dp),
        type = TransportType.WALK,
        color = defaultTeam6Colors.greySecondaryLabel,
        isMarker = true
    )
}

@Preview(name = "walk")
@Composable
fun preview6() {
    TransportVectorIconComposable(
        modifier = Modifier.size(26.dp),
        type = TransportType.WALK,
        color = defaultTeam6Colors.white,
        isMarker = false
    )
}
