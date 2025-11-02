package com.depromeet.team6.domain.usecase

class CalculateDistanceUseCase {
    /**
     * 두 위도/경도 지점 사이의 유클리드 거리의 제곱을 계산합니다 (최적화 버전).
     * 지구 곡률과 단위는 무시되며, 오직 상대적인 거리 비율 계산에만 사용해야 합니다.
     * * @param lat1 첫 번째 지점의 위도
     * @param lon1 첫 번째 지점의 경도
     * @param lat2 두 번째 지점의 위도
     * @param lon2 두 번째 지점의 경도
     * @return 두 지점 사이의 거리의 제곱 (Double)
     */
    operator fun invoke(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Float {
        // 위도(Y축) 차이
        val dy = lat2 - lat1

        // 경도(X축) 차이
        val dx = lon2 - lon1

        // 유클리드 거리 공식의 제곱: Distance² = dx² + dy²
        // 제곱근(sqrt) 연산을 생략하여 오버헤드를 최소화합니다.
        val distanceSquared = dx * dx + dy * dy

        return distanceSquared.toFloat()
    }
}
