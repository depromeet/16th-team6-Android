package com.example.benchmark

import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This is an example startup benchmark.
 *
 * It navigates to the device's home screen, and launches the default activity.
 *
 * Before running this benchmark:
 * 1) switch your app's active build variant in the Studio (affects Studio runs only)
 * 2) add `<profileable android:shell="true" />` to your app's manifest, within the `<application>` tag
 *
 * Run this benchmark from Studio to see startup measurements, and captured system traces
 * for investigating your app's performance.
 */
@RunWith(AndroidJUnit4::class)
class TMapViewScrollBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()
    private val didInit = java.util.concurrent.atomic.AtomicBoolean(false)

    @Test
    fun scrollTMapView() = benchmarkRule.measureRepeated(
        packageName = "com.depromeet.team6",
        metrics = listOf(FrameTimingMetric()),
        iterations = 8, // 실제 테스트 시 8~10회 반복을 권장합니다.
        startupMode = StartupMode.WARM,
        setupBlock = {
            if (didInit.compareAndSet(false, true)) {
                // 초동 환경 테스트를 위해 앱 데이터 삭제
                device.executeShellCommand("pm clear $packageName")
                startActivityAndWait()
                // 위치 권한 부여
                device.executeShellCommand("pm grant $packageName android.permission.ACCESS_FINE_LOCATION")
                device.executeShellCommand("pm grant $packageName android.permission.ACCESS_COARSE_LOCATION")
            } else {
                // 앱의 첫 화면(로그인 화면)을 시작합니다.
                startActivityAndWait()
            }

            // 로그인 버튼 클릭
            var loginSelector = By.desc("kakao_login_button")
            // 해당 selector를 가진 UI 요소가 나타날 때까지 최대 10초간 기다립니다.
            var loginExists = device.wait(Until.hasObject(loginSelector), 3_000) // 10초 = 10000ms
            // 요소가 존재하는지 확인 후 다음 동작을 수행합니다.
            if (loginExists) {
                // 기다린 후에 다시 찾아서 클릭합니다.
                device.findObject(loginSelector).click()
            } else {
                // 시간 내에 못찾았다면 명확한 에러를 발생시켜 테스트를 중단합니다.
                // -> 자동로그인이므로 skip
//                throw AssertionError("Element with desc 'kakao_login_button' was not found on screen.")
            }

            // 지도가 준비됐음을 StartMarker로 판별
            val isMapReady = device.wait(Until.hasObject(By.desc("Start Marker")), 20_000)
            if (!isMapReady) {
                throw IllegalStateException("Map could not be loaded in time after login.")
            }
        }
    ) {
        // 2. 지도가 로딩되고 출발 마커가 표시될 때까지 최대 15초간 기다립니다.
        // TMapViewCompose 코드에서 isMapReady가 true일 때 표시되는 Image의
        // contentDescription을 사용합니다.
        val isMapReady = device.wait(Until.hasObject(By.desc("Start Marker")), 15_000)

        // 지도가 준비되지 않으면 테스트를 실패 처리합니다.
        if (!isMapReady) {
            throw IllegalStateException("Map could not be loaded in time.")
        }

        // 3. 1단계에서 설정한 contentDescription으로 지도 뷰(FrameLayout)를 찾습니다.
        val mapContainer = device.findObject(By.desc("TMapViewContainer"))
        if (!device.wait(Until.hasObject(By.desc("TMapView")), 15_000)) {
            throw IllegalStateException("Map could not be loaded in time.")
        }
        val mapView = device.findObject(By.desc("TMapView"))
        // 2. 자식 뷰가 있는지 확인합니다.
        val children = mapView.children
        if (children.isNullOrEmpty()) {
            throw AssertionError("TMapView has no children.")
        }

        device.waitForIdle()

        // 4. 지도를 아래쪽으로 3번 드래그(스크롤)하며 성능을 측정합니다.
        repeat(3) {
            // 이거 result 가 false로 뜸
//            val result = interactiveMapView.fling(Direction.DOWN)
//            Log.d("fling_result", result.toString())
            // 지도의 중앙을 기준으로 아래로 400픽셀 드래그합니다.
            mapView.swipe(Direction.LEFT, 0.5f)
            // 이거 지도 SDK에서는 Idle 잘 안잡히기 때문에 sleep으로 기다리기
//            device.waitForIdle() // UI 렌더링이 안정될 때까지 기다립니다.
            Thread.sleep(1500) // 프레임 샘플링 여유
        }
        Thread.sleep(1000)
    }
}
