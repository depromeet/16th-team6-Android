import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import androidx.compose.ui.graphics.Color
import androidx.core.content.res.ResourcesCompat
import com.depromeet.team6.R
import com.depromeet.team6.domain.model.course.TransportType
import com.depromeet.team6.presentation.ui.common.TransportVectorIconBitmap

/**
 * 아이콘과 텍스트를 포함한 비트맵을 생성하는 함수
 * 아이콘 크기는 TransportVectorIconBitmap 함수와 동일하게 유지됩니다.
 * 텍스트는 흰색에 검정색 테두리가 있으며, 앱의 스타일을 적용합니다.
 */
fun TransportVectorIconWithTextBitmap(
    context: Context,
    type: TransportType,
    fillColor: Color,
    isMarker: Boolean,
    iconSizePx: Int,
    name: String,
    textPadding: Int
): Bitmap {
    // 앱의 폰트 가져오기 (R.font.your_app_font로 변경 필요)
    // 없는 경우, 기본 폰트 사용
    val customTypeface = try {
        ResourcesCompat.getFont(context, R.font.pretendard_medium) // 앱에서 사용하는 폰트로 변경
    } catch (e: Exception) {
        Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    }

    // 텍스트 테두리용 Paint 객체
    val outlinePaint = Paint().apply {
        color = android.graphics.Color.BLACK
        textSize = convertComposeTextSizeToNative(context, 14f)
        typeface = customTypeface
        textAlign = Paint.Align.CENTER
        style = Paint.Style.STROKE
        strokeWidth = 5f  // 테두리 두께
        isAntiAlias = true
    }

    // 텍스트 내부 채우기용 Paint 객체
    val textPaint = Paint().apply {
        color = android.graphics.Color.WHITE
        textSize = convertComposeTextSizeToNative(context, 14f)
        typeface = customTypeface
        textAlign = Paint.Align.CENTER
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    // 텍스트 크기 측정
    val textBounds = Rect()
    textPaint.getTextBounds(name, 0, name.length, textBounds)
    val textWidth = textBounds.width()
    val textHeight = textBounds.height()

    // 먼저 기본 아이콘 비트맵 생성
    val iconBitmap = TransportVectorIconBitmap(
        context = context,
        type = type,
        fillColor = fillColor,
        isMarker = isMarker,
        sizePx = iconSizePx
    )

    // 테두리 두께를 고려한 여유 공간 추가
    val outlineOffset = outlinePaint.strokeWidth.toInt()

    // 비트맵 전체 크기 계산
    // 너비는 텍스트 너비와 아이콘 너비 중 큰 값에 여백 추가 (테두리 두께 고려)
    val totalWidth = maxOf(iconSizePx, textWidth + 40 + (outlineOffset * 2))
    val totalHeight = iconSizePx + textPadding + textHeight + (outlineOffset * 2)

    // 최종 비트맵 생성
    val bitmap = Bitmap.createBitmap(totalWidth, totalHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    // 아이콘 그리기 (상단 중앙에 위치)
    val iconLeft = (totalWidth - iconSizePx) / 2f
    canvas.drawBitmap(iconBitmap, iconLeft, 0f, null)

    // 텍스트 위치 계산 (가운데 정렬, 아이콘 아래에 위치)
    val textY = iconSizePx + textPadding + textHeight - textPaint.descent() + outlineOffset

    // 먼저 테두리 그리기
    canvas.drawText(name, totalWidth / 2f, textY, outlinePaint)

    // 그 다음 텍스트 내부 채우기
    canvas.drawText(name, totalWidth / 2f, textY, textPaint)

    return bitmap
}

/**
 * Compose 테마의 스케일 팩터를 Native 텍스트 크기로 변환
 * font 크기 계산에 사용 가능
 */
fun convertComposeTextSizeToNative(context: Context, composeSpSize: Float): Float {
    val density = context.resources.displayMetrics.density
    return composeSpSize * density
}